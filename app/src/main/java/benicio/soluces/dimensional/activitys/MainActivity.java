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
import benicio.soluces.dimensional.utils.Converter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    ImageButton restartButton;
    TextView instrucaoTela;
    public static final String MSG1 = "Aponte para a base da árvore, em seguida clique em medir.";
    public static final String MSG2 = "Aponte para o topo da árvore, em seguida clique em medir.";

    // Componentes de medir altura
    float anguloB, anguloT, alturaCalc = 0.0f;
    int etapa = 0; // 0 medir b 1 medir t 2 medir largura
    TextView anguloBText, anguloTText, setinha, medirAngulo, alturaReal;
    // Componentes de medir altura

    // Componentes de medir largura
    int divisorPorZoom = 1;
    LinearLayout barrinhasLayout;
    ImageButton btnMaisRed, btnMenosRed, btnMaisYellow, btnMenosYellow, maisZoom, menosZoom;

    TextView qtdBarrinhasText;
    // Componentes de medir largura

    private static  final String TAG = "jamirGay";
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
    private Dialog dialogInputDH;
    private Float dh = 0.0f;
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

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        
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
        alturaReal = findViewById(R.id.altura_real_text);

        textZoom = findViewById(R.id.textViewZoom);
        qtdBarrinhasText = findViewById(R.id.qtd_barrinha);
        dadosGps = findViewById(R.id.dadosGpsText);
        medidaRealText = findViewById(R.id.medida_real_text);
        imageEmpresa = findViewById(R.id.logoEmpresa);

        restartButton.setOnClickListener( view -> {
            Toast.makeText(this, "Reiniciando...", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(this, MainActivity.class));
        });
        medirAngulo.setOnClickListener( view -> {
            if ( etapa <= 2){
                etapa++;
                instrucaoTela.setText(MSG2);
            }

            if( etapa == 2){
                if ( dh != 0){
                    calculateMeasureHeight();
                    musarParaMedidorDiametro();
                }else{
                    dialogInputDH.show();
                }
            }
        });

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        configurarIncrementoDecrementoAutomatico();

        configurarDialogDH();
        dialogInputDH.show();

        preferences = getSharedPreferences("configPreferences", Context.MODE_PRIVATE);
        editor = preferences.edit();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);



        scale = getResources().getDisplayMetrics().density;

//        R.id.textViewTamanho.setText(
//                Converter.converterDpParaCm(getApplicationContext(), dpBarrinhas)
//        );

        preencherListas();

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

        anguloBText.setVisibility(View.GONE);
        anguloTText.setVisibility(View.GONE);
        setinha.setVisibility(View.GONE);
        medirAngulo.setVisibility(View.GONE);
        instrucaoTela.clearAnimation();
        instrucaoTela.setVisibility(View.INVISIBLE);
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

        alturaReal.setText(String.format("%.4f cm", (Float.valueOf(alturaCalc) * 100 )));
    }

    private void configurarDialogDH() {
        AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
        View dhlbinding = LayoutInflater.from(MainActivity.this).inflate(R.layout.input_distancia_horizoltal_layout, null);

        Button okBtn = dhlbinding.findViewById(R.id.ok_btn);
        TextInputLayout dhField = dhlbinding.findViewById(R.id.dh_field);

        okBtn.setOnClickListener( view -> {
            String dhString = dhField.getEditText().getText().toString();

            if ( !dhString.isEmpty() ){
                dh = Float.parseFloat(dhString);
                if ( etapa == 2){calculateMeasureHeight(); musarParaMedidorDiametro();}
            }
            dialogInputDH.dismiss();
        });

        b.setView(dhlbinding);
        dialogInputDH = b.create();
    }

    private void preencherListas(){

        listay.add(findViewById(R.id.a1).getId());
        listay.add(findViewById(R.id.a2).getId());
        listay.add(findViewById(R.id.a3).getId());
        listay.add(findViewById(R.id.a4).getId());
        listay.add(findViewById(R.id.a5).getId());
        listay.add(findViewById(R.id.a6).getId());
        listay.add(findViewById(R.id.a7).getId());
        listay.add(findViewById(R.id.a8).getId());
        listay.add(findViewById(R.id.a9).getId());
        listay.add(findViewById(R.id.a10).getId());
        listay.add(findViewById(R.id.a11).getId());
        listay.add(findViewById(R.id.a12).getId());
        listay.add(findViewById(R.id.a13).getId());
        listay.add(findViewById(R.id.a14).getId());
        listay.add(findViewById(R.id.a15).getId());
        listay.add(findViewById(R.id.a16).getId());
        listay.add(findViewById(R.id.a17).getId());
        listay.add(findViewById(R.id.a18).getId());
        listay.add(findViewById(R.id.a19).getId());
        listay.add(findViewById(R.id.a20).getId());
        listay.add(findViewById(R.id.a21).getId());
        listay.add(findViewById(R.id.a22).getId());
        listay.add(findViewById(R.id.a23).getId());
        listay.add(findViewById(R.id.a24).getId());
        listay.add(findViewById(R.id.a25).getId());
        listay.add(findViewById(R.id.a26).getId());
        listay.add(findViewById(R.id.a27).getId());
        listay.add(findViewById(R.id.a28).getId());
        listay.add(findViewById(R.id.a29).getId());
        listay.add(findViewById(R.id.a30).getId());
        listay.add(findViewById(R.id.a31).getId());
        listay.add(findViewById(R.id.a32).getId());
        listay.add(findViewById(R.id.a33).getId());
        listay.add(findViewById(R.id.a34).getId());
        listay.add(findViewById(R.id.a35).getId());
        listay.add(findViewById(R.id.a36).getId());
        listay.add(findViewById(R.id.a37).getId());
        listay.add(findViewById(R.id.a38).getId());
        listay.add(findViewById(R.id.a39).getId());
        listay.add(findViewById(R.id.a40).getId());
        listay.add(findViewById(R.id.a41).getId());
        listay.add(findViewById(R.id.a42).getId());
        listay.add(findViewById(R.id.a43).getId());
        listay.add(findViewById(R.id.a44).getId());
        listay.add(findViewById(R.id.a45).getId());
        listay.add(findViewById(R.id.a46).getId());
        listay.add(findViewById(R.id.a47).getId());
        listay.add(findViewById(R.id.a48).getId());
        listay.add(findViewById(R.id.a49).getId());
        listay.add(findViewById(R.id.a50).getId());
        listay.add(findViewById(R.id.a51).getId());
        listay.add(findViewById(R.id.a52).getId());
        listay.add(findViewById(R.id.a53).getId());
        listay.add(findViewById(R.id.a54).getId());
        listay.add(findViewById(R.id.a55).getId());
        listay.add(findViewById(R.id.a56).getId());
        listay.add(findViewById(R.id.a57).getId());
        listay.add(findViewById(R.id.a58).getId());
        listay.add(findViewById(R.id.a59).getId());
        listay.add(findViewById(R.id.a60).getId());
        listay.add(findViewById(R.id.a61).getId());
        listay.add(findViewById(R.id.a62).getId());
        listay.add(findViewById(R.id.a63).getId());
        listay.add(findViewById(R.id.a64).getId());
        listay.add(findViewById(R.id.a65).getId());
        listay.add(findViewById(R.id.a66).getId());
        listay.add(findViewById(R.id.a67).getId());
        listay.add(findViewById(R.id.a68).getId());
        listay.add(findViewById(R.id.a69).getId());
        listay.add(findViewById(R.id.a70).getId());
        listay.add(findViewById(R.id.a71).getId());
        listay.add(findViewById(R.id.a72).getId());
        listay.add(findViewById(R.id.a73).getId());
        listay.add(findViewById(R.id.a74).getId());
        listay.add(findViewById(R.id.a75).getId());
        listay.add(findViewById(R.id.a76).getId());
        listay.add(findViewById(R.id.a77).getId());
        listay.add(findViewById(R.id.a78).getId());
        listay.add(findViewById(R.id.a79).getId());
        listay.add(findViewById(R.id.a80).getId());
        listay.add(findViewById(R.id.a81).getId());
        listay.add(findViewById(R.id.a82).getId());
        listay.add(findViewById(R.id.a83).getId());
        listay.add(findViewById(R.id.a84).getId());
        listay.add(findViewById(R.id.a85).getId());
        listay.add(findViewById(R.id.a86).getId());
        listay.add(findViewById(R.id.a87).getId());
        listay.add(findViewById(R.id.a88).getId());
        listay.add(findViewById(R.id.a89).getId());
        listay.add(findViewById(R.id.a90).getId());
        listay.add(findViewById(R.id.a91).getId());
        listay.add(findViewById(R.id.a92).getId());
        listay.add(findViewById(R.id.a93).getId());
        listay.add(findViewById(R.id.a94).getId());
        listay.add(findViewById(R.id.a95).getId());
        listay.add(findViewById(R.id.a96).getId());
        listay.add(findViewById(R.id.a97).getId());
        listay.add(findViewById(R.id.a98).getId());
        listay.add(findViewById(R.id.a99).getId());
        listay.add(findViewById(R.id.a100).getId());
        listay.add(findViewById(R.id.a101).getId());
        listay.add(findViewById(R.id.a102).getId());
        listay.add(findViewById(R.id.a103).getId());
        listay.add(findViewById(R.id.a104).getId());
        listay.add(findViewById(R.id.a105).getId());
        listay.add(findViewById(R.id.a106).getId());
        listay.add(findViewById(R.id.a107).getId());
        listay.add(findViewById(R.id.a108).getId());
        listay.add(findViewById(R.id.a109).getId());
        listay.add(findViewById(R.id.a110).getId());
        listay.add(findViewById(R.id.a111).getId());
        listay.add(findViewById(R.id.a112).getId());
        listay.add(findViewById(R.id.a113).getId());
        listay.add(findViewById(R.id.a114).getId());
        listay.add(findViewById(R.id.a115).getId());
        listay.add(findViewById(R.id.a116).getId());
        listay.add(findViewById(R.id.a117).getId());
        listay.add(findViewById(R.id.a118).getId());
        listay.add(findViewById(R.id.a119).getId());
        listay.add(findViewById(R.id.a120).getId());
        listay.add(findViewById(R.id.a121).getId());
        listay.add(findViewById(R.id.a122).getId());
        listay.add(findViewById(R.id.a123).getId());
        listay.add(findViewById(R.id.a124).getId());
        listay.add(findViewById(R.id.a125).getId());
        listay.add(findViewById(R.id.a126).getId());
        listay.add(findViewById(R.id.a127).getId());
        listay.add(findViewById(R.id.a128).getId());
        listay.add(findViewById(R.id.a129).getId());
        listay.add(findViewById(R.id.a130).getId());
        listay.add(findViewById(R.id.a131).getId());
        listay.add(findViewById(R.id.a132).getId());
        listay.add(findViewById(R.id.a133).getId());
        listay.add(findViewById(R.id.a134).getId());
        listay.add(findViewById(R.id.a135).getId());
        listay.add(findViewById(R.id.a136).getId());
        listay.add(findViewById(R.id.a137).getId());
        listay.add(findViewById(R.id.a138).getId());
        listay.add(findViewById(R.id.a139).getId());
        listay.add(findViewById(R.id.a140).getId());
        listay.add(findViewById(R.id.a141).getId());
        listay.add(findViewById(R.id.a142).getId());
        listay.add(findViewById(R.id.a143).getId());
        listay.add(findViewById(R.id.a144).getId());
        listay.add(findViewById(R.id.a145).getId());
        listay.add(findViewById(R.id.a146).getId());
        listay.add(findViewById(R.id.a147).getId());
        listay.add(findViewById(R.id.a148).getId());
        listay.add(findViewById(R.id.a149).getId());
        listay.add(findViewById(R.id.a150).getId());
        listay.add(findViewById(R.id.a151).getId());
        listay.add(findViewById(R.id.a152).getId());
        listay.add(findViewById(R.id.a153).getId());
        listay.add(findViewById(R.id.a154).getId());
        listay.add(findViewById(R.id.a155).getId());
        listay.add(findViewById(R.id.a156).getId());
        listay.add(findViewById(R.id.a157).getId());
        listay.add(findViewById(R.id.a158).getId());
        listay.add(findViewById(R.id.a159).getId());
        listay.add(findViewById(R.id.a160).getId());



        Collections.reverse(listay);

// Adicionando valores de r1 até r30 manualmente à listar
        listar.add(findViewById(R.id.v1).getId());
        listar.add(findViewById(R.id.v2).getId());
        listar.add(findViewById(R.id.v3).getId());
        listar.add(findViewById(R.id.v4).getId());
        listar.add(findViewById(R.id.v5).getId());
        listar.add(findViewById(R.id.v6).getId());
        listar.add(findViewById(R.id.v7).getId());
        listar.add(findViewById(R.id.v8).getId());
        listar.add(findViewById(R.id.v9).getId());
        listar.add(findViewById(R.id.v10).getId());
        listar.add(findViewById(R.id.v11).getId());
        listar.add(findViewById(R.id.v12).getId());
        listar.add(findViewById(R.id.v13).getId());
        listar.add(findViewById(R.id.v14).getId());
        listar.add(findViewById(R.id.v15).getId());
        listar.add(findViewById(R.id.v16).getId());
        listar.add(findViewById(R.id.v17).getId());
        listar.add(findViewById(R.id.v18).getId());
        listar.add(findViewById(R.id.v19).getId());
        listar.add(findViewById(R.id.v20).getId());
        listar.add(findViewById(R.id.v21).getId());
        listar.add(findViewById(R.id.v22).getId());
        listar.add(findViewById(R.id.v23).getId());
        listar.add(findViewById(R.id.v24).getId());
        listar.add(findViewById(R.id.v25).getId());
        listar.add(findViewById(R.id.v26).getId());
        listar.add(findViewById(R.id.v27).getId());
        listar.add(findViewById(R.id.v28).getId());
        listar.add(findViewById(R.id.v29).getId());
        listar.add(findViewById(R.id.v30).getId());
        listar.add(findViewById(R.id.v31).getId());
        listar.add(findViewById(R.id.v32).getId());
        listar.add(findViewById(R.id.v33).getId());
        listar.add(findViewById(R.id.v34).getId());
        listar.add(findViewById(R.id.v35).getId());
        listar.add(findViewById(R.id.v36).getId());
        listar.add(findViewById(R.id.v37).getId());
        listar.add(findViewById(R.id.v38).getId());
        listar.add(findViewById(R.id.v39).getId());
        listar.add(findViewById(R.id.v40).getId());
        listar.add(findViewById(R.id.v41).getId());
        listar.add(findViewById(R.id.v42).getId());
        listar.add(findViewById(R.id.v43).getId());
        listar.add(findViewById(R.id.v44).getId());
        listar.add(findViewById(R.id.v45).getId());
        listar.add(findViewById(R.id.v46).getId());
        listar.add(findViewById(R.id.v47).getId());
        listar.add(findViewById(R.id.v48).getId());
        listar.add(findViewById(R.id.v49).getId());
        listar.add(findViewById(R.id.v50).getId());
        listar.add(findViewById(R.id.v51).getId());
        listar.add(findViewById(R.id.v52).getId());
        listar.add(findViewById(R.id.v53).getId());
        listar.add(findViewById(R.id.v54).getId());
        listar.add(findViewById(R.id.v55).getId());
        listar.add(findViewById(R.id.v56).getId());
        listar.add(findViewById(R.id.v57).getId());
        listar.add(findViewById(R.id.v58).getId());
        listar.add(findViewById(R.id.v59).getId());
        listar.add(findViewById(R.id.v60).getId());
        listar.add(findViewById(R.id.v61).getId());
        listar.add(findViewById(R.id.v62).getId());
        listar.add(findViewById(R.id.v63).getId());
        listar.add(findViewById(R.id.v64).getId());
        listar.add(findViewById(R.id.v65).getId());
        listar.add(findViewById(R.id.v66).getId());
        listar.add(findViewById(R.id.v67).getId());
        listar.add(findViewById(R.id.v68).getId());
        listar.add(findViewById(R.id.v69).getId());
        listar.add(findViewById(R.id.v70).getId());
        listar.add(findViewById(R.id.v71).getId());
        listar.add(findViewById(R.id.v72).getId());
        listar.add(findViewById(R.id.v73).getId());
        listar.add(findViewById(R.id.v74).getId());
        listar.add(findViewById(R.id.v75).getId());
        listar.add(findViewById(R.id.v76).getId());
        listar.add(findViewById(R.id.v77).getId());
        listar.add(findViewById(R.id.v78).getId());
        listar.add(findViewById(R.id.v79).getId());
        listar.add(findViewById(R.id.v80).getId());
        listar.add(findViewById(R.id.v81).getId());
        listar.add(findViewById(R.id.v82).getId());
        listar.add(findViewById(R.id.v83).getId());
        listar.add(findViewById(R.id.v84).getId());
        listar.add(findViewById(R.id.v85).getId());
        listar.add(findViewById(R.id.v86).getId());
        listar.add(findViewById(R.id.v87).getId());
        listar.add(findViewById(R.id.v88).getId());
        listar.add(findViewById(R.id.v89).getId());
        listar.add(findViewById(R.id.v90).getId());
        listar.add(findViewById(R.id.v91).getId());
        listar.add(findViewById(R.id.v92).getId());
        listar.add(findViewById(R.id.v93).getId());
        listar.add(findViewById(R.id.v94).getId());
        listar.add(findViewById(R.id.v95).getId());
        listar.add(findViewById(R.id.v96).getId());
        listar.add(findViewById(R.id.v97).getId());
        listar.add(findViewById(R.id.v98).getId());
        listar.add(findViewById(R.id.v99).getId());
        listar.add(findViewById(R.id.v100).getId());
        listar.add(findViewById(R.id.v101).getId());
        listar.add(findViewById(R.id.v102).getId());
        listar.add(findViewById(R.id.v103).getId());
        listar.add(findViewById(R.id.v104).getId());
        listar.add(findViewById(R.id.v105).getId());
        listar.add(findViewById(R.id.v106).getId());
        listar.add(findViewById(R.id.v107).getId());
        listar.add(findViewById(R.id.v108).getId());
        listar.add(findViewById(R.id.v109).getId());
        listar.add(findViewById(R.id.v110).getId());
        listar.add(findViewById(R.id.v111).getId());
        listar.add(findViewById(R.id.v112).getId());
        listar.add(findViewById(R.id.v113).getId());
        listar.add(findViewById(R.id.v114).getId());
        listar.add(findViewById(R.id.v115).getId());
        listar.add(findViewById(R.id.v116).getId());
        listar.add(findViewById(R.id.v117).getId());
        listar.add(findViewById(R.id.v118).getId());
        listar.add(findViewById(R.id.v119).getId());
        listar.add(findViewById(R.id.v120).getId());
        listar.add(findViewById(R.id.v121).getId());
        listar.add(findViewById(R.id.v122).getId());
        listar.add(findViewById(R.id.v123).getId());
        listar.add(findViewById(R.id.v124).getId());
        listar.add(findViewById(R.id.v125).getId());
        listar.add(findViewById(R.id.v126).getId());
        listar.add(findViewById(R.id.v127).getId());
        listar.add(findViewById(R.id.v128).getId());
        listar.add(findViewById(R.id.v129).getId());
        listar.add(findViewById(R.id.v130).getId());
        listar.add(findViewById(R.id.v131).getId());
        listar.add(findViewById(R.id.v132).getId());
        listar.add(findViewById(R.id.v133).getId());
        listar.add(findViewById(R.id.v134).getId());
        listar.add(findViewById(R.id.v135).getId());
        listar.add(findViewById(R.id.v136).getId());
        listar.add(findViewById(R.id.v137).getId());
        listar.add(findViewById(R.id.v138).getId());
        listar.add(findViewById(R.id.v139).getId());
        listar.add(findViewById(R.id.v140).getId());
        listar.add(findViewById(R.id.v141).getId());
        listar.add(findViewById(R.id.v142).getId());
        listar.add(findViewById(R.id.v143).getId());
        listar.add(findViewById(R.id.v144).getId());
        listar.add(findViewById(R.id.v145).getId());
        listar.add(findViewById(R.id.v146).getId());
        listar.add(findViewById(R.id.v147).getId());
        listar.add(findViewById(R.id.v148).getId());
        listar.add(findViewById(R.id.v149).getId());
        listar.add(findViewById(R.id.v150).getId());
        listar.add(findViewById(R.id.v151).getId());
        listar.add(findViewById(R.id.v152).getId());
        listar.add(findViewById(R.id.v153).getId());
        listar.add(findViewById(R.id.v154).getId());
        listar.add(findViewById(R.id.v155).getId());
        listar.add(findViewById(R.id.v156).getId());
        listar.add(findViewById(R.id.v157).getId());
        listar.add(findViewById(R.id.v158).getId());
        listar.add(findViewById(R.id.v159).getId());
        listar.add(findViewById(R.id.v160).getId());

        Log.d(TAG, "listar: " + listar.size());
        Log.d(TAG, "listay: " + listay.size());
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

    @SuppressLint("DefaultLocale")
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
            dialogInputDH.show();
        }
//        R.id.textViewTamanho.setText(
//                Converter.converterDpParaCm(getApplicationContext(), dpBarrinhas)
//        );
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
//                        R.id.textViewTamanho.setText(
//                                Converter.converterDpParaCm(getApplicationContext(), dpBarrinhas)
//                        );
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
//                    Toast.makeText(this, zoomString, Toast.LENGTH_SHORT).show();
//                    R.id.textViewTamanho.setText(
//                            Converter.converterDpParaCm(getApplicationContext(), dpBarrinhas)
//                    );
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

//                            R.id.textViewTamanho.setText(
//                                    Converter.converterDpParaCm(getApplicationContext(), dpBarrinhas)
//                            );
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
//        R.id.dadosDaTela.setText(
//              textoFixo
//        );

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
        int qtdPos = qtdBarrinhas + (qtdBarrinhas - 1);
        qtdBarrinhasText.setText( qtdPos + "");
        medidaRealText.setText(
                String.format("L %.4fcm", (dh * (qtdPos / divisorPorZoom) * CONST_CHAVE))
        );
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
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
                        anguloBText.setText(String.format("%.2f°", degrees));
                        anguloB = degrees;
                        break;
                    case 1:
                        anguloTText.setText(String.format("%.2f°", degrees));
                        anguloT = degrees;
                        break;
                }
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