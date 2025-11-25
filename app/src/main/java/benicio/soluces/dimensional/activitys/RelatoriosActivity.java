package benicio.soluces.dimensional.activitys;

import static benicio.soluces.dimensional.activitys.ConfiguracoesActivity.imageToBase64;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import benicio.soluces.dimensional.R;
import benicio.soluces.dimensional.adapter.AdapterItens;
import benicio.soluces.dimensional.databinding.ActivityRelatoriosBinding;
import benicio.soluces.dimensional.model.ItemRelatorio;
import benicio.soluces.dimensional.model.ProjetoModel;
import benicio.soluces.dimensional.utils.ItemRelatorioUtil;
import benicio.soluces.dimensional.utils.KMZUtils;
import benicio.soluces.dimensional.utils.ProjetosUtils;

public class RelatoriosActivity extends AppCompatActivity {

    private ActivityRelatoriosBinding mainBinding;
    private List<ItemRelatorio> lista = new ArrayList<>();
    private RecyclerView recyclerView;
    private AdapterItens adapterItens;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private String nomeProjeto = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityRelatoriosBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getSupportActionBar().setTitle("Relatórios");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        configurarRecyclerView();
//        mainBinding.mytoken.setText(getSharedPreferences("preferencias_usuario", MODE_PRIVATE).getString("token", ""));
//        mainBinding.mytoken.setOnClickListener(v -> {
//            Toast.makeText(this, "Copiado", Toast.LENGTH_SHORT).show();
//            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//            ClipData clip = ClipData.newPlainText("token", mainBinding.mytoken.getText().toString());
//            clipboardManager.setPrimaryClip(clip);
//
//        });

        mainBinding.verLista.setOnClickListener(v -> startActivity(new Intent(this, ProjetosSalvosActivity.class)));

        preferences = getSharedPreferences("configPreferences", Context.MODE_PRIVATE);
        editor = preferences.edit();

        mainBinding.nomeField.getEditText().setText(
                preferences.getString("nomeProjeto", "")
        );

        if (preferences.getString("logoImage", null) != null) {
            byte[] decodedBytes = Base64.decode(preferences.getString("logoImage", null), Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            mainBinding.logo.setImageBitmap(decodedBitmap);
        }


        mainBinding.logo.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        });

        mainBinding.btnCompartilhar.setOnClickListener(v -> {
            nomeProjeto = mainBinding.nomeField.getEditText().getText().toString().replace(" ", "_").replace("\n", "_");

            if (lista.isEmpty()) {
                Toast.makeText(this, "Nenhuma árvore para o relatório", Toast.LENGTH_SHORT).show();
            } else {
                if (nomeProjeto.isEmpty()) {
                    Toast.makeText(this, "Nome do projeto vazio.", Toast.LENGTH_SHORT).show();
                } else {
                    gerarRelatorio();
                }
            }

        });

        mainBinding.btnKmz.setOnClickListener(v -> {
            Toast.makeText(this, "Gerando kmz", Toast.LENGTH_SHORT).show();
            nomeProjeto = mainBinding.nomeField.getEditText().getText().toString().replace(" ", "_").replace("\n", "_");

            if (!nomeProjeto.isEmpty()) {
                KMZUtils.gerarArquivoKML(RelatoriosActivity.this, lista, nomeProjeto);
            } else {
                Toast.makeText(this, "Nome do projeto vazio.", Toast.LENGTH_SHORT).show();
            }

        });

        mainBinding.checkMarcarTodos.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (adapterItens != null) {
                adapterItens.toggleSelectAll(isChecked);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void configurarRecyclerView() {
        recyclerView = mainBinding.recyclerArvores;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        lista.addAll(ItemRelatorioUtil.returnLista(this));

        if (!lista.isEmpty()) {
            Collections.reverse(lista);
            mainBinding.textAvisoLista.setText("Informações de Registros:");
        }

        adapterItens = new AdapterItens(this, lista);
        recyclerView.setAdapter(adapterItens);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                gerarRelatorio();
            }
        }
    }


    @SuppressLint("SimpleDateFormat")
    public void gerarRelatorio() {

        int pageHeight = 2000;
        int pagewidth = 1414;
        int margin = 80;
        int headerHeight = 220;
        int bottomMargin = 80;

        PdfDocument pdfDocument = new PdfDocument();

        // Paints
        Paint bgPaint = new Paint();
        Paint headerBarPaint = new Paint();
        Paint title = new Paint();
        Paint subTitle = new Paint();
        Paint content = new Paint();
        Paint sectionTitle = new Paint();
        Paint linePaint = new Paint();

        bgPaint.setAntiAlias(true);
        headerBarPaint.setAntiAlias(true);
        title.setAntiAlias(true);
        subTitle.setAntiAlias(true);
        content.setAntiAlias(true);
        sectionTitle.setAntiAlias(true);
        linePaint.setAntiAlias(true);

        bgPaint.setColor(android.graphics.Color.WHITE);
        headerBarPaint.setColor(android.graphics.Color.parseColor("#F1F5F9"));

        title.setTextSize(40);
        title.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        title.setColor(android.graphics.Color.parseColor("#111827"));

        subTitle.setTextSize(26);
        subTitle.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        subTitle.setColor(android.graphics.Color.parseColor("#4B5563"));

        content.setTextSize(26);
        content.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        content.setColor(android.graphics.Color.parseColor("#111827"));

        sectionTitle.setTextSize(30);
        sectionTitle.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        sectionTitle.setColor(android.graphics.Color.parseColor("#111827"));

        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(2f);
        linePaint.setColor(android.graphics.Color.parseColor("#E5E7EB"));


        // Pega apenas os itens selecionados no adapter
        List<ItemRelatorio> fonte;
        if (adapterItens != null) {
            fonte = adapterItens.getSelectedItems();
        } else {
            fonte = new ArrayList<>(lista); // fallback (caso algo dê errado)
        }

        if (fonte.isEmpty()) {
            Toast.makeText(this, "Nenhuma árvore selecionada para o relatório.", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(this, "Gerando Relatório...", Toast.LENGTH_SHORT).show();

        int globalPageNum = 1; // contador de páginas no PDF

        for (ItemRelatorio item : fonte) {

            // --------- PREPARO DOS DADOS ---------
            String dadosGps = item.getDadosGps() != null ? item.getDadosGps() : "";
            String[] gpsLines = dadosGps.split("\n");

            String dadosTora = item.getDadosTora() != null ? item.getDadosTora() : "";
            String[] toraLines = dadosTora.split("\n");

            String dadosVolume = item.getDadosVolume() != null ? item.getDadosVolume() : "";
            String[] volumeLines = dadosVolume.split("\n");

            String metodo = gpsLines.length > 5 ? gpsLines[5] : "";

            String operador = gpsLines.length > 4
                    ? gpsLines[4].replace("Operador: ", "")
                    : "";

            String endereco = "";
            if (gpsLines.length > 1) {
                endereco = gpsLines[0] + ", " + gpsLines[1];
            } else if (gpsLines.length == 1) {
                endereco = gpsLines[0];
            }

            String latitude = "";
            String longitude = "";
            if (gpsLines.length > 2) {
                String[] latLonParts = gpsLines[2].split(" ");
                if (latLonParts.length > 1) {
                    latitude = latLonParts[1];
                }
                if (latLonParts.length > 3) {
                    longitude = latLonParts[3];
                }
            }

            String alturaTotal = "";
            if (toraLines.length > 0) {
                String ultimaLinha = toraLines[toraLines.length - 1];
                alturaTotal = ultimaLinha.replace("Altura total: ", "");
            }

            String volumeTotal = "";
            for (String linha : volumeLines) {
                if (linha.contains("Volume total: ")) {
                    volumeTotal = linha.replace("Volume total: ", "");
                    break;
                }
            }

            String data = "";
            if (toraLines.length > 0) {
                String[] partes = toraLines[0].split(" ");
                if (partes.length > 3) {
                    data = partes[3];
                }
            }

            String obs = "";
            if (mainBinding.observacoesField.getEditText() != null) {
                obs = mainBinding.observacoesField.getEditText()
                        .getText()
                        .toString()
                        .replace("\n", " ");
            }

            // ===== CONTROLE DE PÁGINA =====
            PdfDocument.Page page;
            Canvas canvas;
            int currentY;
            int currentPageNum;

            String nomeProjetoLimpo = nomeProjeto.replace("_", " ");

            // primeira página deste item
            currentPageNum = globalPageNum++;
            page = startNewPage(
                    pdfDocument,
                    pagewidth,
                    pageHeight,
                    currentPageNum,
                    bgPaint,
                    headerBarPaint,
                    title,
                    subTitle,
                    linePaint,
                    margin,
                    headerHeight,
                    nomeProjetoLimpo
            );
            canvas = page.getCanvas();
            currentY = headerHeight + 60;

            // --------- INFORMAÇÕES GERAIS ----------
            canvas.drawText("Informações gerais", margin, currentY, sectionTitle);
            currentY += 40;

            canvas.drawText("Data: " + data, margin, currentY, content);
            currentY += 32;
            canvas.drawText("Operador: " + operador, margin, currentY, content);
            currentY += 32;
            canvas.drawText("Método: " + metodo, margin, currentY, content);
            currentY += 32;
            canvas.drawText("Endereço: " + endereco, margin, currentY, content);
            currentY += 32;
            canvas.drawText("Latitude: " + latitude, margin, currentY, content);
            currentY += 32;
            canvas.drawText("Longitude: " + longitude, margin, currentY, content);
            currentY += 32;
            canvas.drawText("Altura total: " + alturaTotal, margin, currentY, content);
            currentY += 32;
            canvas.drawText("Volume total: " + volumeTotal, margin, currentY, content);
            currentY += 40;

            canvas.drawLine(margin, currentY, pagewidth - margin, currentY, linePaint);
            currentY += 50;

            // --------- OBSERVAÇÕES ----------
            int estimatedObsHeight = 40 + 3 * (int) content.getTextSize();
            if (currentY + estimatedObsHeight > pageHeight - bottomMargin) {
                // fechar página atual
                String pageNumberText = "Página " + currentPageNum;
                float textWidth = content.measureText(pageNumberText);
                canvas.drawText(pageNumberText, pagewidth - margin - textWidth, pageHeight - 40, content);
                pdfDocument.finishPage(page);

                // nova página
                currentPageNum = globalPageNum++;
                page = startNewPage(pdfDocument, pagewidth, pageHeight, currentPageNum,
                        bgPaint, headerBarPaint, title, subTitle,
                        linePaint, margin, headerHeight, nomeProjetoLimpo);
                canvas = page.getCanvas();
                currentY = headerHeight + 60;
            }

            canvas.drawText("Observações", margin, currentY, sectionTitle);
            currentY += 40;

            float obsMaxWidth = pagewidth - (margin * 2);
            currentY = drawMultiLineText(canvas, obs, margin, currentY, obsMaxWidth, content, 10);
            currentY += 40;

            canvas.drawLine(margin, currentY, pagewidth - margin, currentY, linePaint);
            currentY += 50;

            // --------- INFORMAÇÕES DE VOLUME ----------
            int blocoAltura = 420;

            if (currentY + 40 + 10 + blocoAltura > pageHeight - bottomMargin) {
                // fecha página
                String pageNumberText = "Página " + currentPageNum;
                float textWidth = content.measureText(pageNumberText);
                canvas.drawText(pageNumberText, pagewidth - margin - textWidth, pageHeight - 40, content);
                pdfDocument.finishPage(page);

                // nova página
                currentPageNum = globalPageNum++;
                page = startNewPage(pdfDocument, pagewidth, pageHeight, currentPageNum,
                        bgPaint, headerBarPaint, title, subTitle,
                        linePaint, margin, headerHeight, nomeProjetoLimpo);
                canvas = page.getCanvas();
                currentY = headerHeight + 60;
            }

            canvas.drawText("Informações de volume", margin, currentY, sectionTitle);
            currentY += 30;

            java.util.List<String> headerVolumeLines = new java.util.ArrayList<>();
            java.util.List<String> dataVolumeLines = new java.util.ArrayList<>();

            for (int idx = 0; idx < volumeLines.length; idx++) {
                String linha = volumeLines[idx];
                if (linha.trim().isEmpty()) continue;

                if (idx < 2) {
                    headerVolumeLines.add(linha);
                } else {
                    dataVolumeLines.add(linha);
                }
            }

            int dataIndex = 0;
            boolean firstBlock = true;

            while (dataIndex < dataVolumeLines.size() || (firstBlock && !headerVolumeLines.isEmpty())) {

                // se não cabe mais um bloco nesta página, cria nova página
                if (currentY + 10 + blocoAltura > pageHeight - bottomMargin) {
                    String pageNumberText = "Página " + currentPageNum;
                    float textWidth = content.measureText(pageNumberText);
                    canvas.drawText(pageNumberText, pagewidth - margin - textWidth, pageHeight - 40, content);
                    pdfDocument.finishPage(page);

                    currentPageNum = globalPageNum++;
                    page = startNewPage(pdfDocument, pagewidth, pageHeight, currentPageNum,
                            bgPaint, headerBarPaint, title, subTitle,
                            linePaint, margin, headerHeight, nomeProjetoLimpo);
                    canvas = page.getCanvas();
                    currentY = headerHeight + 60;

                    canvas.drawText("Informações de volume (cont.)", margin, currentY, sectionTitle);
                    currentY += 30;
                }

                int tableTop = currentY + 10;
                int tableLeft = margin;
                int tableRight = pagewidth - margin;
                int tableBottom = tableTop + blocoAltura;

                canvas.drawRoundRect(
                        tableLeft,
                        tableTop,
                        tableRight,
                        tableBottom,
                        16,
                        16,
                        linePaint
                );

                int linhaY = tableTop + 40;

                // cabeçalho só no primeiro bloco
                if (firstBlock && !headerVolumeLines.isEmpty()) {
                    for (String h : headerVolumeLines) {
                        canvas.drawText(h, tableLeft + 20, linhaY, content);
                        linhaY += 30;
                    }
                    linhaY += 10;
                }

                int col1X = tableLeft + 20;
                int col2X = tableLeft + (tableRight - tableLeft) / 2 + 10;
                int colStartY = linhaY;
                int lineHeight = 30;
                int maxYForColumns = tableBottom - 20;

                int x = col1X;
                linhaY = colStartY;

                while (dataIndex < dataVolumeLines.size()) {
                    if (linhaY > maxYForColumns) {
                        if (x == col1X) {
                            x = col2X;
                            linhaY = colStartY;
                        } else {
                            break; // bloco cheio
                        }
                    }

                    String dado = dataVolumeLines.get(dataIndex);
                    canvas.drawText(dado, x, linhaY, content);
                    linhaY += lineHeight;
                    dataIndex++;
                }

                currentY = tableBottom + 40;
                firstBlock = false;
            }

            // --------- REGISTRO FOTOGRÁFICO ----------
            int fotoHeight = 400;
            int neededForFoto = 30 + (item.getImagemArvore() != null ? fotoHeight + 20 : 0);

            if (currentY + neededForFoto > pageHeight - bottomMargin) {
                String pageNumberText = "Página " + currentPageNum;
                float textWidth = content.measureText(pageNumberText);
                canvas.drawText(pageNumberText, pagewidth - margin - textWidth, pageHeight - 40, content);
                pdfDocument.finishPage(page);

                currentPageNum = globalPageNum++;
                page = startNewPage(pdfDocument, pagewidth, pageHeight, currentPageNum,
                        bgPaint, headerBarPaint, title, subTitle,
                        linePaint, margin, headerHeight, nomeProjetoLimpo);
                canvas = page.getCanvas();
                currentY = headerHeight + 60;
            }

            canvas.drawText("Registro fotográfico", margin, currentY, sectionTitle);
            currentY += 30;

            if (item.getImagemArvore() != null) {
                Bitmap selectedImageBitmap = getBitmapFromUri(Uri.parse(item.getImagemArvore()));
                if (selectedImageBitmap != null) {
                    int fotoWidth = pagewidth - (margin * 2);
                    Bitmap fotoScaled = Bitmap.createScaledBitmap(selectedImageBitmap, fotoWidth, fotoHeight, false);
                    canvas.drawBitmap(fotoScaled, margin, currentY, null);
                    currentY += fotoHeight + 20;
                }
            }

            // fecha última página deste item
            String pageNumberText = "Página " + currentPageNum;
            float textWidth = content.measureText(pageNumberText);
            canvas.drawText(pageNumberText, pagewidth - margin - textWidth, pageHeight - 40, content);
            pdfDocument.finishPage(page);
        }

        // Diretório para salvar o PDF
        File documentsFolder = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "dendro_smart");
        if (!documentsFolder.exists()) {
            documentsFolder.mkdirs();
        }

        File pdfFile = new File(documentsFolder, "Relatorio_" + nomeProjeto + ".pdf");

        try {
            pdfDocument.writeTo(Files.newOutputStream(pdfFile.toPath()));

            Uri fileUri = FileProvider.getUriForFile(
                    this,
                    getApplicationContext().getPackageName() + ".provider",
                    pdfFile
            );

            ProjetoModel novoProjeto = new ProjetoModel(
                    "Relatorio_" + nomeProjeto + ".pdf",
                    new SimpleDateFormat("dd/MM/yyyy").format(new Date()),
                    fileUri.toString()
            );

            List<ProjetoModel> existente = ProjetosUtils.returnList(this);
            existente.add(novoProjeto);
            ProjetosUtils.saveList(existente, this);

            sharePdf(pdfFile);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Erro ao gerar PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            pdfDocument.close();
        }
    }

    /**
     * Desenha texto com quebra de linha simples baseado na largura máxima.
     */
    private int drawMultiLineText(Canvas canvas,
                                  String text,
                                  float x,
                                  int startY,
                                  float maxWidth,
                                  Paint paint,
                                  float lineSpacing) {

        if (text == null || text.trim().isEmpty()) {
            return startY;
        }

        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int y = startY;

        for (String word : words) {
            String testLine = line.length() == 0 ? word : line + " " + word;
            float testWidth = paint.measureText(testLine);

            if (testWidth > maxWidth) {
                // desenha a linha atual
                canvas.drawText(line.toString(), x, y, paint);
                // nova linha começa com a palavra atual
                line = new StringBuilder(word);
                y += paint.getTextSize() + lineSpacing;
            } else {
                line = new StringBuilder(testLine);
            }
        }

        // desenha a última linha
        if (line.length() > 0) {
            canvas.drawText(line.toString(), x, y, paint);
            y += paint.getTextSize() + lineSpacing;
        }

        return y;
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            return BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void sharePdf(File pdfFile) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("application/pdf");

        // Adicione o arquivo PDF como um anexo
        Uri pdfUri = FileProvider.getUriForFile(this, "benicio.soluces.dimensional.provider", pdfFile);
        shareIntent.putExtra(Intent.EXTRA_STREAM, pdfUri);

        // Adicione um texto de compartilhamento opcional
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Compartilhando relatório em PDF");

        // Abre a janela de compartilhamento
        startActivity(Intent.createChooser(shareIntent, "Compartilhar relatório via"));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            editor.putString("logoImage", imageToBase64(selectedImageUri, getApplicationContext()));
            editor.apply();


            byte[] decodedBytes = Base64.decode(preferences.getString("logoImage", null), Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            mainBinding.logo.setImageBitmap(decodedBitmap);
        }
    }

    /** Inicia uma nova página com cabeçalho padrão e retorna o Page. */
    private PdfDocument.Page startNewPage(PdfDocument pdfDocument,
                                          int pagewidth,
                                          int pageHeight,
                                          int pageNum,
                                          Paint bgPaint,
                                          Paint headerBarPaint,
                                          Paint title,
                                          Paint subTitle,
                                          Paint linePaint,
                                          int margin,
                                          int headerHeight,
                                          String nomeProjetoLimpo) {

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo
                .Builder(pagewidth, pageHeight, pageNum)
                .create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // fundo
        canvas.drawRect(0, 0, pagewidth, pageHeight, bgPaint);
        // faixa de cabeçalho
        canvas.drawRect(0, 0, pagewidth, headerHeight, headerBarPaint);

        // logo
        if (preferences.getString("logoImage", null) != null) {
            byte[] decodedBytes = Base64.decode(preferences.getString("logoImage", null), Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            Bitmap logoScaledBitmap = Bitmap.createScaledBitmap(decodedBitmap, 140, 140, false);
            canvas.drawBitmap(logoScaledBitmap, margin, 40, null);
        }

        // título e subtítulo
        canvas.drawText(nomeProjetoLimpo, margin + 170, 90, title);
        canvas.drawText("Relatório de Inventário Florestal", margin + 170, 140, subTitle);

        // linha final do cabeçalho
        canvas.drawLine(margin, headerHeight, pagewidth - margin, headerHeight, linePaint);

        return page;
    }



}