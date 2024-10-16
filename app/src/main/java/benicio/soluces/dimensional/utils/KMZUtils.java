package benicio.soluces.dimensional.utils;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import benicio.soluces.dimensional.model.ItemRelatorio;
import benicio.soluces.dimensional.model.ProjetoModel;
import benicio.soluces.dimensional.model.UploadResponse;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KMZUtils {
    @SuppressLint("SimpleDateFormat")
    public static void gerarArquivoKML(
            Activity context,
            List<ItemRelatorio> itensRelatorio,
            String nomeProjeto
    ) {

        StringBuilder kmlBuilder = new StringBuilder();
        kmlBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        kmlBuilder.append("<kml xmlns=\"http://www.opengis.net/kml/2.2\" xmlns:gx=\"http://www.google.com/kml/ext/2.2\" xmlns:kml=\"http://www.opengis.net/kml/2.2\" xmlns:atom=\"http://www.w3.org/2005/Atom\">\n");
        kmlBuilder.append("<Document>\n");

        kmlBuilder.append("<name>" + nomeProjeto.replace(" ", "_") + "</name>");
        kmlBuilder.append("\t<description>Projeto criado no dia " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + "</description>\n");

        // Para cada ponto do projeto
        for (ItemRelatorio itemRelatorio : itensRelatorio) {
            // icone do ponto

            String placeMarkId = UUID.randomUUID().toString().replace("-", "");
            String styleMapId = UUID.randomUUID().toString().replace("-", "");
            String highlightId = UUID.randomUUID().toString().replace("-", "");
            String normalId = UUID.randomUUID().toString().replace("-", "");

            kmlBuilder.append(returnCascadingStyle(highlightId, normalId));
            kmlBuilder.append(returnStyleMap(normalId, highlightId, styleMapId));

            kmlBuilder.append(returnPlacemark(
                    placeMarkId,
                    styleMapId,
                    "Árvore",
                    itemRelatorio.getDadosVolume() + "\n" + itemRelatorio.getDadosTora(),
                    itemRelatorio.getLongitude(),
                    itemRelatorio.getLatitude()
            ));
        }

        kmlBuilder.append("</Document>\n");
        kmlBuilder.append("</kml>\n");

        // Obtém o diretório "Documentos" no armazenamento externo
        File documentosDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

        // Crie a pasta "KaizenProjetos" dentro do diretório "Documentos"
        File kaizenProjetosDir = new File(documentosDir, "FOTO MAPA");
        if (!kaizenProjetosDir.exists()) {
            kaizenProjetosDir.mkdirs();
        }


        // Salvar o arquivo KML no armazenamento externo
        File kmlFile = new File(kaizenProjetosDir, nomeProjeto + ".kml");
        try {
            FileOutputStream fos = new FileOutputStream(kmlFile);
            fos.write(kmlBuilder.toString().getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Comprimir o arquivo KML em um arquivo KMZ
        File kmzFile = new File(kaizenProjetosDir, nomeProjeto + ".kmz");
        try {
            FileOutputStream fos = new FileOutputStream(kmzFile);
            ZipOutputStream zos = new ZipOutputStream(fos);

            // Adicionar o arquivo KML ao arquivo KMZ
            ZipEntry entry = new ZipEntry("marker.kml");
            zos.putNextEntry(entry);
            byte[] kmlBytes = kmlBuilder.toString().getBytes();
            zos.write(kmlBytes, 0, kmlBytes.length);
            zos.closeEntry();

            zos.close();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Abra o arquivo KMZ no aplicativo Google Earth
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW);

            Uri uri = FileProvider.getUriForFile(Objects.requireNonNull(context),
                    "benicio.soluces.dimensional.provider", kmzFile);

            List<ProjetoModel> existente = ProjetosUtils.returnList(context);

            ProjetoModel novoProjeto = new ProjetoModel(
                    nomeProjeto + ".kmz",
                    new SimpleDateFormat("dd/MM/yyyy").format(new Date()),
                    uri.toString()
            );

            uploadApi(context, kmzFile, nomeProjeto);

            existente.add(novoProjeto);
            ProjetosUtils.saveList(existente, context);

            intent.setDataAndType(uri, "application/vnd.google-earth.kmz");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "Nenhum aplicativo encontrado para abrir este arquivo.", Toast.LENGTH_SHORT).show();
        }

    }

    public static void uploadApi(Context context, File kmzFile, String nomeProjeto) {
        RequestBody fileRequestBody = RequestBody.create(MediaType.parse("application/octet-stream"), kmzFile);
        SharedPreferences sharedPreferences = context.getSharedPreferences("preferencias_usuario", MODE_PRIVATE);
        RequestBody idRequestBody = RequestBody.create(MediaType.parse("text/plain"), sharedPreferences.getString("token", ""));
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", nomeProjeto + ".kmz", fileRequestBody);
        RetrofitKmz.createServiceKmz(
                RetrofitKmz.createRetrofitKmz()
        ).uploadKmz(idRequestBody, filePart).enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                if (response.isSuccessful()) {
                    UploadResponse uploadResponse = response.body();
                    Log.d("API", uploadResponse.getMessage());
                }
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                Log.e("mayara", "Erro: " + t.getMessage());
                uploadApi(context, kmzFile, nomeProjeto);
            }
        });
    }

    public static String returnCascadingStyle(String highlightId, String normalId) {
        StringBuilder xmlStringBuilder = new StringBuilder();

        xmlStringBuilder.append("<gx:CascadingStyle kml:id=\"__managed_style_" + highlightId + "\">\n");
        xmlStringBuilder.append("    <styleUrl>https://earth.google.com/balloon_components/base/1.0.26.0/card_template.kml#main</styleUrl>\n");
        xmlStringBuilder.append("    <Style>\n");
        xmlStringBuilder.append("        <IconStyle>\n");
        xmlStringBuilder.append("            <scale>1.2</scale>\n");
        xmlStringBuilder.append("            <Icon>\n");
        xmlStringBuilder.append("                <href>https://i.imgur.com/7hhYXgu.png</href>");
        xmlStringBuilder.append("            </Icon>\n");
        xmlStringBuilder.append("        </IconStyle>\n");
        xmlStringBuilder.append("        <LabelStyle>\n");
        xmlStringBuilder.append("        </LabelStyle>\n");
        xmlStringBuilder.append("        <LineStyle>\n");
        xmlStringBuilder.append("            <color>ff2dc0fb</color>\n");
        xmlStringBuilder.append("            <width>6</width>\n");
        xmlStringBuilder.append("        </LineStyle>\n");
        xmlStringBuilder.append("        <PolyStyle>\n");
        xmlStringBuilder.append("            <color>40ffffff</color>\n");
        xmlStringBuilder.append("        </PolyStyle>\n");
        xmlStringBuilder.append("        <BalloonStyle>\n");
        xmlStringBuilder.append("        </BalloonStyle>\n");
        xmlStringBuilder.append("    </Style>\n");
        xmlStringBuilder.append("</gx:CascadingStyle>");

        xmlStringBuilder.append("<gx:CascadingStyle kml:id=\"__managed_style_" + normalId + "\">\n");
        xmlStringBuilder.append("    <styleUrl>https://earth.google.com/balloon_components/base/1.0.26.0/card_template.kml#main</styleUrl>\n");
        xmlStringBuilder.append("    <Style>\n");
        xmlStringBuilder.append("        <IconStyle>\n");
        xmlStringBuilder.append("            <Icon>\n");
        xmlStringBuilder.append("                 <href>https://i.imgur.com/7hhYXgu.png</href>");
        xmlStringBuilder.append("            </Icon>\n");
        xmlStringBuilder.append("        </IconStyle>\n");
        xmlStringBuilder.append("        <LabelStyle>\n");
        xmlStringBuilder.append("        </LabelStyle>\n");
        xmlStringBuilder.append("        <LineStyle>\n");
        xmlStringBuilder.append("            <color>ff2dc0fb</color>\n");
        xmlStringBuilder.append("            <width>4</width>\n");
        xmlStringBuilder.append("        </LineStyle>\n");
        xmlStringBuilder.append("        <PolyStyle>\n");
        xmlStringBuilder.append("            <color>40ffffff</color>\n");
        xmlStringBuilder.append("        </PolyStyle>\n");
        xmlStringBuilder.append("        <BalloonStyle>\n");
        xmlStringBuilder.append("        </BalloonStyle>\n");
        xmlStringBuilder.append("    </Style>\n");
        xmlStringBuilder.append("</gx:CascadingStyle>\n");


        return xmlStringBuilder.toString();
    }

    public static String returnStyleMap(String normal, String highlight, String stylemap) {
        StringBuilder xmlStringBuilder = new StringBuilder();

        xmlStringBuilder.append("<StyleMap id=\"__managed_style_" + stylemap + "\">\n");
        xmlStringBuilder.append("    <Pair>\n");
        xmlStringBuilder.append("        <key>normal</key>\n");
        xmlStringBuilder.append("        <styleUrl>#__managed_style_" + normal + "</styleUrl>\n");
        xmlStringBuilder.append("    </Pair>\n");
        xmlStringBuilder.append("    <Pair>\n");
        xmlStringBuilder.append("        <key>highlight</key>\n");
        xmlStringBuilder.append("        <styleUrl>#__managed_style_" + highlight + "</styleUrl>\n");
        xmlStringBuilder.append("    </Pair>\n");
        xmlStringBuilder.append("</StyleMap>");

        return xmlStringBuilder.toString();
    }

    // , List<String> images, Context c
    public static String returnPlacemark(String id, String stylemapId, String titulo, String descri, String longi, String lat) {
        StringBuilder xmlStringBuilder = new StringBuilder();

        xmlStringBuilder.append("<Placemark id=\"" + id + "\">")
                .append("\n\t<name>" + titulo + "</name>")
                .append("\n\t<description><![CDATA[<div>" + descri + "</div>]]></description>")
                .append("\n\t<LookAt>")
                .append("\n\t\t<longitude>" + longi + "</longitude>")
                .append("\n\t\t<latitude>" + lat + "</latitude>")
                .append("\n\t\t<altitude>431.4313290066361</altitude>")
                .append("\n\t\t<heading>0</heading>")
                .append("\n\t\t<tilt>0</tilt>")
                .append("\n\t\t<gx:fovy>30</gx:fovy>")
                .append("\n\t\t<range>169.5750582342589</range>")
                .append("\n\t\t<altitudeMode>absolute</altitudeMode>")
                .append("\n\t</LookAt>")
                .append("\n\t<styleUrl>#__managed_style_" + stylemapId + "</styleUrl>")
                .append("\n\t<gx:Carousel>");

//        for (String imageLink : images){
//            xmlStringBuilder.append("\n\t\t<gx:Image kml:id=\"embedded_image_03AE9FBE172BDD5C899D_0\">")
//                    .append("\n\t\t\t<gx:ImageUrl>"+
//                            imageLink
//                            +"</gx:ImageUrl>")
//                    .append("\n\t\t</gx:Image>");
//        }


        xmlStringBuilder.append("\n\t</gx:Carousel>")
                .append("\n\t<Point>")
                .append("\n\t\t<coordinates>" + longi + "," + lat + ",430.6608048569743</coordinates>")
                .append("\n\t</Point>")
                .append("\n</Placemark>");

        return xmlStringBuilder.toString();
    }

}
