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
import benicio.soluces.dimensional.utils.ItemRelatorioUtil;
import benicio.soluces.dimensional.utils.KMZUtils;

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

        preferences = getSharedPreferences("configPreferences", Context.MODE_PRIVATE);
        editor = preferences.edit();

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
                    Toast.makeText(this, "AGUARDE...", Toast.LENGTH_SHORT).show();
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
        // Carregar a imagem do template
        Bitmap bmpTemplate = BitmapFactory.decodeResource(getResources(), R.drawable.frentepdf);
        Bitmap scaledbmpTemplate = Bitmap.createScaledBitmap(bmpTemplate, 1414, 2000, false);

        int pageHeight = 2000;
        int pagewidth = 1414;

        PdfDocument pdfDocument = new PdfDocument();

        Paint paint = new Paint();
        Paint title = new Paint();

        for (int i = 0; i < lista.size(); i++) {

            ItemRelatorio item = lista.get(i);

            PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, i + 1).create();
            PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);

            Canvas canvas = myPage.getCanvas();
            canvas.drawBitmap(scaledbmpTemplate, 0, 0, paint);


            if (preferences.getString("logoImage", null) != null) {
                byte[] decodedBytes = Base64.decode(preferences.getString("logoImage", null), Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                // Adicionar a imagem decodificada ao PDF em coordenadas específicas
                Bitmap logoScaledBitmap = Bitmap.createScaledBitmap(decodedBitmap, 160, 155, false);
                canvas.drawBitmap(logoScaledBitmap, 75, 28, paint);
            }

            title.setTextSize(32);
            title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            title.setColor(ContextCompat.getColor(this, R.color.black));

            // Nome projeto
            canvas.drawText(nomeProjeto.replace("_", " "), 710, 195, title);

            // data
            canvas.drawText(item.getDadosTora().split("\n")[0].split(" ")[3], 191, 259, title);

            // metodo
            canvas.drawText(item.getDadosGps().split("\n")[5], 871, 260, title);

            // operador
            canvas.drawText(item.getDadosGps().split("\n")[4].replace("Operador: ", ""), 551, 402, title);

            // endereco
            canvas.drawText(item.getDadosGps().split("\n")[0] + "," + item.getDadosGps().split("\n")[1], 305, 467, title);

            // latitude
            canvas.drawText(item.getDadosGps().split("\n")[2].split(" ")[1], 277, 531, title);

            // longitue
            canvas.drawText(item.getDadosGps().split("\n")[2].split(" ")[3], 1010, 531, title);

            // altura total
            int qtd_total = item.getDadosTora().split("\n").length;
            canvas.drawText(item.getDadosTora().split("\n")[qtd_total - 1].replace("Altura total: ", ""), 1042, 608, title);

            // volume total
            String volumeTotal = "";
            for (String linha : item.getDadosVolume().split("\n")) {

                if (linha.contains("Volume total: ")) {
                    volumeTotal = linha.replace("Volume total: ", "");
                }
            }
            canvas.drawText(volumeTotal, 400, 608, title);


            // obs
            canvas.drawText(mainBinding.observacoesField.getEditText().getText().toString().replace("\n", " "), 388, 332, title);


            // informacoes tora
            int linha = 740;
            for (String dado : item.getDadosVolume().split("\n")) {
                canvas.drawText(dado, 70, linha, title);
                linha += 32;
            }


            // foto da arvore
            if (item.getImagemArvore() != null) {
                Bitmap selectedImageBitmap = getBitmapFromUri(Uri.parse(item.getImagemArvore()));
                if (selectedImageBitmap != null) {
                    Bitmap logoScaledBitmap = Bitmap.createScaledBitmap(selectedImageBitmap, 1200, 450, false);
                    canvas.drawBitmap(logoScaledBitmap, 97, 1512, paint);
                }
            }
            pdfDocument.finishPage(myPage);
        }


        // Diretório para salvar o PDF
        File documentsFolder = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "dendro_smart");
        if (!documentsFolder.exists()) {
            documentsFolder.mkdirs();
        }

        // Cria um arquivo temporário para armazenar o PDF
        File pdfFile = new File(documentsFolder, "Relatorio_" + nomeProjeto + ".pdf");

        try {
            // Salva o PDF no arquivo temporário
            pdfDocument.writeTo(Files.newOutputStream(pdfFile.toPath()));
            sharePdf(pdfFile);
        } catch (IOException e) {
            Log.d("mayara", "gerarRelatorio: " + e.getMessage());
            e.printStackTrace();
        } finally {
            pdfDocument.close();
        }
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


}