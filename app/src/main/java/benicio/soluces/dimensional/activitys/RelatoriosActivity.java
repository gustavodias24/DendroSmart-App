package benicio.soluces.dimensional.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import benicio.soluces.dimensional.R;
import benicio.soluces.dimensional.adapter.AdapterItens;
import benicio.soluces.dimensional.databinding.ActivityRelatoriosBinding;
import benicio.soluces.dimensional.databinding.ActivitySelecionarMetodoBinding;
import benicio.soluces.dimensional.model.ItemRelatorio;
import benicio.soluces.dimensional.utils.ItemRelatorioUtil;

public class RelatoriosActivity extends AppCompatActivity {

    private ActivityRelatoriosBinding mainBinding;
    private List<ItemRelatorio> lista = new ArrayList<>();
    private RecyclerView recyclerView;
    private AdapterItens adapterItens;

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
    }

    private void configurarRecyclerView() {
        recyclerView = mainBinding.recyclerArvores;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        lista.addAll(ItemRelatorioUtil.returnLista(this));

        if( !lista.isEmpty()){mainBinding.textAvisoLista.setVisibility(View.GONE);}

        adapterItens = new AdapterItens(this, lista);
        recyclerView.setAdapter(adapterItens);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if ( item.getItemId() == android.R.id.home){finish();}
        return super.onOptionsItemSelected(item);
    }

    public void gerarRelatório(View view){
        Bitmap bmpTemplate = BitmapFactory.decodeResource(getResources(), R.raw.templaterelatorio);
        Bitmap scaledbmpTemplate = Bitmap.createScaledBitmap(bmpTemplate, 792, 1120, false);

        int pageHeight = 1120;
        int pagewidth = 792;

        PdfDocument pdfDocument = new PdfDocument();

        Paint paint = new Paint();
        Paint title = new Paint();

        PdfDocument.PageInfo mypageInfo = new PdfDocument.PageInfo.Builder(pagewidth, pageHeight, 1).create();
        PdfDocument.Page myPage = pdfDocument.startPage(mypageInfo);

        Canvas canvas = myPage.getCanvas();
        canvas.drawBitmap(scaledbmpTemplate, 1, 1, paint);

        SharedPreferences preferences = getSharedPreferences("configPreferences", Context.MODE_PRIVATE);
        String logoEmpresaString = preferences.getString("logoImage", "");

        if ( !logoEmpresaString.isEmpty() ){
            byte[] decodedBytes = Base64.decode(logoEmpresaString, Base64.DEFAULT);
            Bitmap logoEmpresabmp = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            Bitmap logoEpresaScaledbmp = Bitmap.createScaledBitmap(logoEmpresabmp, 104, 104, false);
            canvas.drawBitmap(logoEpresaScaledbmp, 670, 25, paint);
        }

        title.setTextSize(25);
        title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        title.setColor(ContextCompat.getColor(this, R.color.black));

        String nomeProjeto = mainBinding.nomeField.getEditText().getText().toString();
        canvas.drawText(nomeProjeto, 328, 44, title);

        title.setTextSize(10);
        title.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));

        int startX = 92;
        int startY = 98;

        int paddginY = 14;
        int paddginX = 200;

        for ( ItemRelatorio item : lista){
            int comecouY = startY;
            startX = 92;

            for ( String linha : item.getDadosTora().split("\n")){
                canvas.drawText(linha, startX, startY, title);
                startY += paddginY;
            }

            startY = comecouY;
            startX += paddginX;

            for ( String linha : item.getDadosGps().split("\n")){
                canvas.drawText(linha, startX, startY, title);
                startY += paddginY;
            }

            startY = comecouY;
            startX += paddginX;

            for ( String linha : item.getDadosVolume().split("\n")){
                canvas.drawText(linha, startX, startY, title);
                startY += paddginY;
            }

            startY += (paddginY * 2);
        }

        pdfDocument.finishPage(myPage);

        // Cria um arquivo temporário para armazenar o PDF
        File pdfFile = createPdfFile();

        try {
            // Salva o PDF no arquivo temporário
            pdfDocument.writeTo(new FileOutputStream(pdfFile));

            // Abre a opção de compartilhamento
            sharePdf(pdfFile);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            pdfDocument.close();
        }

    }

    private File createPdfFile() {
        File pdfFolder = new File(getExternalFilesDir(null), "pdfs");
        if (!pdfFolder.exists()) {
            pdfFolder.mkdirs();
        }

        return new File(pdfFolder, "relatorio.pdf");
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
}