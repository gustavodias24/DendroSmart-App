package benicio.soluces.dimensional.activitys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LifecycleOwner;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.textfield.TextInputLayout;
import com.google.common.util.concurrent.ListenableFuture;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import benicio.soluces.dimensional.R;
import benicio.soluces.dimensional.model.ItemRelatorio;
import benicio.soluces.dimensional.utils.Converter;
import benicio.soluces.dimensional.utils.GenericUtils;
import benicio.soluces.dimensional.utils.ItemRelatorioUtil;
import benicio.soluces.dimensional.utils.ListaBarrinhasUtils;
import benicio.soluces.dimensional.utils.MetodosUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {
    float alturaDesejada = 0.0f;
    float volumeTotal = 0.0f;
    int qtdPos;
    float diametroMarcado;
    TextView infosGenericas, infoMedirTora;
    LinearLayout layoutIntroVolume;
    boolean acabouToras = false;
    String parteDaTora = "a base";
    int parteDaToraPos = 1;
    int toraAtual = 1;
    Float alturaAtualTora = 0.0f;
    String alturaAtualToraString = "0.0 m";
    Float anguloAtualTora = 0.0f;
    Float anguloBaseTora = 0.0f;
    Float diametroBaseTora = 0.0f;
    Float diametroMedioTora = 0.0f;
    Float diametroTopoTora = 0.0f;

    Float ultimoDiametroBase = 0.0f;
    boolean primeiroCalculoBase = false;

    // variaveis da divisão
    int qtdDivisao = 0;
    float tamCadaParte = 0.f;
//    Dialog dialogDivisao;

    // variaveis da divisão

    ImageButton restartButton;
    TextView instrucaoTela;
    public static final String MSG1 = "Aponte para a base da árvore, em seguida clique em medir.";
    public static final String MSG2 = "Aponte para o topo da árvore, em seguida clique em medir.";

    // Componentes de medir altura
    float anguloB, anguloT, alturaCalc = 0.0f;
    int etapa = 0; // 0 medir b 1 medir t 2 medir largura
    TextView anguloBText, anguloTText, setinha, medirAngulo, alturaReal, medirDiametro;
    // Componentes de medir altura

    // Componentes de medir largura
    int divisorPorZoom = 1;
    LinearLayout barrinhasLayout;
    ImageButton btnMaisRed, btnMenosRed, btnMaisYellow, btnMenosYellow, maisZoom, menosZoom;

    TextView qtdBarrinhasText;
    // Componentes de medir largura

    private static  final String TAG = "mayara";
    TextView textZoom, dadosGps, medidaRealText;
    ImageView imageEmpresa;
    private static final float CONST_CHAVE = 0.054347826f;
//    private static final float CONST_CHAVE = 0.130266f;
    private long lastUpdate;
    SensorManager sensorManager;
    Sensor accelerometer;
    private boolean longPressing = false;
    private boolean ocupadoRed = false;
    private boolean ocupadoYellow = false;
    private boolean firstTime;
    private int tempoEspera = 500;


    String qualPressionado = "";
    Runnable longPressRunnable;
//    private Dialog dialogInputDH;
    private Float dh ;
    private Bundle bundle;
    private static final int ALTURA_BARRINHA_NORMAL = 30;
    private static final int ALTURA_BARRINHA_AUMENTADA = 70;
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
    private int qtdBarrinhas = 8;
    private int ACRESCENTADOR = 0;
    private int LIMITER = 0;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private float maxZoomLevel = 1f; // Variável para armazenar o zoom máximo

    private float currentZoomLevel = 2.0f;
    private String textoFixo = "";
    private Camera mCamera;
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
    private float lastAccelX;
    private float lastAccelY;
    private float lastAccelZ;

    @SuppressLint({"ResourceType", "MissingInflatedId", "DefaultLocale"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        bundle = getIntent().getExtras();

        dh = bundle.getFloat("dh");
        tamCadaParte = bundle.getFloat("tamCadaParte");

        medidaRealText = findViewById(R.id.medida_real_text);
        medidaRealText.setText(
                String.format("L %.4f m", ((dh * ((qtdBarrinhas + (qtdBarrinhas - 1)) / divisorPorZoom) * CONST_CHAVE)/100))
        );

        findViewById(R.id.backButton).setOnClickListener( view -> {
            finish();
        });

        infosGenericas = findViewById(R.id.infos_dev_text);
        layoutIntroVolume = findViewById(R.id.layout_mediar_volume);
        infoMedirTora = findViewById(R.id.info_medir_volume_tora);

        configurarInstrucaoTela();
        restartButton = findViewById(R.id.restartButton);
        
        maisZoom = findViewById(R.id.mais_zoom);
        menosZoom = findViewById(R.id.menos_zoom);
        btnMaisRed = findViewById(R.id.maisred);
        btnMenosRed = findViewById(R.id.menosred);
        btnMaisYellow = findViewById(R.id.maisyelow);
        btnMenosYellow = findViewById(R.id.menosyelow);
        barrinhasLayout = findViewById(R.id.barrinhas);

        anguloBText = findViewById(R.id.angulo_base);
        anguloTText = findViewById(R.id.angulo_topo);
        setinha = findViewById(R.id.setinha);
        medirAngulo = findViewById(R.id.calAltura);
        medirDiametro = findViewById(R.id.medirDiametroBtn);
        alturaReal = findViewById(R.id.altura_real_text);

        textZoom = findViewById(R.id.textViewZoom);
        qtdBarrinhasText = findViewById(R.id.qtd_barrinha);
        dadosGps = findViewById(R.id.dadosGpsText);
        imageEmpresa = findViewById(R.id.logoEmpresa);

        restartButton.setOnClickListener( view -> {
            Toast.makeText(this, "Reiniciando...", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(this, BaterFotoArvoreActivity.class));
        });
        medirAngulo.setOnClickListener( view -> {

            medirAngulo.setText("Medir ângulo T");

            if ( etapa <= 2){
                etapa++;
                instrucaoTela.setText(MSG2);

            }

            if( etapa == 2){
                if ( dh != 0){
                    calculateMeasureHeight();
                    musarParaMedidorDiametro();
                    calcularQuantidadeTora();
                }else{
//                    dialogInputDH.show();
                }
            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        configurarIncrementoDecrementoAutomatico();

//        configurarDialogDH();
//        configurarDialogDivisao();
//        dialogInputDH.show();

        preferences = getSharedPreferences("configPreferences", Context.MODE_PRIVATE);
        editor = preferences.edit();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        scale = getResources().getDisplayMetrics().density;

        ListaBarrinhasUtils.preencherListas(listay, listar, this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        previewView = findViewById(R.id.camera_preview);


        findViewById(R.id.maisyelow).setOnClickListener(this);
        findViewById(R.id.menosyelow).setOnClickListener(this);
        findViewById(R.id.maisred).setOnClickListener(this);
        findViewById(R.id.menosred).setOnClickListener(this);
        findViewById(R.id.mais_zoom).setOnClickListener(this);
        findViewById(R.id.menos_zoom).setOnClickListener(this);
        findViewById(R.id.configuracoes).setOnClickListener(this);
        findViewById(R.id.setar_dh).setOnClickListener(this);
        medirDiametro.setOnClickListener(this);

        configurarEventoDePressionar();
        pegarZoomMaximo();

        if (
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera(cameraFacing);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA }, PERMISSIONS_GERAL);
        }

//        calcularTamanhoDaTela();

        Runnable runnableCode = new Runnable() {
            @Override
            public void run() {
                pegarLocalizacao();
                handler.postDelayed(this, delay);
            }
        };

        runnableCode.run();

    }

    private void configurarInstrucaoTela() {
        // Obtém a referência do TextView
        instrucaoTela = findViewById(R.id.instrucao_text);

        // Cria a animação
        Animation blinkAnimation = new AlphaAnimation(1, 0); // De totalmente visível para totalmente transparente
        blinkAnimation.setDuration(1000); // Define a duração da animação em milissegundos
        blinkAnimation.setRepeatMode(Animation.REVERSE); // Inverte a animação ao chegar ao fim
        blinkAnimation.setRepeatCount(Animation.INFINITE); // Repete a animação infinitamente

        // Define um listener para a animação (opcional)
        blinkAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Executa ações quando a animação começa
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Executa ações quando a animação termina
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Executa ações quando a animação se repete
            }
        });

        // Inicia a animação
        instrucaoTela.startAnimation(blinkAnimation);
//        imageAnguloCorreto.startAnimation(blinkAnimation);
    }

    private void musarParaMedidorDiametro(){
        qtdBarrinhasText.setVisibility(View.VISIBLE);
        maisZoom.setVisibility(View.VISIBLE);
        menosZoom.setVisibility(View.VISIBLE);
        btnMaisRed.setVisibility(View.VISIBLE);
        btnMenosRed.setVisibility(View.VISIBLE);
        btnMaisYellow.setVisibility(View.VISIBLE);
        btnMenosYellow.setVisibility(View.VISIBLE);
        barrinhasLayout.setVisibility(View.VISIBLE);
        layoutIntroVolume.setVisibility(View.VISIBLE);
        medirDiametro.setVisibility(View.VISIBLE);

        anguloBText.setVisibility(View.GONE);
        anguloTText.setVisibility(View.GONE);
        setinha.setVisibility(View.GONE);
        medirAngulo.setVisibility(View.GONE);
//        instrucaoTela.clearAnimation();
//        instrucaoTela.setVisibility(View.INVISIBLE);
    }
    @SuppressLint("DefaultLocale")
    public void calculateMeasureHeight() {
        Float ValueTA =  (float) Math.tan(Math.toRadians(anguloB));
        Float ValueTB =  (float) Math.tan(Math.toRadians(anguloT));
        
        if ((ValueTA > 0.0f && ValueTB > 0.0f) || (ValueTA < 0.0f && ValueTB < 0.0f)) {
            if (ValueTA < 0.0f) {
                ValueTA = ValueTA * (-1.0f);
            }
            if (ValueTB < 0.0f) {
                ValueTB = ValueTB * (-1.0f);
            }
            alturaCalc = (dh * (ValueTA - ValueTB));

            if (alturaCalc < 0.0f) {
                alturaCalc = (alturaCalc * (-1.0f));
            }
        } else {
            if (ValueTA < 0.0f) {
                ValueTA = ValueTA * (-1.0f);
            }
            if (ValueTB < 0.0f) {
                ValueTB  = ValueTB * (-1.0f);
            }

            alturaCalc = dh * (ValueTA + ValueTB);

            if (alturaCalc < 0.0f) {
                alturaCalc = alturaCalc * (-1.0f);
            }
        }

        // mudança aqui
        alturaCalc = Float.valueOf(alturaCalc);
        alturaReal.setText(String.format("A %.4f m", alturaCalc));
    }

    @SuppressLint("DefaultLocale")
    public void calcularAlturaTora() {
        Float ValueTA =  (float) Math.tan(Math.toRadians(anguloBaseTora));
        Float ValueTB =  (float) Math.tan(Math.toRadians(anguloAtualTora));

        if ((ValueTA > 0.0f && ValueTB > 0.0f) || (ValueTA < 0.0f && ValueTB < 0.0f)) {
            if (ValueTA < 0.0f) {
                ValueTA = ValueTA * (-1.0f);
            }
            if (ValueTB < 0.0f) {
                ValueTB = ValueTB * (-1.0f);
            }
            alturaAtualTora = (dh * (ValueTA - ValueTB));

            if (alturaCalc < 0.0f) {
                alturaAtualTora = (alturaAtualTora * (-1.0f));
            }
        } else {
            if (ValueTA < 0.0f) {
                ValueTA = ValueTA * (-1.0f);
            }
            if (ValueTB < 0.0f) {
                ValueTB  = ValueTB * (-1.0f);
            }

            alturaAtualTora = dh * (ValueTA + ValueTB);

            if (alturaAtualTora < 0.0f) {
                alturaAtualTora = alturaAtualTora * (-1.0f);
            }
        }

//        alturaAtualTora = Float.valueOf(alturaAtualTora) * 100;
        alturaAtualTora = Float.valueOf(alturaAtualTora);

        alturaAtualToraString = String.format(" %.2f m", alturaAtualTora);
    }


    @SuppressLint("DefaultLocale")
    private void calcularQuantidadeTora() {
//        AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
//        b.setMessage("Insira a altura comercial de cada tora");
//        View dvbinding = LayoutInflater.from(MainActivity.this).inflate(R.layout.input_distancia_horizoltal_layout, null);


//        Button okBtn = dvbinding.findViewById(R.id.ok_btn);
//        TextInputLayout divisaoField = dvbinding.findViewById(R.id.dh_field);
//
//        b.setCancelable(false);
//        divisaoField.setHint("Altura comercial");

        qtdDivisao = (int) Math.floor(alturaCalc/tamCadaParte);
//        Double mToraPonta = (Double) ();

        Log.d(TAG, "calcularQuantidadeTora: " + alturaCalc);
        Log.d(TAG, "calcularQuantidadeTora: " + tamCadaParte);
        Log.d(TAG, "calcularQuantidadeTora: " + alturaCalc%tamCadaParte);


        infosGenericas.setText(
                String.format("%d tora(s) de %.2f metro(s)\ntora da ponta: %.2f metro(s)",
                        qtdDivisao, tamCadaParte, alturaCalc%tamCadaParte)
        );
//        dialogDivisao.dismiss();
        layoutIntroVolume.setVisibility(View.VISIBLE);

//        okBtn.setOnClickListener( view -> {
//            String divisaoString = divisaoField.getEditText().getText().toString().replace(",", ".");
//
//            if ( !divisaoString.isEmpty() ){
//
////                qtdDivisao = Integer.parseInt(divisaoString);
//                tamCadaParte = Float.parseFloat(divisaoString);
//
//                if ( tamCadaParte != 0){
//
//                    Log.d(TAG, "tamCadaParte: " + tamCadaParte);
//                    Log.d(TAG, "altura: " + alturaCalc);
//
//
//                }else{
//                    Toast.makeText(this, "Coloque algum valor válido.", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//
//        b.setView(dvbinding);
//        dialogDivisao = b.create();
    }

//    private void configurarDialogDH() {
//        AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
//        b.setMessage("Insira a distância horizontal");
//        View dhlbinding = LayoutInflater.from(MainActivity.this).inflate(R.layout.input_distancia_horizoltal_layout, null);
//
//        b.setCancelable(false);
//        Button okBtn = dhlbinding.findViewById(R.id.ok_btn);
//        TextInputLayout dhField = dhlbinding.findViewById(R.id.dh_field);
//
//        okBtn.setOnClickListener( view -> {
//            String dhString = dhField.getEditText().getText().toString();
//
//            if ( !dhString.isEmpty() ){
//                dh = Float.parseFloat(dhString.replace(",", "."));
//                atualizarContagemBarrinhas();
//                if ( etapa == 2){calculateMeasureHeight(); musarParaMedidorDiametro();}
//                dialogInputDH.dismiss();
//
//            }
//        });
//
//        b.setView(dhlbinding);
//        dialogInputDH = b.create();
//    }



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
                    mCamera.getCameraControl().setZoomRatio(currentZoomLevel);
                }

                findViewById(R.id.print).setOnClickListener( view -> {
                    if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        activityResultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    } else {
                        takePrint(imageCapture);
                    }
                });

                preview.setSurfaceProvider(previewView.getSurfaceProvider());

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));


    }

    public void takePrint(ImageCapture imageCapture){
        findViewById(R.id.mais_zoom).setVisibility(View.INVISIBLE);
        findViewById(R.id.menos_zoom).setVisibility(View.INVISIBLE);
        findViewById(R.id.configuracoes).setVisibility(View.INVISIBLE);
        findViewById(R.id.maisred).setVisibility(View.INVISIBLE);
        findViewById(R.id.menosred).setVisibility(View.INVISIBLE);
        findViewById(R.id.maisyelow).setVisibility(View.INVISIBLE);
        findViewById(R.id.menosyelow).setVisibility(View.INVISIBLE);
        findViewById(R.id.print).setVisibility(View.INVISIBLE);

        findViewById(R.id.imagePreview).setVisibility(View.VISIBLE);



        File documentosDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

        File fotoMapaDir = new File(documentosDir, "FOTO MAPA");
        if (!fotoMapaDir.exists()) {
            fotoMapaDir.mkdirs();
        }

        File partesDir = new File(fotoMapaDir, "PARTES");

        if ( !partesDir.exists()){
            partesDir.mkdirs();
        }


        final File file = new File(partesDir, System.currentTimeMillis() + ".png");
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();
        imageCapture.takePicture(outputFileOptions, Executors.newCachedThreadPool(), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                runOnUiThread(() -> {
                    Picasso.get().load(file).into(findViewById(R.id.imagePreview), new Callback() {
                        @Override
                        public void onSuccess() {
                            try {
                                // create bitmap screen capture
                                View v1 = getWindow().getDecorView().getRootView().findViewById(R.id.maconha);
                                v1.setDrawingCacheEnabled(true);
                                Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
                                v1.setDrawingCacheEnabled(false);

                                File documentosDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

                                File fotoMapaDir = new File(documentosDir, "DIMENSIONAL PHOTOS");

                                if (!fotoMapaDir.exists()) {
                                    fotoMapaDir.mkdirs();
                                }

                                File partesDir = new File(fotoMapaDir, "PARTES");

                                if ( !partesDir.exists()){
                                    partesDir.mkdirs();
                                }

                                File imageFile = new File(partesDir, UUID.randomUUID().toString() + ".png");

                                FileOutputStream outputStream = new FileOutputStream(imageFile);
                                int quality = 70;

                                bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream);

                                outputStream.flush();
                                outputStream.close();

                                Toast.makeText(MainActivity.this, "Imagem Salva", Toast.LENGTH_SHORT).show();

                                findViewById(R.id.mais_zoom).setVisibility(View.VISIBLE);
                                findViewById(R.id.menos_zoom).setVisibility(View.VISIBLE);
                                findViewById(R.id.configuracoes).setVisibility(View.VISIBLE);
                                findViewById(R.id.maisred).setVisibility(View.VISIBLE);
                                findViewById(R.id.menosred).setVisibility(View.VISIBLE);
                                findViewById(R.id.maisyelow).setVisibility(View.VISIBLE);
                                findViewById(R.id.menosyelow).setVisibility(View.VISIBLE);
                                findViewById(R.id.print).setVisibility(View.VISIBLE);

                                startCamera(cameraFacing);
                                baterPrintDenovo();
                            } catch (Throwable e) {

                            }
                        }

                        @Override
                        public void onError(Exception e) {
                            Log.d("cauda do erro", "onError: " + e.getCause().getMessage());
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            findViewById( R.id.imagePreview).setVisibility(View.GONE);
                        }
                    });
                });
            }
            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Erro: "+ exception.getMessage(), Toast.LENGTH_SHORT).show());
                startCamera(cameraFacing);
            }
        });
    }

    public  void baterPrintDenovo (){
        try {
            findViewById(R.id.configuracoes).setVisibility(View.INVISIBLE);
            findViewById(R.id.maisred).setVisibility(View.INVISIBLE);
            findViewById(R.id.menosred).setVisibility(View.INVISIBLE);
            findViewById(R.id.maisyelow).setVisibility(View.INVISIBLE);
            findViewById(R.id.menosyelow).setVisibility(View.INVISIBLE);
            findViewById(R.id.print).setVisibility(View.INVISIBLE);
            findViewById(R.id.mais_zoom).setVisibility(View.INVISIBLE);
            findViewById(R.id.menos_zoom).setVisibility(View.INVISIBLE);

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView().findViewById(R.id.maconha);
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File documentosDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);

            File fotoMapaDir = new File(documentosDir, "DIMENSIONAL PHOTOS");
            if (!fotoMapaDir.exists()) {
                fotoMapaDir.mkdirs();
            }

            File imageFile = new File(fotoMapaDir, UUID.randomUUID().toString() + ".png");

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 70;

            bitmap.compress(Bitmap.CompressFormat.PNG, quality, outputStream);

            outputStream.flush();
            outputStream.close();

            findViewById(R.id.configuracoes).setVisibility(View.VISIBLE);
            findViewById(R.id.maisred).setVisibility(View.VISIBLE);
            findViewById(R.id.menosred).setVisibility(View.VISIBLE);
            findViewById(R.id.maisyelow).setVisibility(View.VISIBLE);
            findViewById(R.id.menosyelow).setVisibility(View.VISIBLE);
            findViewById(R.id.print).setVisibility(View.VISIBLE);
            findViewById(R.id.mais_zoom).setVisibility(View.VISIBLE);
            findViewById(R.id.menos_zoom).setVisibility(View.VISIBLE);
            startCamera(cameraFacing);

            Uri uri = FileProvider.getUriForFile(Objects.requireNonNull(MainActivity.this),
                    "benicio.soluces.dimensional.provider", imageFile);

            Intent viewImageIntent = new Intent(Intent.ACTION_VIEW);
            viewImageIntent.setDataAndType(uri, "image/*");
            viewImageIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(viewImageIntent);

            findViewById(R.id.imagePreview).setVisibility(View.GONE);

        } catch (Throwable e) {
            Log.d("baterPrintDenovo:",  e.getMessage());
        }
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

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    @Override
    public void onClick(View view) {

        int id = view.getId();

        if ( id == findViewById(R.id.maisred).getId() ){
            aumentarVermelho();
        }else if (id == findViewById(R.id.menosred).getId()){
            diminuirVermelho();
        }
        else if (id ==findViewById( R.id.maisyelow).getId()){
            aumentarAmerelo();
        }else if (id == findViewById(R.id.menosyelow).getId()){
            diminuirAmerelo();
        }
        else if ( id == findViewById(R.id.configuracoes).getId() ){
            startActivity(new Intent(getApplicationContext(), ConfiguracoesActivity.class));
        }else if ( id == findViewById(R.id.setar_dh).getId() ){
//            dialogInputDH.show();
        }else if ( id == medirDiametro.getId()){

           switch (parteDaToraPos){
               case 1:
                   if (!primeiroCalculoBase){
                       diametroBaseTora = diametroMarcado;
                       parteDaToraPos++;
                       parteDaTora = "o centro";
                       primeiroCalculoBase = true;
                       alturaDesejada += (alturaCalc/4);

                   }else{
                       diametroMedioTora = diametroMarcado;
                       parteDaToraPos += 2;
                       parteDaTora = "o topo";
                       alturaDesejada += (alturaCalc/4);

                   }

                   break;
               case 2:
                   alturaDesejada += (alturaCalc/4);
                   diametroMedioTora = diametroMarcado;
                   parteDaToraPos++;
                   parteDaTora = "o topo";
                   break;
               case 3:
                   diametroTopoTora = diametroMarcado;
                   parteDaToraPos++;
//                   medirDiametro.setText("Próxima Tora");
                   parteDaTora = "o centro";

                   break;
               default:
//                   parteDaToraPos = 1;
                   if (!primeiroCalculoBase){
                       parteDaTora = "a base";
                   }else{
                       parteDaTora = "o centro";
                       parteDaToraPos = 2;
                       alturaDesejada += (alturaCalc/4);
                   }
                   medirDiametro.setText("Medir Diâmetro");

                   if ( toraAtual < qtdDivisao){

                       float volumeToraCalculado = Float.parseFloat(
                               String.valueOf(MetodosUtils.calculoNewton(diametroTopoTora, diametroMedioTora, diametroBaseTora, tamCadaParte )).split("E")[0]
                       );

                       volumeTotal += volumeToraCalculado;
                       infosGenericas.setText(
                       infosGenericas.getText() + "\n" + String.format(
                                       "Volume Tora %d: %.4f m³",
                                       toraAtual,
                                       volumeToraCalculado
                                       )
                       );
                       ultimoDiametroBase = diametroTopoTora;
                       diametroTopoTora = diametroMedioTora = diametroBaseTora = 0.0f;

                       if(primeiroCalculoBase){diametroBaseTora = ultimoDiametroBase;}

                       toraAtual += 1;
                   }else{
                       medirDiametro.setVisibility(View.GONE);
                       instrucaoTela.clearAnimation();
                       instrucaoTela.setVisibility(View.INVISIBLE);
                       acabouToras = true;

                       float volumeToraCalculado = Float.parseFloat(
                               String.valueOf(MetodosUtils.calculoNewton(diametroTopoTora, diametroMedioTora, diametroBaseTora, tamCadaParte )).split("E")[0]
                       );

                       volumeTotal += volumeToraCalculado;
                       infosGenericas.setText(
                               infosGenericas.getText() + "\n" + String.format(
                                       "Volume Tora %d: %.4f m³",
                                       toraAtual,
                                       volumeToraCalculado
                               ) + "\n" +
                                       String.format("Volume total: %.4f m³", volumeTotal)
                       );

                       ItemRelatorio novoItem = new ItemRelatorio();
                       novoItem.setDadosGps(
                               dadosGps.getText().toString()
                       );

                       novoItem.setDadosVolume(
                               infosGenericas.getText().toString()
                       );

                       LocalDateTime agora = LocalDateTime.now();

                       // Formatando a data e hora de acordo com o seu gosto
                       DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                       String dataHoraFormatada = agora.format(formatador);

                       StringBuilder dadosTora = new StringBuilder();
                       dadosTora.append("Data e Hora: ").append(dataHoraFormatada).append("\n");
                       dadosTora.append("- Infos gerais -").append("\n");
                       dadosTora.append("DH: ").append(dh).append("\n");
                       dadosTora.append("Altura total: ").append(alturaCalc).append("m");

                       novoItem.setDadosTora(
                               dadosTora.toString()
                       );

                       List<ItemRelatorio> listaParaAtualiziar = ItemRelatorioUtil.returnLista(this);
                       listaParaAtualiziar.add(novoItem);
                       ItemRelatorioUtil.saveList(listaParaAtualiziar, this);
                       Toast.makeText(this, "Árvore salva no relatório.", Toast.LENGTH_SHORT).show();

                   }
                   break;
           }
        }
    }
    private void aumentarAmerelo(){
        if ( indexy < (listay.size() - 1 )){
            qtdBarrinhas ++ ;
            atualizarContagemBarrinhas();
            indexy++;
            dpBarrinhas += 9;

            int anterior = indexy + 1 > (listay.size() - 1) ? (listay.size() - 1) : indexy + 1;
            findViewById(listay.get(anterior)).getLayoutParams().height = Converter.dpToPixels(this, ALTURA_BARRINHA_AUMENTADA);
            findViewById(listay.get(anterior)).requestLayout();

            findViewById(listay.get(indexy - 1)).getLayoutParams().height = Converter.dpToPixels(this, ALTURA_BARRINHA_NORMAL);
            findViewById(listay.get(indexy - 1)).requestLayout();

            findViewById(listay.get(indexy)).setVisibility(View.VISIBLE);

            Log.d(TAG, "aumentarAmerelo: " + indexy);

        }
    }
    private void diminuirAmerelo(){
        if ( indexy >= 1){
            qtdBarrinhas -- ;
            atualizarContagemBarrinhas();
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
            qtdBarrinhas ++ ;
            atualizarContagemBarrinhas();
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
            qtdBarrinhas --;
            atualizarContagemBarrinhas();
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

        findViewById(R.id.mais_zoom).setOnTouchListener((view, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                // Quando o botão é pressionado
                if ( findViewById(listar.get( listar.size() - 1 )).getVisibility() == View.INVISIBLE ){
                    if ( currentZoomLevel < maxZoomLevel ){

                        divisorPorZoom *= 2;

                        int quantidadeParaAjustar = (indexr+1) + (indexy+1);
                        for (int i = 0 ; i < quantidadeParaAjustar ; i++){
                            if (i % 2 == 0){
                                aumentarAmerelo();
                            }else{
                                aumentarVermelho();
                            }
                        }
                        currentZoomLevel = currentZoomLevel*2;
                        mCamera.getCameraControl().setZoomRatio(currentZoomLevel);

                        String zoomString = currentZoomLevel  + "x";
                        textZoom.setText(zoomString);

                    }
                }

            }
            return true;
        });

        findViewById(R.id.menos_zoom).setOnTouchListener((view, event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN){
                // Quando o botão é pressionado
                if ( currentZoomLevel > 2){

                    divisorPorZoom /= 2;

                    int quantidadeParaAjustar = ((indexr+1) + (indexy+1)) / 2 ;
                    Log.d("diminuicao", "configurarEventoDePressionar: " + quantidadeParaAjustar);
                    for (int i = 0 ; i < quantidadeParaAjustar ; i++){
                        if (i % 2 == 0){
                            diminuirAmerelo();
                        }else{
                            diminuirVermelho();
                        }
                    }

                    currentZoomLevel = currentZoomLevel/2;
                    mCamera.getCameraControl().setZoomRatio(currentZoomLevel);

                    String zoomString = currentZoomLevel  + "x";
                    textZoom.setText(zoomString);
                }else{
                    Log.d("zoomDiminuir", "não pode zoom: " + currentZoomLevel);
                }
            }
            return true;
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    public void configurarIncrementoDecrementoAutomatico(){
        findViewById(R.id.maisyelow).setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    ocupadoYellow = true;
                    qualPressionado = "+Y";
                    longPressing = true;
                    startRepeatingTask();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    ocupadoYellow = false;
                    longPressing = false;
                    stopRepeatingTask();
                    break;
            }
            return false;
        });

        findViewById(R.id.menosyelow).setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    qualPressionado = "-Y";
                    longPressing = true;
                    ocupadoYellow = true;
                    startRepeatingTask();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    stopRepeatingTask();
                    longPressing = false;
                    ocupadoYellow = false;
                    break;
            }
            return false;
        });

        findViewById(R.id.maisred).setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    qualPressionado = "+R";
                    longPressing = true;
                    ocupadoRed = true;
                    startRepeatingTask();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    stopRepeatingTask();
                    longPressing = false;
                    ocupadoRed = false;
                    break;
            }
            return false;
        });

        findViewById(R.id.menosred).setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    qualPressionado = "-R";
                    longPressing = true;
                    ocupadoRed = true;
                    startRepeatingTask();

                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    stopRepeatingTask();
                    longPressing = false;
                    ocupadoRed = false;
                    break;
            }
            return false;
        });
    }

    void startRepeatingTask() {

        firstTime = true;
        tempoEspera = 500;

        longPressRunnable = new Runnable() {
            @Override
            public void run() {

                if ((!ocupadoRed && ocupadoYellow) || (ocupadoRed && !ocupadoYellow) ){
                    if ( !firstTime ){
                        if (longPressing) {

                            switch (qualPressionado){
                                case "+Y":
                                    aumentarAmerelo();
                                    break;
                                case "-Y":
                                    diminuirAmerelo();
                                    break;
                                case "+R":
                                    aumentarVermelho();
                                    break;
                                case "-R":
                                    diminuirVermelho();
                                    break;
                            }
                            handler.postDelayed(this, tempoEspera);

                        }
                    }else{
                        handler.postDelayed(this, tempoEspera);
                        firstTime = false;
                        tempoEspera = 12;
                    }
                }
            }
        };

        longPressRunnable.run();
    }
    void stopRepeatingTask() {
        handler.removeCallbacks(longPressRunnable);
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

    private void pegarConfiguracoesAtuais(){
        if ( preferences.getString("logoImage", null) != null){
            byte[] decodedBytes = Base64.decode(preferences.getString("logoImage", null), Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            imageEmpresa.setImageBitmap(decodedBitmap);
            findViewById(R.id.logoEmpresa).setVisibility(View.VISIBLE);
        }

        if ( preferences.getBoolean("gps", false) ){
            findViewById(R.id.dadosGpsText).setVisibility(View.VISIBLE);
        }else{
            findViewById(R.id.dadosGpsText).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        pegarConfiguracoesAtuais();
        if (accelerometer == null){
            Toast.makeText(this, "Sensor acelerômetro não encontrado no dispositivo", Toast.LENGTH_SHORT).show();
            finish();
        }else {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
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

        dadosGps.setText(
                String.format("%s ás %s", formattedDate, formattedTime) + "\n" +
                        cordenadas + "\n" +
                        "Operador: " + operador
        );


        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {

            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                String[] fullAddress = address.getAddressLine(0).split(",");
                dadosGps.setText(
                        String.format("%s ás %s", formattedDate, formattedTime) + "\n" +
                                cordenadas + "\n" +
                                fullAddress[0] + ", " + fullAddress[1] + "\n" +
                                fullAddress[2] + fullAddress[3] + "\n" +
                                "Operador: " + operador
                );
            } else {
                Log.d("Address", "No address found");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void atualizarContagemBarrinhas(){
        qtdPos = qtdBarrinhas + (qtdBarrinhas - 1);
        qtdBarrinhasText.setText( qtdPos + "");
        diametroMarcado = ((dh * (qtdPos / divisorPorZoom) * CONST_CHAVE)/100);
        medidaRealText.setText(
                String.format("L %.4f m", diametroMarcado)
        );
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale", "ResourceType"})
    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() != 1) {
            return;
        }
        float f = sensorEvent.values[0];
        float f2 = sensorEvent.values[1];
        float f3 = sensorEvent.values[2];

        float degrees = (float) Math.toDegrees(Math.acos(f / ((float) Math.sqrt(((f * f) + (f2 * f2)) + (f3 * f3)))));
        if (f3 > 0.0f) {
            degrees *= -1.0f;
        }

        long currentTimeMillis = System.currentTimeMillis();
        long j = this.lastUpdate;

        if (currentTimeMillis - j > 100) {
            
            long j2 = currentTimeMillis - j;
            this.lastUpdate = currentTimeMillis;
            
            if ((Math.abs(((((f2 + f) + f3) - this.lastAccelX) - this.lastAccelY) - this.lastAccelZ) / ((float) j2)) * 10000.0f > 6.0f) {

                // Para ficar calculando a medida que altera o angulo
                if ( dh != 0.0 && anguloB != 0.0 && etapa == 1){
                    anguloT = degrees;
                    calculateMeasureHeight();
                }

                switch (etapa){
                    case 0:
                        anguloBText.setText(String.format("B %.2f°", degrees));
                        anguloBaseTora = degrees;
                        anguloB = degrees;
                        break;
                    case 1:
                        anguloTText.setText(String.format("T %.2f°", degrees));
                        anguloT = degrees;
                        break;
                }

                if ( toraAtual == qtdDivisao && acabouToras){
                    layoutIntroVolume.setVisibility(View.INVISIBLE);
                }
                calcularAlturaTora();
                anguloAtualTora = degrees;

                if ( qtdDivisao != 0){
                    instrucaoTela.setText(
//                            String.format("Aponte para %s da %d° tora na altura %.2f m", parteDaTora, toraAtual, alturaDesejada)
                            String.format("Aponte para %s da %d° tora", parteDaTora, toraAtual)
                    );
                }


                infoMedirTora.setText(
                        String.format(
                                        "\nAltura atual: %s " +
                                        "\nÂngulo atual: %.2f" +
                                        "\nDiametro da base: %.2f m" +
                                        "\nDiametro do centro: %.2f m" +
                                        "\nDiametro do topo: %.2f m",
                                alturaAtualToraString.replace("-", ""),
                                anguloAtualTora,
                                diametroBaseTora,
                                diametroMedioTora,
                                diametroTopoTora
                        )
                );
            }
            this.lastAccelX = f2;
            this.lastAccelY = f;
            this.lastAccelZ = f3;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }



}