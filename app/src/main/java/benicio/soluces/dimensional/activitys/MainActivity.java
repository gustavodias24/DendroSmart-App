package benicio.soluces.dimensional.activitys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationRequest;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import benicio.soluces.dimensional.databinding.ActivityMainBinding;
import benicio.soluces.dimensional.utils.Converter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int ALTURA_BARRINHA_NORMAL = 30;
    private static final int ALTURA_BARRINHA_AUMENTADA = 40;
    private Double latitude, longitude;
    private FusedLocationProviderClient fusedLocationClient;
    private Handler handler = new Handler(Looper.getMainLooper());
    private int delay = 3000; // 3 segundos em milissegundos
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    int dpBarrinhas = 90;
    float scale;
    int indexy = 3;
    int indexr = 3;
    List<Integer> listay = new ArrayList<>();
    List<Integer> listar = new ArrayList<>();
    private String textoFixo = "";
    private int ACRESCENTADOR = 0;
    private int LIMITER = 0;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private float maxZoomLevel = 1f; // Variável para armazenar o zoom máximo

    private float currentZoomLevel = 4.0f;
    private Camera mCamera;
    private ActivityMainBinding binding;
    private static final int PERMISSIONS_GERAL = 1;
    int cameraFacing = CameraSelector.LENS_FACING_BACK;
    private PreviewView previewView;
    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (result) {
                startCamera(cameraFacing);
            }
        }
    });

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        preferences = getSharedPreferences("configPreferences", Context.MODE_PRIVATE);
        editor = preferences.edit();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);



        scale = getResources().getDisplayMetrics().density;

        binding.textViewTamanho.setText(
                Converter.converterDpParaCm(getApplicationContext(), dpBarrinhas)
        );

        preencherListas();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        previewView = binding.cameraPreview;


        binding.maisyelow.setOnClickListener(this);
        binding.menosyelow.setOnClickListener(this);
        binding.maisred.setOnClickListener(this);
        binding.menosred.setOnClickListener(this);
        binding.maisZoom.setOnClickListener(this);
        binding.menosZoom.setOnClickListener(this);
        binding.configuracoes.setOnClickListener(this);

        configurarEventoDePressionar();
        pegarZoomMaximo();

        if (
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera(cameraFacing);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA }, PERMISSIONS_GERAL);
        }

        calcularTamanhoDaTela();

        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                pegarLocalizacao();
                handler.postDelayed(this, delay);
            }
        };

        runnableCode.run();
    }

    private void preencherListas(){
        listay.add(binding.a1.getId());
        listay.add(binding.a2.getId());
        listay.add(binding.a3.getId());
        listay.add(binding.a4.getId());
        listay.add(binding.a5.getId());
        listay.add(binding.a6.getId());
        listay.add(binding.a7.getId());
        listay.add(binding.a8.getId());
        listay.add(binding.a9.getId());
        listay.add(binding.a10.getId());
        listay.add(binding.a11.getId());
        listay.add(binding.a12.getId());
        listay.add(binding.a13.getId());
        listay.add(binding.a14.getId());
        listay.add(binding.a15.getId());
        listay.add(binding.a16.getId());
        listay.add(binding.a17.getId());
        listay.add(binding.a18.getId());
        listay.add(binding.a19.getId());
        listay.add(binding.a20.getId());
        listay.add(binding.a21.getId());
        listay.add(binding.a22.getId());
        listay.add(binding.a23.getId());
        listay.add(binding.a24.getId());
        listay.add(binding.a25.getId());
        listay.add(binding.a26.getId());
        listay.add(binding.a27.getId());
        listay.add(binding.a28.getId());
        listay.add(binding.a29.getId());
        listay.add(binding.a30.getId());
        listay.add(binding.a31.getId());
        listay.add(binding.a32.getId());
        listay.add(binding.a33.getId());
        listay.add(binding.a34.getId());
        listay.add(binding.a35.getId());
        listay.add(binding.a36.getId());
        listay.add(binding.a37.getId());
        listay.add(binding.a38.getId());
        listay.add(binding.a39.getId());
        listay.add(binding.a40.getId());

        Collections.reverse(listay);

// Adicionando valores de r1 até r30 manualmente à listar
        listar.add(binding.v1.getId());
        listar.add(binding.v2.getId());
        listar.add(binding.v3.getId());
        listar.add(binding.v4.getId());
        listar.add(binding.v5.getId());
        listar.add(binding.v6.getId());
        listar.add(binding.v7.getId());
        listar.add(binding.v8.getId());
        listar.add(binding.v9.getId());
        listar.add(binding.v10.getId());
        listar.add(binding.v11.getId());
        listar.add(binding.v12.getId());
        listar.add(binding.v13.getId());
        listar.add(binding.v14.getId());
        listar.add(binding.v15.getId());
        listar.add(binding.v16.getId());
        listar.add(binding.v17.getId());
        listar.add(binding.v18.getId());
        listar.add(binding.v19.getId());
        listar.add(binding.v20.getId());
        listar.add(binding.v21.getId());
        listar.add(binding.v22.getId());
        listar.add(binding.v23.getId());
        listar.add(binding.v24.getId());
        listar.add(binding.v25.getId());
        listar.add(binding.v26.getId());
        listar.add(binding.v27.getId());
        listar.add(binding.v28.getId());
        listar.add(binding.v29.getId());
        listar.add(binding.v30.getId());
        listar.add(binding.v31.getId());
        listar.add(binding.v32.getId());
        listar.add(binding.v33.getId());
        listar.add(binding.v34.getId());
        listar.add(binding.v35.getId());
        listar.add(binding.v36.getId());
        listar.add(binding.v37.getId());
        listar.add(binding.v38.getId());
        listar.add(binding.v39.getId());
        listar.add(binding.v40.getId());
    }

    public void pegarLocalizacao(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                configurarTextInfos();
                Log.d("latlong", "onSuccess: " +  latitude + " " + longitude);
            }
        });
    }
    public void startCamera(int cameraFacing) {
        int aspectRatio = aspectRatio(previewView.getWidth(), previewView.getHeight());
        ListenableFuture<ProcessCameraProvider> listenableFuture = ProcessCameraProvider.getInstance(this);

        listenableFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = (ProcessCameraProvider) listenableFuture.get();

                Preview preview = new Preview.Builder().setTargetAspectRatio(aspectRatio).build();

                ImageCapture imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(cameraFacing).build();

                cameraProvider.unbindAll();

                if ( !this.isDestroyed() ){
                    mCamera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
                    mCamera.getCameraControl().setZoomRatio(4.0f);
                }

                preview.setSurfaceProvider(previewView.getSurfaceProvider());

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));


    }
    private int aspectRatio(int width, int height) {
        double previewRatio = (double) Math.max(width, height) / Math.min(width, height);
        if (Math.abs(previewRatio - 4.0 / 3.0) <= Math.abs(previewRatio - 16.0 / 9.0)) {
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_GERAL) {
            boolean allPermissionsGranted = true;
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false;
                    break;
                }
            }

            // Se todas as permissões foram concedidas, inicie as operações que requerem permissões
            if (allPermissionsGranted) {
                startCamera(cameraFacing);
            } else {
                // Se o usuário recusar alguma permissão, exiba uma mensagem informando a necessidade das permissões
                Toast.makeText(this, "PERMISSÃO NEGADA", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onClick(View view) {

        int id = view.getId();

        if ( id == binding.maisred.getId() ){
            aumentarVermelho();
        }else if (id == binding.menosred.getId()){
            diminuirVermelho();
        }
        else if (id == binding.maisyelow.getId()){
            aumentarAmerelo();
        }else if (id == binding.menosyelow.getId()){
            diminuirAmerelo();
        }
        else if ( id == binding.configuracoes.getId() ){
            startActivity(new Intent(getApplicationContext(), ConfiguracoesActivity.class));
        }
        binding.textViewTamanho.setText(
                Converter.converterDpParaCm(getApplicationContext(), dpBarrinhas)
        );
    }

    private void aumentarAmerelo(){
        if ( indexy < (listay.size() - 1 )){
            indexy++;
            dpBarrinhas += 9;

            int anterior = indexy + 1 > (listay.size() - 1) ? (listay.size() - 1) : indexy + 1;
            findViewById(listay.get(anterior)).getLayoutParams().height = Converter.dpToPixels(this, ALTURA_BARRINHA_AUMENTADA);
            findViewById(listay.get(anterior)).requestLayout();

            findViewById(listay.get(indexy - 1)).getLayoutParams().height = Converter.dpToPixels(this, ALTURA_BARRINHA_NORMAL);
            findViewById(listay.get(indexy - 1)).requestLayout();

            findViewById(listay.get(indexy)).setVisibility(View.VISIBLE);
        }
    }
    private void diminuirAmerelo(){
        if ( indexy >= 1){
            dpBarrinhas -= 9;

            int anterior = Math.min(indexy + 1, 0);
            findViewById(listay.get(anterior)).getLayoutParams().height = Converter.dpToPixels(this, ALTURA_BARRINHA_NORMAL);
            findViewById(listay.get(anterior)).requestLayout();

            findViewById(listay.get(indexy - 1)).getLayoutParams().height = Converter.dpToPixels(this, ALTURA_BARRINHA_AUMENTADA);
            findViewById(listay.get(indexy - 1)).requestLayout();

            findViewById(listay.get(indexy)).setVisibility(View.INVISIBLE);
            indexy--;
        }
    }
    private void aumentarVermelho(){
        if ( indexr < (listar.size() - 1 ) ){
            indexr++;
            dpBarrinhas += 9;
            findViewById(listar.get(indexr)).setVisibility(View.VISIBLE);

            int anterior = Math.max(indexr - 1, 0);
            findViewById(listar.get(anterior)).getLayoutParams().height = Converter.dpToPixels(this, ALTURA_BARRINHA_NORMAL);
            findViewById(listar.get(anterior)).requestLayout();

            findViewById(listar.get(indexr)).getLayoutParams().height = Converter.dpToPixels(this, ALTURA_BARRINHA_AUMENTADA);
            findViewById(listar.get(indexr)).requestLayout();

        }
    }
    private void diminuirVermelho(){
        if ( indexr >= 1){
            dpBarrinhas -= 9;
            findViewById(listar.get(indexr)).setVisibility(View.INVISIBLE);

            int anterior = Math.max(indexr - 1, 0);
            findViewById(listar.get(anterior)).getLayoutParams().height = Converter.dpToPixels(this, ALTURA_BARRINHA_AUMENTADA);
            findViewById(listar.get(anterior)).requestLayout();

            findViewById(listar.get(indexr)).getLayoutParams().height = Converter.dpToPixels(this, ALTURA_BARRINHA_NORMAL);
            findViewById(listar.get(indexr)).requestLayout();

            indexr--;
        }
    }
    @SuppressLint("ClickableViewAccessibility")
    public void configurarEventoDePressionar(){

        binding.maisZoom.setOnTouchListener((view, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                // Quando o botão é pressionado
                if ( findViewById(listar.get( listar.size() - 1 )).getVisibility() == View.INVISIBLE ){
                    if ( currentZoomLevel < maxZoomLevel ){

                        int quantidadeParaAjustar = (int) Math.ceil( (indexr + indexy) * 0.5 ) / 2;
                        quantidadeParaAjustar = quantidadeParaAjustar == 0 ? 1 : quantidadeParaAjustar;

                        for (int i = 0 ; i < quantidadeParaAjustar  ; i++){
                            aumentarAmerelo();
                            aumentarVermelho();
                        }

                        currentZoomLevel += 0.5f;
                        mCamera.getCameraControl().setZoomRatio(currentZoomLevel);

                        String zoomString = currentZoomLevel  + "x";
                        binding.textViewZoom.setText(zoomString);
//                    Toast.makeText(this, zoomString, Toast.LENGTH_SHORT).show();
                        binding.textViewTamanho.setText(
                                Converter.converterDpParaCm(getApplicationContext(), dpBarrinhas)
                        );
                    }
                }

            }
            return true;
        });

        binding.menosZoom.setOnTouchListener((view, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                // Quando o botão é pressionado
                if ( currentZoomLevel > 1){

                    int quantidadeParaAjustar = (int) Math.floor( (indexr + indexy) * 0.5 ) / 2;
                    quantidadeParaAjustar = quantidadeParaAjustar == 0 ? 1 : quantidadeParaAjustar;


                    for (int i = 0 ; i < quantidadeParaAjustar ; i++){
                        diminuirVermelho();
                        diminuirAmerelo();
                    }


                    currentZoomLevel -= 0.5f;
                    mCamera.getCameraControl().setZoomRatio(currentZoomLevel);

                    String zoomString = currentZoomLevel  + "x";
                    binding.textViewZoom.setText(zoomString);
//                    Toast.makeText(this, zoomString, Toast.LENGTH_SHORT).show();
                    binding.textViewTamanho.setText(
                            Converter.converterDpParaCm(getApplicationContext(), dpBarrinhas)
                    );
                }
            }
            return true;
        });
    }

    public void pegarZoomMaximo(){
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Selecionar a câmera traseira como padrão
                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                // Configurar o Preview da câmera
                Preview preview = new Preview.Builder().build();

                // Configurar o ImageCapture da câmera
                ImageCapture imageCapture = new ImageCapture.Builder().build();

                // Vincular a câmera ao ciclo de vida
                cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);

                // Obter o zoom máximo da câmera usando Camera2 API
                CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
                String cameraId = cameraManager.getCameraIdList()[0];
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
                float maxZoom = characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);

                if (maxZoom > 1) {
                    maxZoomLevel = maxZoom;
                }

            } catch (Exception e) {
                // Lidar com exceções relacionadas à câmera
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @SuppressLint("DefaultLocale")
    public void calcularTamanhoDaTela(){
        // Obtendo as dimensões da tela em pixels
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int heightPixels = displayMetrics.heightPixels;
        int widthPixels = displayMetrics.widthPixels;

        // Calculando as dimensões em centímetros
        double heightInches = heightPixels / displayMetrics.ydpi;
        double widthInches = widthPixels / displayMetrics.xdpi;

        // Convertendo polegadas para centímetros (1 polegada = 2.54 cm)
        double heightCm = heightInches * 2.54;
        double widthCm = widthInches * 2.54;

//        ACRESCENTADOR = (int) Math.ceil(((20 * widthPixels) / 1459));
//        LIMITER = (int) Math.ceil(((360 * widthPixels) / 1459));
        ACRESCENTADOR = 20;
        LIMITER = 360;


        Log.d("calcularTamanhoDaTela", "acrescenteador: " + ACRESCENTADOR);
        textoFixo =   String.format(
                "%.2fx%.2f cm"
                        +"\n"+
                        "%.2fx%.2f polegadas"
                        +"\n"+
                        "%dx%d pixels"
                        +"\n"+
                        "Acrescentadorr: %d"
                        +"\n"+
                        "Limitador: %d"
                , widthCm, heightCm, widthInches,heightInches, widthPixels, heightPixels, ACRESCENTADOR, LIMITER );
//        binding.dadosDaTela.setText(
//              textoFixo
//        );

    }

    private void pegarConfiguracoesAtuais(){
        if ( preferences.getString("logoImage", null) != null){
            byte[] decodedBytes = Base64.decode(preferences.getString("logoImage", null), Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            binding.logoEmpresa.setImageBitmap(decodedBitmap);
            binding.logoEmpresa.setVisibility(View.VISIBLE);
        }

        if ( preferences.getBoolean("gps", false) ){
            binding.dadosGpsText.setVisibility(View.VISIBLE);
        }else{
            binding.dadosGpsText.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        pegarConfiguracoesAtuais();
    }

    @SuppressLint("SetTextI18n")
    private void configurarTextInfos(){
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // Formatar a data e a hora
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        String formattedDate = dateFormat.format(currentDate);
        String formattedTime = timeFormat.format(currentDate);

        String operador;

        String nomeOperador = getSharedPreferences("configPreferences", MODE_PRIVATE).getString("operador", "");
        operador = Objects.requireNonNull(nomeOperador.isEmpty() ? "Nome não informado." : nomeOperador);


        @SuppressLint("DefaultLocale") String cordenadas = String.format("Lat: %f Long: %f", latitude, longitude);

        binding.dadosGpsText.setText(
                String.format("%s ás %s", formattedDate, formattedTime) + "\n" +
                        cordenadas + "\n" +
                        "Operador: " + operador
        );


        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {

            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                String fullAddress = address.getAddressLine(0).replace(",", "\n");
                binding.dadosGpsText.setText(
                        String.format("%s ás %s", formattedDate, formattedTime) + "\n" +
                                cordenadas + "\n" +
                                fullAddress + "\n" +
                                "Operador: " + operador
                );
                Log.d("Address", fullAddress);
            } else {
                Log.d("Address", "No address found");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}