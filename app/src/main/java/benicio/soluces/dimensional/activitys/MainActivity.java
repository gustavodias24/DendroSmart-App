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
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
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
import android.view.animation.AnimationSet;
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.BindException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
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

    private float tolerancia = 0.0f;
    private MediaPlayer mediaPlayer;
    private MediaPlayer mediaPlayerError;
    private ImageButton relatoriobtn;
    private ItemRelatorio itemRelatorio;
    TextView edt_dh;
    private Boolean isPrimeiraVez = true;
    boolean smalianFirstCalc = true;
    float disDireta = 0.0f;
    ImageView imagemIlustrativaArvore;
    float toraDaponta = 0.0f;
    TextView alturaAtual;
    ServerSocket serverSocket = null;
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

    // variaveis da divisão
    int qtdDivisao = 0;
    float tamCadaParte = 0.f;

    // variaveis da divisão

    ImageButton restartButton;
    TextView instrucaoTela;
    public static final String MSG1 = "Aponte para a base da árvore, em seguida clique em medir.";
    public static final String MSG2 = "Aponte para o topo da árvore, em seguida clique em medir.";

    // Componentes de medir altura
    float anguloB, anguloT, alturaCalc = 0.0f;
    int etapa = 0; // 0 medir b 1 medir t 2 medir largura
    TextView anguloBText, anguloTText, setinha, alturaReal;
    ImageButton medirAngulo, medirDiametro;
    // Componentes de medir altura

    // Componentes de medir largura
    int divisorPorZoom = 1;
    LinearLayout barrinhasLayout;
    ImageButton btnMaisRed, btnMenosRed, btnMaisYellow, btnMenosYellow, maisZoom, menosZoom;

    TextView qtdBarrinhasText;
    // Componentes de medir largura

    private static final String TAG = "mayara";
    TextView textZoom, dadosGps, medidaRealText;
    ImageView imageEmpresa;

    private float CONST_CHAVE = 0.054347826f;

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
    private Float dh;
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
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private float maxZoomLevel = 1f; // Variável para armazenar o zoom máximo

    private float currentZoomLevel = 4.0f;
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


    @SuppressLint({"ResourceType", "MissingInflatedId", "DefaultLocale", "NewApi"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        mediaPlayer = MediaPlayer.create(this, R.raw.bip);
        mediaPlayerError = MediaPlayer.create(this, R.raw.somerro);

        preferences = getSharedPreferences("configPreferences", Context.MODE_PRIVATE);

        currentZoomLevel = Float.parseFloat(String.valueOf(preferences.getInt("zoomInicial", 4)));

        tolerancia = Float.parseFloat(
                preferences.getString("tolerancia", "0,02").replace(",", ".")
        );
        editor = preferences.edit();

        CONST_CHAVE = CONST_CHAVE * preferences.getFloat("corretivo", 0.48484848f);

        relatoriobtn = findViewById(R.id.relatoriobtn);
        relatoriobtn.setOnClickListener(view -> startActivity(new Intent(this, RelatoriosActivity.class)));
        qtdPos = qtdBarrinhas + (qtdBarrinhas - 1);

        imagemIlustrativaArvore = findViewById(R.id.imagemIlustrativaArvore);

        alturaAtual = findViewById(R.id.infos_altura_atual);


        new Thread(() -> {
            try {
                serverSocket = new ServerSocket(4203);

                while (true) {
                    try {
                        if (!serverSocket.isClosed()) {
                            Socket socket = serverSocket.accept(); // Espera por conexões
                            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));


                            final String info = reader.readLine(); // Recebe a string do cliente

                            runOnUiThread(() -> {
                                String[] command = info.split(" ");

                                Log.d("mayara", info);

                                if (command[0].equals("+") && command[1].equals("red")) {
                                    aumentarVermelho();
                                } else if (command[0].equals("-") && command[1].equals("red")) {
                                    diminuirVermelho();
                                } else if (command[0].equals("+") && command[1].equals("yellow")) {
                                    aumentarAmerelo();
                                } else if (command[0].equals("-") && command[1].equals("yellow")) {
                                    diminuirAmerelo();
                                } else if (command[0].equals("-") && command[1].equals("zoom")) {
                                    menosZoomFuncao();
                                } else if (command[0].equals("+") && command[1].equals("zoom")) {
                                    maisZoomFuncao();
                                }

                            });

                            socket.close();
                        }
                    } catch (IOException e) {
                        Log.d(TAG, "Erro ao aceitar conexão: " + e.getMessage());
                    }
                }

            } catch (BindException e) {
                Log.d(TAG, "Porta já está em uso, continuando a ouvir...");
            } catch (IOException e) {
                Log.d(TAG, "Erro ao criar o servidor: " + e.getMessage());
            }
        }).start();

        bundle = getIntent().getExtras();


        dh = bundle.getFloat("dh");
        tamCadaParte = bundle.getFloat("tamCadaParte");

        medidaRealText = findViewById(R.id.medida_real_text);
        if (divisorPorZoom == 0) divisorPorZoom = 1;
        medidaRealText.setText(String.format("Diâmetro %.4f m", ((dh * ((qtdBarrinhas + (qtdBarrinhas - 1)) / divisorPorZoom) * CONST_CHAVE) / 100)));

        findViewById(R.id.backButton).setOnClickListener(view -> {
            finish();
        });

        infosGenericas = findViewById(R.id.infos_dev_text);
        layoutIntroVolume = findViewById(R.id.layout_mediar_volume);
        infoMedirTora = findViewById(R.id.info_medir_volume_tora);

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
        animacaoBotaoZoom();
        qtdBarrinhasText = findViewById(R.id.qtd_barrinha);
        dadosGps = findViewById(R.id.dadosGpsText);
        imageEmpresa = findViewById(R.id.logoEmpresa);

        configurarInstrucaoTela();


        restartButton.setOnClickListener(view -> {
            Toast.makeText(this, "Reiniciando...", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(this, BaterFotoArvoreActivity.class));
        });
        medirAngulo.setOnClickListener(view -> {
            mediaPlayer.start();
//            medirAngulo.setText("Medir ângulo T");

            if (etapa <= 2) {
                etapa++;
                Picasso.get().load(R.drawable.angulo_topo_arvore).into(imagemIlustrativaArvore);
                instrucaoTela.setText(MSG2);

            }

            if (etapa == 2) {
                if (dh != 0) {
                    calculateMeasureHeight();
                    musarParaMedidorDiametro();
                    calcularQuantidadeTora();
                } else {
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


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        scale = getResources().getDisplayMetrics().density;

        ListaBarrinhasUtils.preencherListas(listay, listar, this);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

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

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED) {
            startCamera(cameraFacing);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSIONS_GERAL);
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


        if (bundle != null && bundle.getBoolean("diametro", false)) {

            configurarTeclado();
            musarParaMedidorDiametro();

            instrucaoTela.clearAnimation();
            medirDiametro.clearAnimation();
            medirAngulo.clearAnimation();

            findViewById(R.id.LayoutTeclado).setVisibility(View.VISIBLE);
            new Thread() {
                @Override
                public void run() {
                    super.run();

                    try {
                        sleep(4000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    runOnUiThread(() -> findViewById(R.id.LayoutTeclado).setVisibility(View.INVISIBLE));
                }
            }.start();

            findViewById(R.id.linearLayout5).setVisibility(View.INVISIBLE);
            instrucaoTela.setVisibility(View.INVISIBLE);
            infosGenericas.setVisibility(View.INVISIBLE);
            medirDiametro.setVisibility(View.INVISIBLE);
            alturaAtual.setVisibility(View.INVISIBLE);
            medirAngulo.setVisibility(View.INVISIBLE);

            restartButton.setVisibility(View.GONE);

        }
        // itemRelatorio
        itemRelatorio = new ItemRelatorio();
        itemRelatorio.setDh(String.valueOf(dh));
        itemRelatorio.setTamanhoCadaTora(String.valueOf(tamCadaParte));
        itemRelatorio.setImagemArvore(getIntent().getExtras().getString("link", ""));
    }

    @SuppressLint("DefaultLocale")
    private void configurarTeclado() {
        Button b0, b1, b2, b3, b4, b5, b6, b7, b8, b9, bVirgula;
        ImageButton bConfirmar, bApagar;
        TextView minimizar_teclado;

        minimizar_teclado = findViewById(R.id.minimizar_teclado);
        edt_dh = findViewById(R.id.edt_dh);
        edt_dh.setText(bundle.getFloat("dh") + "");
        minimizar_teclado.setVisibility(View.VISIBLE);

        minimizar_teclado.setOnClickListener(v -> {
            LinearLayout LayoutTeclado = findViewById(R.id.LayoutTeclado);
            if (LayoutTeclado.getVisibility() == View.VISIBLE) {
                LayoutTeclado.setVisibility(View.INVISIBLE);
            } else {
                LayoutTeclado.setVisibility(View.VISIBLE);
            }
        });

        b0 = findViewById(R.id.btn0);
        b1 = findViewById(R.id.btn1);
        b2 = findViewById(R.id.btn2);
        b3 = findViewById(R.id.btn3);
        b4 = findViewById(R.id.btn4);
        b5 = findViewById(R.id.btn5);
        b6 = findViewById(R.id.btn6);
        b7 = findViewById(R.id.btn7);
        b8 = findViewById(R.id.btn8);
        b9 = findViewById(R.id.btn9);

        bVirgula = findViewById(R.id.btnVirgula);
        bConfirmar = findViewById(R.id.btnProsseguir);
        bApagar = findViewById(R.id.btnApagar);

        b0.setOnClickListener(this);
        b1.setOnClickListener(this);
        b2.setOnClickListener(this);
        b3.setOnClickListener(this);
        b4.setOnClickListener(this);
        b5.setOnClickListener(this);
        b6.setOnClickListener(this);
        b7.setOnClickListener(this);
        b8.setOnClickListener(this);
        b9.setOnClickListener(this);
        bVirgula.setOnClickListener(this);

        bApagar.setOnClickListener(view -> {
            String textoExistente = edt_dh.getText().toString();

            if (textoExistente.length() == 1) {
                edt_dh.setText("0");
            } else {
                edt_dh.setText(
                        SetarDHActivity.removerUltimaLetra(textoExistente)
                );
            }
        });

        bConfirmar.setOnClickListener(v -> {
            try {
                dh = Float.parseFloat(
                        edt_dh.getText().toString().replace(",", ".")
                );

                medidaRealText.setText(String.format("Diâmetro %.4f m", ((dh * ((qtdBarrinhas + (qtdBarrinhas - 1)) / divisorPorZoom) * CONST_CHAVE) / 100)));
            } catch (Exception ignored) {
                Toast.makeText(this, "Digite um número válido!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    public void animarBotao(View v) {
        // Animação de fade in
        final AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1000);

        // Animação de fade out
        final AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(1000);

        // Listener para reiniciar a animação ao terminar
        fadeIn.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Nada a fazer aqui
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Inicia a animação de fade out após o fade in terminar
                v.startAnimation(fadeOut);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Nada a fazer aqui
            }
        });

        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // Nada a fazer aqui
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // Inicia a animação de fade in após o fade out terminar
                v.startAnimation(fadeIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // Nada a fazer aqui
            }
        });

        // Inicia a animação de fade in
        v.startAnimation(fadeIn);
    }

    public void animacaoBotaoZoom() {
        final AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(1000); // ajuste a duração conforme necessário

        // Configura a animação de fade out
        final AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
        fadeOut.setDuration(1000); // ajuste a duração conforme necessário

        // Torna o textView visível
//        textZoom.setVisibility(View.VISIBLE);

        // Inicia a animação de fade in
        textZoom.startAnimation(fadeIn);

        // Programa a tarefa para esconder o textView após 3 segundos
        new Handler().postDelayed(() -> {
            // Inicia a animação de fade out
            textZoom.startAnimation(fadeOut);

            // Torna o textView invisível após a animação
            textZoom.setVisibility(View.INVISIBLE);
        }, 3000);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                Log.d(TAG, "onDestroy: fechado");
            }
        } catch (IOException e) {
            Log.e(TAG, "Erro ao fechar o servidor: " + e.getMessage());
        }
    }

    private void configurarInstrucaoTela() {
        // Obtém a referência do TextView
        instrucaoTela = findViewById(R.id.instrucao_text);

        // Cria a animação
        Animation blinkAnimation = new AlphaAnimation(1, 0); // De totalmente visível para totalmente transparente
        blinkAnimation.setDuration(500); // Define a duração da animação em milissegundos
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
        medirAngulo.startAnimation(blinkAnimation);
        medirDiametro.startAnimation(blinkAnimation);

//        imageAnguloCorreto.startAnimation(blinkAnimation);
    }

    private void musarParaMedidorDiametro() {
//        qtdBarrinhasText.setVisibility(View.VISIBLE);
        maisZoom.setVisibility(View.VISIBLE);
        menosZoom.setVisibility(View.VISIBLE);
        btnMaisRed.setVisibility(View.VISIBLE);
        btnMenosRed.setVisibility(View.VISIBLE);
        btnMaisYellow.setVisibility(View.VISIBLE);
        medidaRealText.setVisibility(View.VISIBLE);
        btnMenosYellow.setVisibility(View.VISIBLE);
        barrinhasLayout.setVisibility(View.VISIBLE);

        findViewById(R.id.barrinha_vermelha_horizontal).setVisibility(View.VISIBLE);
        findViewById(R.id.barrinha_amarela_horizontal).setVisibility(View.VISIBLE);

        imagemIlustrativaArvore.setVisibility(View.INVISIBLE);

//        layoutIntroVolume.setVisibility(View.VISIBLE);
        medirDiametro.setVisibility(View.VISIBLE);


        alturaAtual.setVisibility(View.VISIBLE);
        anguloBText.setVisibility(View.INVISIBLE);
        anguloTText.setVisibility(View.INVISIBLE);
        setinha.setVisibility(View.INVISIBLE);
        medirAngulo.clearAnimation();
        medirAngulo.setVisibility(View.GONE);
//        instrucaoTela.clearAnimation();
//        instrucaoTela.setVisibility(View.INVISIBLE);
    }

    @SuppressLint("DefaultLocale")
    public void calculateMeasureHeight() {
        Float ValueTA = (float) Math.tan(Math.toRadians(anguloB));
        Float ValueTB = (float) Math.tan(Math.toRadians(anguloT));

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
                ValueTB = ValueTB * (-1.0f);
            }

            alturaCalc = dh * (ValueTA + ValueTB);

            if (alturaCalc < 0.0f) {
                alturaCalc = alturaCalc * (-1.0f);
            }
        }

        // mudança aqui
        alturaCalc = Float.valueOf(alturaCalc);
        alturaReal.setText(String.format("Altura Total\n%.2f m", alturaCalc));
        alturaReal.setVisibility(View.VISIBLE);
    }

    @SuppressLint("DefaultLocale")
    public void calcularAlturaTora() {
        Float ValueTA = (float) Math.tan(Math.toRadians(anguloBaseTora));
        Float ValueTB = (float) Math.tan(Math.toRadians(anguloAtualTora));

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
                ValueTB = ValueTB * (-1.0f);
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

        qtdDivisao = (int) Math.floor(alturaCalc / tamCadaParte);
        Log.d(TAG, "calcularQuantidadeTora: " + qtdDivisao);
//        Double mToraPonta = (Double) ();

        Log.d(TAG, "calcularQuantidadeTora: " + alturaCalc);
        Log.d(TAG, "calcularQuantidadeTora: " + tamCadaParte);
        Log.d(TAG, "calcularQuantidadeTora: " + alturaCalc % tamCadaParte);

        toraDaponta = alturaCalc % tamCadaParte;
        infosGenericas.setText(String.format("%d tora(s) de %.2f metro(s)\ntora da ponta: %.2f metro(s)", qtdDivisao, tamCadaParte, toraDaponta));
//        dialogDivisao.dismiss();
//        layoutIntroVolume.setVisibility(View.VISIBLE);

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


    public void pegarLocalizacao() {
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
                Log.d("latlong", "onSuccess: " + latitude + " " + longitude);
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

                ImageCapture imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY).setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();

                CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(cameraFacing).build();

                cameraProvider.unbindAll();

                if (!this.isDestroyed()) {
                    mCamera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
                    mCamera.getCameraControl().setZoomRatio(currentZoomLevel);
                }

                findViewById(R.id.print).setOnClickListener(view -> {
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

    public void takePrint(ImageCapture imageCapture) {
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

        if (!partesDir.exists()) {
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

                                if (!partesDir.exists()) {
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
                            findViewById(R.id.imagePreview).setVisibility(View.GONE);
                        }
                    });
                });
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Erro: " + exception.getMessage(), Toast.LENGTH_SHORT).show());
                startCamera(cameraFacing);
            }
        });
    }

    public void baterPrintDenovo() {
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

            Uri uri = FileProvider.getUriForFile(Objects.requireNonNull(MainActivity.this), "benicio.soluces.dimensional.provider", imageFile);

            Intent viewImageIntent = new Intent(Intent.ACTION_VIEW);
            viewImageIntent.setDataAndType(uri, "image/*");
            viewImageIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(viewImageIntent);

            findViewById(R.id.imagePreview).setVisibility(View.GONE);

        } catch (Throwable e) {
            Log.d("baterPrintDenovo:", e.getMessage());
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
//            if (allPermissionsGranted) {
            if (true) {
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

        if (id == findViewById(R.id.maisred).getId()) {
            aumentarVermelho();
        } else if (id == findViewById(R.id.menosred).getId()) {
            diminuirVermelho();
        } else if (id == findViewById(R.id.maisyelow).getId()) {
            aumentarAmerelo();
        } else if (id == findViewById(R.id.menosyelow).getId()) {
            diminuirAmerelo();
        } else if (id == findViewById(R.id.configuracoes).getId()) {
            startActivity(new Intent(getApplicationContext(), ConfiguracoesActivity.class));
        } else if (id == findViewById(R.id.setar_dh).getId()) {
//            dialogInputDH.show();
        } else if (id == medirDiametro.getId()) {

            float alturaInstataneaFormatada = Float.parseFloat(alturaAtualToraString.replace(" ", "").replace("m", "").replace(",", "."));

            if ((alturaInstataneaFormatada - tolerancia) > alturaDesejada) {
                mediaPlayerError.start();
                Toast.makeText(this, "Altura menor que a tolerância", Toast.LENGTH_SHORT).show();
            } else if ((alturaInstataneaFormatada + tolerancia) < alturaDesejada) {
                mediaPlayerError.start();
                Toast.makeText(this, "Altura ultrapassou a tolerância", Toast.LENGTH_SHORT).show();
            } else {
                mediaPlayer.start();
                if (preferences.getString("metodo", "").equals("Newton")) {
                    configurarSwitchNewton();
                } else if (preferences.getString("metodo", "").equals("Smalian")) {
                    configurarSwitchSmalian();
                }
            }

        } else if (
                id == R.id.btn0 ||
                        id == R.id.btn1 ||
                        id == R.id.btn2 ||
                        id == R.id.btn3 ||
                        id == R.id.btn4 ||
                        id == R.id.btn5 ||
                        id == R.id.btn6 ||
                        id == R.id.btn7 ||
                        id == R.id.btn8 ||
                        id == R.id.btn9 ||
                        id == R.id.btnVirgula
        ) {
            Button button = (Button) view;
            String novoTexto = button.getText().toString();

            if (isPrimeiraVez || edt_dh.getText().equals("0")) {
                edt_dh.setText(novoTexto);
                isPrimeiraVez = false;
            } else {
                String textoExistente = edt_dh.getText().toString();
                edt_dh.setText(textoExistente + novoTexto);
            }
        }

    }

    private void configurarSwitchSmalian() {
        switch (parteDaToraPos) {
            case 1:
                diametroBaseTora = diametroMarcado;
                parteDaToraPos++;
                parteDaTora = "o topo";
//                alturaDesejada += (alturaCalc / 4);
                alturaDesejada += (float) calcularIncremento((alturaCalc-toraDaponta), tamCadaParte);

                break;
            case 2:

                if (!smalianFirstCalc) {
                    diametroBaseTora = diametroTopoTora;
                }

                smalianFirstCalc = false;

                diametroTopoTora = diametroMarcado;


//                alturaDesejada += (alturaCalc / 4);
                alturaDesejada += (float) calcularIncremento((alturaCalc-toraDaponta), tamCadaParte);

                if (toraAtual < qtdDivisao) {
                    fazerCaluloVolume();
                } else {
                    fazerCalculoFinalVolume();
                    salvarArvoreRelatorio();
                }

                toraAtual += 1;

                break;
        }
    }

    public static double calcularIncremento(double alturaArvore, double comprimentoTora) {
        int numeroDeToras = (int) Math.ceil(alturaArvore / comprimentoTora);
        return 1.0 / numeroDeToras;
    }

    private void configurarSwitchNewton() {
        switch (parteDaToraPos) {
            case 1:

                diametroBaseTora = diametroMarcado;
                parteDaToraPos++;
                parteDaTora = "o centro";
                alturaDesejada += (float) calcularIncremento((alturaCalc-toraDaponta), tamCadaParte);

                break;
            case 2:

                alturaDesejada += (float) calcularIncremento((alturaCalc-toraDaponta), tamCadaParte);
                diametroMedioTora = diametroMarcado;
                parteDaToraPos++;
                parteDaTora = "o topo";

                break;
            case 3:
                diametroTopoTora = diametroMarcado;
                parteDaTora = "o centro";
                parteDaToraPos = 2;

                diametroBaseTora = ultimoDiametroBase;

                if (toraAtual < qtdDivisao) {
                    fazerCaluloVolume();
                } else {
                    fazerCalculoFinalVolume();
                    salvarArvoreRelatorio();
                }
                toraAtual += 1;
                break;
        }
    }

    private void fazerCaluloVolume() {

        float volumeToraCalculado = 0.f;
        if (preferences.getString("metodo", "").equals("Newton")) {
            volumeToraCalculado = Float.parseFloat(String.valueOf(MetodosUtils.novoCalculoNewton((diametroBaseTora / 2), (diametroMedioTora / 2), (diametroTopoTora / 2), tamCadaParte)).split("E")[0]);
        } else if (preferences.getString("metodo", "").equals("Smalian")) {
            volumeToraCalculado = Float.parseFloat(String.valueOf(MetodosUtils.calculoSmalian((diametroBaseTora / 2), (diametroTopoTora / 2), tamCadaParte)).split("E")[0]);
        }

        volumeTotal += volumeToraCalculado;
        infosGenericas.setText(infosGenericas.getText() + "\n" + String.format("Volume Tora %d: %.4f m³", toraAtual, volumeToraCalculado));
        ultimoDiametroBase = diametroTopoTora;
        diametroTopoTora = diametroMedioTora = diametroBaseTora = 0.0f;

    }

    private void fazerCalculoFinalVolume() {
        medirDiametro.setVisibility(View.GONE);
        instrucaoTela.clearAnimation();
        instrucaoTela.setVisibility(View.INVISIBLE);
        acabouToras = true;


        float volumeToraCalculado = 0.f;
        if (preferences.getString("metodo", "").equals("Newton")) {
            volumeToraCalculado = Float.parseFloat(String.valueOf(MetodosUtils.novoCalculoNewton((diametroBaseTora / 2), (diametroMedioTora / 2), (diametroTopoTora / 2), tamCadaParte)).split("E")[0]);
        } else if (preferences.getString("metodo", "").equals("Smalian")) {
            volumeToraCalculado = Float.parseFloat(String.valueOf(MetodosUtils.calculoSmalian((diametroBaseTora / 2), (diametroTopoTora / 2), tamCadaParte)).split("E")[0]);
        }

        volumeTotal += volumeToraCalculado;

        float volumePonta = 0.f;
        if (preferences.getString("metodo", "").equals("Newton")) {
            volumePonta = Float.parseFloat(String.valueOf(MetodosUtils.novoCalculoNewton((diametroBaseTora / 2), (diametroMedioTora / 2), (diametroTopoTora / 2), toraDaponta)).split("E")[0]);
        } else if (preferences.getString("metodo", "").equals("Smalian")) {
            volumePonta = Float.parseFloat(String.valueOf(MetodosUtils.calculoSmalian((diametroBaseTora / 2), (diametroTopoTora / 2), toraDaponta)).split("E")[0]);
        }


        infosGenericas.setText(infosGenericas.getText() + "\n" + String.format("Volume Tora %d: %.4f m³", toraAtual, volumeToraCalculado) + "\n" + String.format("Volume da ponta: %.4f m³", volumePonta) + "\n" + String.format("Volume total: %.4f m³", volumeTotal));

    }

    private void salvarArvoreRelatorio() {

        itemRelatorio.setDadosGps(dadosGps.getText().toString());
        itemRelatorio.setDadosVolume(infosGenericas.getText().toString());

        LocalDateTime agora = LocalDateTime.now();

        // Formatando a data e hora de acordo com o seu gosto
        DateTimeFormatter formatador = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        String dataHoraFormatada = agora.format(formatador);

        StringBuilder dadosTora = new StringBuilder();
        dadosTora.append("Data e Hora: ").append(dataHoraFormatada).append("\n");
        dadosTora.append("- Infos gerais -").append("\n");
        dadosTora.append("DH: ").append(dh).append("\n");
        dadosTora.append("Altura total: ").append(alturaCalc).append("m");

        itemRelatorio.setDadosTora(dadosTora.toString());

        List<ItemRelatorio> listaParaAtualiziar = ItemRelatorioUtil.returnLista(this);
        listaParaAtualiziar.add(itemRelatorio);
        ItemRelatorioUtil.saveList(listaParaAtualiziar, this);
        animarBotao(restartButton);
        relatoriobtn.setVisibility(View.VISIBLE);
        Toast.makeText(this, "Árvore salva no relatório.", Toast.LENGTH_SHORT).show();
    }

    private void aumentarAmerelo() {
        if (indexy < (listay.size() - 1)) {
            qtdBarrinhas++;
            atualizarContagemBarrinhas();
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

    private void diminuirAmerelo() {
        if (indexy >= 1) {
            qtdBarrinhas--;
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

    private void aumentarVermelho() {
        if (indexr < (listar.size() - 1)) {
            qtdBarrinhas++;
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

    private void diminuirVermelho() {
        if (indexr >= 1) {
            qtdBarrinhas--;
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
    public void configurarEventoDePressionar() {

        findViewById(R.id.mais_zoom).setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // Quando o botão é pressionado
                if (findViewById(listar.get(listar.size() - 1)).getVisibility() == View.INVISIBLE) {
                    maisZoomFuncao();
                }

            }
            return true;
        });

        findViewById(R.id.menos_zoom).setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                // Quando o botão é pressionado
                menosZoomFuncao();
            }
            return true;
        });
    }

    private void menosZoomFuncao() {
        if (currentZoomLevel > 2) {

            divisorPorZoom /= 2;

            int quantidadeParaAjustar = ((indexr + 1) + (indexy + 1)) / 2;
            Log.d("diminuicao", "configurarEventoDePressionar: " + quantidadeParaAjustar);
            for (int i = 0; i < quantidadeParaAjustar; i++) {
                if (i % 2 == 0) {
                    diminuirAmerelo();
                } else {
                    diminuirVermelho();
                }
            }

            currentZoomLevel = currentZoomLevel / 2;
            mCamera.getCameraControl().setZoomRatio(currentZoomLevel);

            String zoomString = currentZoomLevel + "x";
            textZoom.setText(zoomString);
            animacaoBotaoZoom();
        } else {
            Log.d("zoomDiminuir", "não pode zoom: " + currentZoomLevel);
        }
    }

    private void maisZoomFuncao() {
        if (currentZoomLevel < maxZoomLevel) {

            divisorPorZoom *= 2;

            int quantidadeParaAjustar = (indexr + 1) + (indexy + 1);
            for (int i = 0; i < quantidadeParaAjustar; i++) {
                if (i % 2 == 0) {
                    aumentarAmerelo();
                } else {
                    aumentarVermelho();
                }
            }
            currentZoomLevel = currentZoomLevel * 2;
            mCamera.getCameraControl().setZoomRatio(currentZoomLevel);

            String zoomString = currentZoomLevel + "x";
            textZoom.setText(zoomString);
            animacaoBotaoZoom();

        }
    }

    @SuppressLint("ClickableViewAccessibility")
    public void configurarIncrementoDecrementoAutomatico() {
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

                if ((!ocupadoRed && ocupadoYellow) || (ocupadoRed && !ocupadoYellow)) {
                    if (!firstTime) {
                        if (longPressing) {

                            switch (qualPressionado) {
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
                                case "angle":
                                    // TO DO
                                    break;
                            }
                            handler.postDelayed(this, tempoEspera);

                        }
                    } else {
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

    public void pegarZoomMaximo() {
        maxZoomLevel = preferences.getInt("zoomMaximo", 4);
//        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
//
//        cameraProviderFuture.addListener(() -> {
//            try {
//                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
//
//                // Selecionar a câmera traseira como padrão
//                CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
//
//                // Configurar o Preview da câmera
//                Preview preview = new Preview.Builder().build();
//
//                // Configurar o ImageCapture da câmera
//                ImageCapture imageCapture = new ImageCapture.Builder().build();
//
//                // Vincular a câmera ao ciclo de vida
//                cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageCapture);
//
//                // Obter o zoom máximo da câmera usando Camera2 API
//                CameraManager cameraManager = (CameraManager) getSystemService(CAMERA_SERVICE);
//                String cameraId = cameraManager.getCameraIdList()[0];
//                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(cameraId);
//                float maxZoom = characteristics.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);
//
//                if (maxZoom > 1) {
//                    maxZoomLevel = maxZoom;
//                }
//
//            } catch (Exception e) {
//                // Lidar com exceções relacionadas à câmera
//            }
//        }, ContextCompat.getMainExecutor(this));
    }

    private void pegarConfiguracoesAtuais() {
        if (preferences.getString("logoImage", null) != null) {
            byte[] decodedBytes = Base64.decode(preferences.getString("logoImage", null), Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            imageEmpresa.setImageBitmap(decodedBitmap);
            findViewById(R.id.logoEmpresa).setVisibility(View.VISIBLE);
        }

        if (preferences.getBoolean("gps", true)) {
            findViewById(R.id.dadosGpsText).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.dadosGpsText).setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        pegarConfiguracoesAtuais();
        if (accelerometer == null) {
            Toast.makeText(this, "Sensor acelerômetro não encontrado no dispositivo", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @SuppressLint("SetTextI18n")
    private void configurarTextInfos() {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        // Formatar a data e a hora
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        String formattedDate = dateFormat.format(currentDate);
        String formattedTime = timeFormat.format(currentDate);

        String operador;

        String nomeOperador = preferences.getString("operador", "");
        operador = Objects.requireNonNull(nomeOperador.isEmpty() ? "Nome não informado." : nomeOperador);


        @SuppressLint("DefaultLocale") String cordenadas = String.format("Lat: %f Long: %f", latitude, longitude);

        itemRelatorio.setLatitude(latitude + "");
        itemRelatorio.setLongitude(longitude + "");

        dadosGps.setText(String.format("%s ás %s", formattedDate, formattedTime) + "\n" + cordenadas + "\n" + "Operador: " + operador);


        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {

            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                String[] fullAddress = address.getAddressLine(0).split(",");
                dadosGps.setText(fullAddress[0] + ", " + fullAddress[1] + "\n" + fullAddress[2] + fullAddress[3] + "\n" + cordenadas + "\n" + String.format("%s ás %s", formattedDate, formattedTime) + "\n" + "Operador: " + operador + "\n" + "Método: " + preferences.getString("metodo", ""));
            } else {
                Log.d("Address", "No address found");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void atualizarContagemBarrinhas() {
        qtdPos = qtdBarrinhas + (qtdBarrinhas - 1);
        qtdBarrinhasText.setText(qtdPos + "");
        if (divisorPorZoom == 0) {
            divisorPorZoom = 1;
        }
        diametroMarcado = ((dh * (qtdPos / divisorPorZoom) * CONST_CHAVE) / 100);
        medidaRealText.setText(String.format("Diâmetro %.4f m", diametroMarcado));
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

        // 100
//        if (currentTimeMillis - j > 0) {

        long j2 = currentTimeMillis - j;
        this.lastUpdate = currentTimeMillis;

        if ((Math.abs(((((f2 + f) + f3) - this.lastAccelX) - this.lastAccelY) - this.lastAccelZ) / ((float) j2)) * 10000.0f > 6.0f) {

            // Para ficar calculando a medida que altera o angulo
            if (dh != 0.0 && anguloB != 0.0 && etapa == 1) {
                anguloT = degrees;
                calculateMeasureHeight();
            }

            switch (etapa) {
                case 0:
                    anguloBText.setText(String.format("Base %.2f°", degrees));
                    anguloBaseTora = degrees;
                    anguloB = degrees;
                    break;
                case 1:
                    anguloTText.setText(String.format("Topo %.2f°", degrees));
                    anguloT = degrees;
                    break;
            }

            if (toraAtual == qtdDivisao && acabouToras) {
                layoutIntroVolume.setVisibility(View.INVISIBLE);
            }
            calcularAlturaTora();
            anguloAtualTora = degrees;

            float anguloRadiano = (float) Math.toRadians(anguloAtualTora);
            float cos = (float) Math.cos(anguloRadiano);

            disDireta = dh / cos;

            diametroMarcado = ((disDireta * (qtdPos / divisorPorZoom) * CONST_CHAVE) / 100);
            if (bundle == null && !bundle.getBoolean("diametro", false)) {
                medidaRealText.setText(String.format("Diâmetro %.4f m", diametroMarcado));
            }

            if (qtdDivisao != 0) {
                instrucaoTela.setText(
                        String.format("Aponte para %s da %d° tora na altura %.2f m", parteDaTora, toraAtual, alturaDesejada));
//                            String.format("Aponte para %s da %d° tora", parteDaTora, toraAtual));
            }


            alturaAtual.setText(String.format("Altura Istantânea: %s ", alturaAtualToraString.replace("-", "")));

            infoMedirTora.setText(String.format("\nAltura atual: %s " + "\nÂngulo atual: %.2f" + "\nDiametro da base: %.2f m" + "\nDiametro do centro: %.2f m" + "\nDiametro do topo: %.2f m", alturaAtualToraString.replace("-", ""), anguloAtualTora, diametroBaseTora, diametroMedioTora, diametroTopoTora));
        }
        this.lastAccelX = f2;
        this.lastAccelY = f;
        this.lastAccelZ = f3;
//        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
    }


    public static String getIPAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                Enumeration<InetAddress> addresses = iface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress addr = addresses.nextElement();
                    if (!addr.isLoopbackAddress() && !addr.isLinkLocalAddress() && addr.isSiteLocalAddress()) {
                        return addr.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }
}