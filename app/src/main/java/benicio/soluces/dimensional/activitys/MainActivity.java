package benicio.soluces.dimensional.activitys;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.common.util.concurrent.ListenableFuture;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import benicio.soluces.dimensional.R;
import benicio.soluces.dimensional.model.ItemRelatorio;
import benicio.soluces.dimensional.utils.AudioIA;
import benicio.soluces.dimensional.utils.Converter;
import benicio.soluces.dimensional.utils.ItemRelatorioUtil;
import benicio.soluces.dimensional.utils.ListaBarrinhasUtils;
import benicio.soluces.dimensional.utils.MetodosUtils;
import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {

    private static final String TAG = "dimensional";
    private static final int PERMISSIONS_GERAL = 1;

    private static final int ALTURA_BARRINHA_NORMAL = 30;
    private static final int ALTURA_BARRINHA_AUMENTADA = 70;

    public static final String MSG1 = "Aponte para a base da árvore, em seguida clique em medir.";
    public static final String MSG2 = "Aponte para o topo da árvore, em seguida clique em medir.";

    private enum MetodoVolume { NEWTON, SMALIAN }
    private enum AcaoPressionada { MAIS_Y, MENOS_Y, MAIS_R, MENOS_R, NONE }

    // =========================
    // Estado
    // =========================
    private SharedPreferences preferences;

    private float tolerancia = 0.0f;
    private float CONST_CHAVE = 0.054347826f;

    private float dh = 0f;
    private float tamCadaParte = 0f;

    private float alturaCalc = 0.0f;
    private float disDireta = 0.0f;

    private int etapa = 0;
    private float anguloB = 0f;
    private float anguloT = 0f;

    private float anguloBaseTora = 0.0f;
    private float anguloAtualTora = 0.0f;
    private float alturaDesejada = 0.0f;

    private float alturaAtualTora = 0.0f;
    private String alturaAtualToraString = "0.0 m";

    private int qtdDivisao = 0;
    private float toraDaponta = 0.0f;
    private int toraAtual = 1;

    private float diametroMarcado = 0.0f;
    private float diametroBaseTora = 0.0f;
    private float diametroMedioTora = 0.0f;
    private float diametroTopoTora = 0.0f;
    private float ultimoDiametroBase = 0.0f;

    private float volumeTotal = 0.0f;

    private boolean smalianFirstCalc = true;
    private boolean acabouToras = false;

    private String parteDaTora = "a base";
    private int parteDaToraPos = 1;

    private int audioAtual = 0;
    private boolean jaClicou = false;

    // Barrinhas / Zoom
    private int divisorPorZoom = 1;
    private int qtdBarrinhas = 8;
    private int qtdPos;
    private int indexy = 3;
    private int indexr = 3;
    private final List<Integer> listay = new ArrayList<>();
    private final List<Integer> listar = new ArrayList<>();

    private float currentZoomLevel = 4.0f;
    private float maxZoomLevel = 8f;
    private float maxZoomLevelReal = 1f;

    // Long press
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean longPressing = false;
    private boolean firstTime = true;
    private int tempoEspera = 500;
    private AcaoPressionada acaoPressionada = AcaoPressionada.NONE;
    private Runnable longPressRunnable;

    // GPS
    private Double latitude, longitude;
    private FusedLocationProviderClient fusedLocationClient;
    private Runnable gpsRunnable;

    // Sensor
    private long lastUpdate;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private float lastAccelX, lastAccelY, lastAccelZ;

    // CameraX (SAFE)
    private PreviewView previewView;
    private Camera mCamera;
    private ProcessCameraProvider cameraProvider;
    private ImageCapture imageCapture;
    private ExecutorService cameraExecutor;

    // UI
    private GifImageView imagemIlustrativaArvore;
    private TextView alturaAtual;
    private TextView instrucaoTela;
    private TextView infosGenericas;
    private TextView infoMedirTora;
    private TextView text_tora_apontamento_counter;

    private TextView anguloBText, anguloTText, setinha, alturaReal;
    private ImageButton medirAngulo, medirDiametro;

    private LinearLayout barrinhasLayout;
    private ImageButton btnMaisRed, btnMenosRed, btnMaisYellow, btnMenosYellow, maisZoom, menosZoom;
    private TextView qtdBarrinhasText;
    private TextView textZoom, dadosGps, medidaRealText;
    private ImageView imageEmpresa;

    private ImageButton relatoriobtn;
    private ImageButton restartButton;

    private MediaPlayer mediaPlayer;
    private MediaPlayer mediaPlayerError;

    private ItemRelatorio itemRelatorio;

    // =========================
    // Lifecycle
    // =========================
    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        preferences = getSharedPreferences("configPreferences", Context.MODE_PRIVATE);

        mediaPlayer = MediaPlayer.create(this, R.raw.bip);
        mediaPlayerError = MediaPlayer.create(this, R.raw.somerro);

        AudioIA.pararAudio();
        AudioIA.tocarAudio(this, R.raw.basedaarvore);

        // extras
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            dh = bundle.getFloat("dh", 0f);
            tamCadaParte = bundle.getFloat("tamCadaParte", 0f);
        }

        currentZoomLevel = preferences.getInt("zoomInicial", 4);

        bindViews();
        setupUI();
        setupListeners();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        ListaBarrinhasUtils.preencherListas(listay, listar, this);

        atualizarContagemBarrinhas();
        configurarInstrucaoTelaBlink();
        pegarZoomMaximoReal(); // sem bind de câmera

        itemRelatorio = new ItemRelatorio();
        itemRelatorio.setDh(String.valueOf(dh));
        itemRelatorio.setTamanhoCadaTora(String.valueOf(tamCadaParte));
        if (bundle != null) itemRelatorio.setImagemArvore(bundle.getString("link", ""));

        // Camera executor único (evita crash de thread solta)
        cameraExecutor = Executors.newSingleThreadExecutor();

        // sensor
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        // permissões
        if (!hasPermissoes()) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CAMERA
                    },
                    PERMISSIONS_GERAL
            );
        }

        // gps periódico
        startGpsLoop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        tolerancia = parseFloatPref(preferences, "tolerancia", "0,10");
        CONST_CHAVE = applyFatorCorretivo(CONST_CHAVE, dh, preferences);

        // só inicia câmera quando a Activity está “viva”
        if (hasCameraPermission()) {
            startCamera(CameraSelector.LENS_FACING_BACK);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        pegarConfiguracoesAtuais();

        if (accelerometer == null) {
            Toast.makeText(this, "Sensor acelerômetro não encontrado no dispositivo", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        stopRepeatingTask();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopGpsLoop();
        stopCameraSafely();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopGpsLoop();
        stopCameraSafely();

        if (cameraExecutor != null) {
            cameraExecutor.shutdownNow();
            cameraExecutor = null;
        }

        releasePlayer(mediaPlayer);
        releasePlayer(mediaPlayerError);
    }

    private void releasePlayer(MediaPlayer mp) {
        try {
            if (mp != null) {
                mp.stop();
                mp.release();
            }
        } catch (Throwable ignored) {}
    }

    // =========================
    // Bind / Setup
    // =========================
    private void bindViews() {
        relatoriobtn = findViewById(R.id.relatoriobtn);
        restartButton = findViewById(R.id.restartButton);

        imagemIlustrativaArvore = findViewById(R.id.imagemIlustrativaArvore);
        alturaAtual = findViewById(R.id.infos_altura_atual);

        instrucaoTela = findViewById(R.id.instrucao_text);
        infosGenericas = findViewById(R.id.infos_dev_text);
        infoMedirTora = findViewById(R.id.info_medir_volume_tora);

        text_tora_apontamento_counter = findViewById(R.id.text_tora_apontamento_counter);

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
        medidaRealText = findViewById(R.id.medida_real_text);
        imageEmpresa = findViewById(R.id.logoEmpresa);

        previewView = findViewById(R.id.camera_preview);
    }

    @SuppressLint("SetTextI18n")
    private void setupUI() {
        qtdPos = qtdBarrinhas + (qtdBarrinhas - 1);
        textZoom.setText(((int) currentZoomLevel) + " X");
        animacaoBotaoZoom();

        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        relatoriobtn.setOnClickListener(v -> startActivity(new Intent(this, RelatoriosActivity.class)));

        restartButton.setOnClickListener(v -> {
            Toast.makeText(this, "Reiniciando...", Toast.LENGTH_SHORT).show();
            finish();
            startActivity(new Intent(this, BaterFotoArvoreActivity.class));
        });

        medirAngulo.setOnClickListener(v -> onMedirAlturaClick());
        medirDiametro.setOnClickListener(this);

        findViewById(R.id.maisyelow).setOnClickListener(this);
        findViewById(R.id.menosyelow).setOnClickListener(this);
        findViewById(R.id.maisred).setOnClickListener(this);
        findViewById(R.id.menosred).setOnClickListener(this);
        findViewById(R.id.mais_zoom).setOnClickListener(this);
        findViewById(R.id.menos_zoom).setOnClickListener(this);
        findViewById(R.id.configuracoes).setOnClickListener(this);

        // print (agora seguro)
        findViewById(R.id.print).setOnClickListener(v -> {
            if (imageCapture == null) {
                Toast.makeText(this, "Câmera ainda não pronta.", Toast.LENGTH_SHORT).show();
                return;
            }
            takePrint(imageCapture);
        });
    }

    private void setupListeners() {
        configurarEventoDePressionarZoom();
        configurarIncrementoDecrementoAutomatico();
    }

    // =========================
    // Clicks
    // =========================
    @Override
    public void onClick(View view) {
        int id = view.getId();

        if (id == R.id.maisred) { aumentarVermelho(); return; }
        if (id == R.id.menosred) { diminuirVermelho(); return; }
        if (id == R.id.maisyelow) { aumentarAmerelo(); return; }
        if (id == R.id.menosyelow) { diminuirAmerelo(); return; }
        if (id == R.id.mais_zoom) { maisZoomFuncao(); return; }
        if (id == R.id.menos_zoom) { menosZoomFuncao(); return; }
        if (id == R.id.configuracoes) { startActivity(new Intent(this, ConfiguracoesActivity.class)); return; }

        if (medirDiametro != null && id == medirDiametro.getId()) {
            onMedirDiametroClick();
        }
    }

    private void onMedirAlturaClick() {
        safePlay(mediaPlayer);

        if (etapa < 2) etapa++;
        if (etapa == 1) {
            imagemIlustrativaArvore.setImageResource(R.drawable.angulo_topo_arvore);

            if (!jaClicou) {
                instrucaoTela.setText(MSG2);
                jaClicou = true;
                AudioIA.pararAudio();
                AudioIA.tocarAudio(this, R.raw.topoarvore);
            } else {
                AudioIA.pararAudio();
                AudioIA.tocarAudio(this, R.raw.basetora);
                audioAtual = 0;
            }
            return;
        }

        if (etapa == 2) {
            if (dh == 0f) return;
            calculateMeasureHeight();
            mudarParaMedidorDiametro();
            calcularQuantidadeTora();
        }
    }

    private void onMedirDiametroClick() {
        float alturaInstant = parseAlturaInstantanea();
        atualizarAudioTora();

        if (!alturaDentroDaTolerancia(alturaInstant, alturaDesejada, tolerancia)) {
            safePlay(mediaPlayerError);
            Toast.makeText(this, getMsgTolerancia(alturaInstant), Toast.LENGTH_SHORT).show();
            return;
        }

        safePlay(mediaPlayer);
        aplicarMetodoSelecionado();
    }

    private void safePlay(MediaPlayer mp) {
        try {
            if (mp != null) mp.start();
        } catch (Throwable ignored) {}
    }

    // =========================
    // Método Newton / Smalian
    // =========================
    private MetodoVolume getMetodo() {
        return "Newton".equals(preferences.getString("metodo", "")) ? MetodoVolume.NEWTON : MetodoVolume.SMALIAN;
    }

    private void aplicarMetodoSelecionado() {
        if (getMetodo() == MetodoVolume.NEWTON) configurarSwitchNewton();
        else configurarSwitchSmalian();
    }

    private void configurarSwitchSmalian() {
        if (parteDaToraPos == 1) {
            diametroBaseTora = diametroMarcado;
            parteDaToraPos = 2;
            parteDaTora = "o topo";
            alturaDesejada += (float) calcularIncremento(alturaCalc - toraDaponta, tamCadaParte);
            return;
        }

        if (!smalianFirstCalc) diametroBaseTora = diametroTopoTora;
        smalianFirstCalc = false;

        diametroTopoTora = diametroMarcado;
        alturaDesejada += (float) calcularIncremento(alturaCalc - toraDaponta, tamCadaParte);

        finalizarOuProximaTora();
    }

    private void configurarSwitchNewton() {
        if (parteDaToraPos == 1) {
            diametroBaseTora = diametroMarcado;
            parteDaToraPos = 2;
            parteDaTora = "o centro";
            alturaDesejada += (float) calcularIncremento(alturaCalc - toraDaponta, tamCadaParte);
            return;
        }

        if (parteDaToraPos == 2) {
            diametroMedioTora = diametroMarcado;
            parteDaToraPos = 3;
            parteDaTora = "o topo";
            alturaDesejada += (float) calcularIncremento(alturaCalc - toraDaponta, tamCadaParte);
            return;
        }

        diametroTopoTora = diametroMarcado;
        alturaDesejada += (float) calcularIncremento(alturaCalc - toraDaponta, tamCadaParte);

        parteDaTora = "o centro";
        parteDaToraPos = 2;

        diametroBaseTora = ultimoDiametroBase;
        finalizarOuProximaTora();
    }

    private void finalizarOuProximaTora() {
        if (toraAtual < qtdDivisao) {
            fazerCaluloVolume();
        } else {
            fazerCalculoFinalVolume();
            salvarArvoreRelatorio();
        }
        toraAtual++;
    }

    public double calcularIncremento(double alturaArvore, double comprimentoTora) {
        return comprimentoTora / 2.0;
    }

    private float calcularVolume(float comprimento) {
        float rBase = diametroBaseTora / 2f;
        float rMedio = diametroMedioTora / 2f;
        float rTopo = diametroTopoTora / 2f;

        double v = (getMetodo() == MetodoVolume.NEWTON)
                ? MetodosUtils.novoCalculoNewton(rBase, rMedio, rTopo, comprimento)
                : MetodosUtils.calculoSmalian(rBase, rTopo, comprimento);

        return (float) v;
    }

    private void fazerCaluloVolume() {
        float volume = calcularVolume(tamCadaParte);
        volumeTotal += volume;

        infosGenericas.append("\n" + String.format(Locale.US, "Volume Tora %d: %.4f m³", toraAtual, volume));

        ultimoDiametroBase = diametroTopoTora;
        resetDiametros();
    }

    private void fazerCalculoFinalVolume() {
        finalizarUIFinal();

        float volumeUltima = calcularVolume(tamCadaParte);
        volumeTotal += volumeUltima;

        float volumePonta = calcularVolume(toraDaponta);

        infosGenericas.append("\n" + String.format(Locale.US, "Volume Tora %d: %.4f m³", toraAtual, volumeUltima));
        infosGenericas.append("\n" + String.format(Locale.US, "Volume da ponta: %.4f m³", volumePonta));
        infosGenericas.append("\n" + String.format(Locale.US, "Volume total: %.4f m³", volumeTotal));
    }

    private void resetDiametros() {
        diametroTopoTora = 0f;
        diametroMedioTora = 0f;
        diametroBaseTora = 0f;
    }

    private void finalizarUIFinal() {
        medirDiametro.setVisibility(View.GONE);
        instrucaoTela.clearAnimation();
        instrucaoTela.setVisibility(View.INVISIBLE);
        acabouToras = true;
    }

    // =========================
    // UI Helpers
    // =========================
    private void configurarInstrucaoTelaBlink() {
        Animation blink = new AlphaAnimation(1, 0);
        blink.setDuration(500);
        blink.setRepeatMode(Animation.REVERSE);
        blink.setRepeatCount(Animation.INFINITE);

        instrucaoTela.startAnimation(blink);
        medirAngulo.startAnimation(blink);
        medirDiametro.startAnimation(blink);
    }

    private void animacaoBotaoZoom() {
        AlphaAnimation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setDuration(1000);

        AlphaAnimation fadeOut = new AlphaAnimation(1f, 0f);
        fadeOut.setDuration(1000);

        textZoom.startAnimation(fadeIn);

        handler.postDelayed(() -> {
            textZoom.startAnimation(fadeOut);
            textZoom.setVisibility(View.INVISIBLE);
        }, 2000);
    }

    private void mudarParaMedidorDiametro() {
        show(View.VISIBLE,
                maisZoom, menosZoom,
                btnMaisRed, btnMenosRed,
                btnMaisYellow, btnMenosYellow,
                barrinhasLayout, medidaRealText,
                text_tora_apontamento_counter,
                medirDiametro, alturaAtual
        );

        findViewById(R.id.barrinha_vermelha_horizontal).setVisibility(View.VISIBLE);
        findViewById(R.id.barrinha_amarela_horizontal).setVisibility(View.VISIBLE);

        imagemIlustrativaArvore.setImageResource(R.drawable.aponta_p_base);

        anguloBText.setVisibility(View.INVISIBLE);
        anguloTText.setVisibility(View.INVISIBLE);
        setinha.setVisibility(View.INVISIBLE);

        medirAngulo.clearAnimation();
        medirAngulo.setVisibility(View.GONE);
    }

    private void show(int visibility, View... views) {
        for (View v : views) v.setVisibility(visibility);
    }

    // =========================
    // Altura / Tora
    // =========================
    @SuppressLint("DefaultLocale")
    public void calculateMeasureHeight() {
        float ta = (float) Math.tan(Math.toRadians(anguloB));
        float tb = (float) Math.tan(Math.toRadians(anguloT));

        float a = Math.abs(ta);
        float b = Math.abs(tb);

        boolean mesmoSinal = (ta > 0 && tb > 0) || (ta < 0 && tb < 0);
        alturaCalc = dh * (mesmoSinal ? (a - b) : (a + b));
        alturaCalc = Math.abs(alturaCalc);

        alturaReal.setText(String.format(Locale.US, "Altura Total\n%.2f m", alturaCalc));
        alturaReal.setVisibility(View.VISIBLE);
    }

    @SuppressLint("DefaultLocale")
    public void calcularAlturaTora() {
        float ta = (float) Math.tan(Math.toRadians(anguloBaseTora));
        float tb = (float) Math.tan(Math.toRadians(anguloAtualTora));

        float a = Math.abs(ta);
        float b = Math.abs(tb);

        boolean mesmoSinal = (ta > 0 && tb > 0) || (ta < 0 && tb < 0);
        alturaAtualTora = dh * (mesmoSinal ? (a - b) : (a + b));
        alturaAtualTora = Math.abs(alturaAtualTora);

        alturaAtualToraString = String.format(Locale.US, " %.2f m", alturaAtualTora);
    }

    private void calcularQuantidadeTora() {
        if (tamCadaParte <= 0f) return;

        qtdDivisao = (int) Math.floor(alturaCalc / tamCadaParte);
        toraDaponta = alturaCalc % tamCadaParte;

        infosGenericas.setText(String.format(Locale.US,
                "%d tora(s) de %.2f m\ntora da ponta: %.2f m",
                qtdDivisao, tamCadaParte, toraDaponta
        ));
    }

    // =========================
    // GPS (SAFE)
    // =========================
    private void startGpsLoop() {
        stopGpsLoop();
        gpsRunnable = new Runnable() {
            @Override public void run() {
                if (isFinishing() || isDestroyed()) return;
                pegarLocalizacao();
                handler.postDelayed(this, 3000);
            }
        };
        handler.post(gpsRunnable);
    }

    private void stopGpsLoop() {
        if (gpsRunnable != null) {
            handler.removeCallbacks(gpsRunnable);
            gpsRunnable = null;
        }
    }

    public void pegarLocalizacao() {
        if (!hasLocationPermission()) return;

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
            if (location == null || isFinishing() || isDestroyed()) return;

            latitude = location.getLatitude();
            longitude = location.getLongitude();
            configurarTextInfos();
        });
    }

    @SuppressLint("SetTextI18n")
    private void configurarTextInfos() {
        Calendar calendar = Calendar.getInstance();
        Date currentDate = calendar.getTime();

        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        java.text.SimpleDateFormat timeFormat = new java.text.SimpleDateFormat("HH:mm:ss", Locale.getDefault());

        String formattedDate = dateFormat.format(currentDate);
        String formattedTime = timeFormat.format(currentDate);

        String operador = preferences.getString("operador", "");
        if (operador == null || operador.trim().isEmpty()) operador = "Nome não informado.";

        String coordenadas = String.format(Locale.US, "Lat: %f Long: %f", latitude, longitude);

        itemRelatorio.setLatitude(String.valueOf(latitude));
        itemRelatorio.setLongitude(String.valueOf(longitude));

        String baseText = formattedDate + " ás " + formattedTime + "\n" +
                coordenadas + "\n" +
                "Operador: " + operador + "\n" +
                "Método: " + preferences.getString("metodo", "");

        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null && !addresses.isEmpty()) {
                Address address = addresses.get(0);
                String[] fullAddress = address.getAddressLine(0).split(",");
                if (fullAddress.length >= 2) {
                    dadosGps.setText(fullAddress[0] + ", " + fullAddress[1] + "\n" + baseText);
                    return;
                }
            }
        } catch (IOException ignored) {}

        dadosGps.setText(baseText);
    }

    private void pegarConfiguracoesAtuais() {
        String logoBase64 = preferences.getString("logoImage", null);
        if (logoBase64 != null) {
            byte[] decoded = Base64.decode(logoBase64, Base64.DEFAULT);
            Bitmap decodedBitmap = android.graphics.BitmapFactory.decodeByteArray(decoded, 0, decoded.length);
            imageEmpresa.setImageBitmap(decodedBitmap);
            imageEmpresa.setVisibility(View.VISIBLE);
        }
        dadosGps.setVisibility(preferences.getBoolean("gps", true) ? View.VISIBLE : View.INVISIBLE);
    }

    // =========================
    // Barrinhas / Diâmetro
    // =========================
    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void atualizarContagemBarrinhas() {
        qtdPos = qtdBarrinhas + (qtdBarrinhas - 1);
        qtdBarrinhasText.setText(String.valueOf(qtdPos));

        if (divisorPorZoom == 0) divisorPorZoom = 1;

        diametroMarcado = ((dh * (qtdPos / (float) divisorPorZoom) * CONST_CHAVE) / 100f);
        medidaRealText.setText(String.format(Locale.US, "DIÂMETRO %.4f m", diametroMarcado));
    }

    private void aumentarAmerelo() {
        if (indexy >= (listay.size() - 1)) return;

        qtdBarrinhas++;
        indexy++;
        atualizarContagemBarrinhas();

        ajustarAlturaBarrinha(listay, indexy, ALTURA_BARRINHA_AUMENTADA);
        ajustarAlturaBarrinha(listay, indexy - 1, ALTURA_BARRINHA_NORMAL);

        findViewById(listay.get(indexy)).setVisibility(View.VISIBLE);
    }

    private void diminuirAmerelo() {
        if (indexy < 1) return;

        qtdBarrinhas--;
        atualizarContagemBarrinhas();

        findViewById(listay.get(indexy)).setVisibility(View.INVISIBLE);
        ajustarAlturaBarrinha(listay, indexy - 1, ALTURA_BARRINHA_AUMENTADA);
        ajustarAlturaBarrinha(listay, indexy, ALTURA_BARRINHA_NORMAL);

        indexy--;
    }

    private void aumentarVermelho() {
        if (indexr >= (listar.size() - 1)) return;

        qtdBarrinhas++;
        indexr++;
        atualizarContagemBarrinhas();

        findViewById(listar.get(indexr)).setVisibility(View.VISIBLE);
        ajustarAlturaBarrinha(listar, Math.max(indexr - 1, 0), ALTURA_BARRINHA_NORMAL);
        ajustarAlturaBarrinha(listar, indexr, ALTURA_BARRINHA_AUMENTADA);
    }

    private void diminuirVermelho() {
        if (indexr < 1) return;

        qtdBarrinhas--;
        atualizarContagemBarrinhas();

        findViewById(listar.get(indexr)).setVisibility(View.INVISIBLE);
        ajustarAlturaBarrinha(listar, Math.max(indexr - 1, 0), ALTURA_BARRINHA_AUMENTADA);
        ajustarAlturaBarrinha(listar, indexr, ALTURA_BARRINHA_NORMAL);

        indexr--;
    }

    private void ajustarAlturaBarrinha(List<Integer> lista, int index, int alturaDp) {
        if (index < 0 || index >= lista.size()) return;
        View v = findViewById(lista.get(index));
        v.getLayoutParams().height = Converter.dpToPixels(this, alturaDp);
        v.requestLayout();
    }

    // =========================
    // Zoom (SAFE)
    // =========================
    @SuppressLint("ClickableViewAccessibility")
    private void configurarEventoDePressionarZoom() {
        findViewById(R.id.mais_zoom).setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) maisZoomFuncao();
            return true;
        });

        findViewById(R.id.menos_zoom).setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) menosZoomFuncao();
            return true;
        });
    }

    private void atualizarZoom(float novoZoom) {
        float zoomMinimo = preferences.getInt("zoomInicial", 4);
        currentZoomLevel = Math.max(zoomMinimo, Math.min(novoZoom, maxZoomLevel));

        if (mCamera != null) {
            try { mCamera.getCameraControl().setZoomRatio(currentZoomLevel); } catch (Throwable ignored) {}
        }

        textZoom.setVisibility(View.VISIBLE);
        textZoom.setText(((int) currentZoomLevel) + " X");
        animacaoBotaoZoom();
    }

    private void menosZoomFuncao() {
        float zoomMinimo = preferences.getInt("zoomInicial", 4);
        if (currentZoomLevel <= zoomMinimo) return;

        divisorPorZoom = Math.max(1, divisorPorZoom / 2);
        atualizarZoom(currentZoomLevel / 2f);
        atualizarContagemBarrinhas();
    }

    private void maisZoomFuncao() {
        if (currentZoomLevel >= maxZoomLevel) return;

        divisorPorZoom = divisorPorZoom * 2;
        atualizarZoom(currentZoomLevel * 2f);
        atualizarContagemBarrinhas();

        if (currentZoomLevel > maxZoomLevelReal) {
            Toast.makeText(this, "Zoom máximo real: " + maxZoomLevelReal, Toast.LENGTH_SHORT).show();
        }
    }

    // =========================
    // Long press (SAFE)
    // =========================
    @SuppressLint("ClickableViewAccessibility")
    public void configurarIncrementoDecrementoAutomatico() {
        setupRepeatTouch(R.id.maisyelow, AcaoPressionada.MAIS_Y);
        setupRepeatTouch(R.id.menosyelow, AcaoPressionada.MENOS_Y);
        setupRepeatTouch(R.id.maisred, AcaoPressionada.MAIS_R);
        setupRepeatTouch(R.id.menosred, AcaoPressionada.MENOS_R);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupRepeatTouch(int viewId, AcaoPressionada acao) {
        findViewById(viewId).setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    acaoPressionada = acao;
                    longPressing = true;
                    startRepeatingTask();
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    longPressing = false;
                    stopRepeatingTask();
                    acaoPressionada = AcaoPressionada.NONE;
                    break;
            }
            return false;
        });
    }

    private void startRepeatingTask() {
        stopRepeatingTask();
        firstTime = true;
        tempoEspera = 500;

        longPressRunnable = new Runnable() {
            @Override public void run() {
                if (!longPressing) return;

                if (firstTime) {
                    firstTime = false;
                    tempoEspera = 12;
                } else {
                    executarAcaoPressionada();
                }

                handler.postDelayed(this, tempoEspera);
            }
        };

        handler.post(longPressRunnable);
    }

    private void stopRepeatingTask() {
        if (longPressRunnable != null) {
            handler.removeCallbacks(longPressRunnable);
            longPressRunnable = null;
        }
    }

    private void executarAcaoPressionada() {
        switch (acaoPressionada) {
            case MAIS_Y: aumentarAmerelo(); break;
            case MENOS_Y: diminuirAmerelo(); break;
            case MAIS_R: aumentarVermelho(); break;
            case MENOS_R: diminuirVermelho(); break;
            default: break;
        }
    }

    // =========================
    // Sensor
    // =========================
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER) return;

        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        float degrees = calcDegrees(x, y, z);

        long now = System.currentTimeMillis();
        long delta = now - lastUpdate;
        lastUpdate = now;

        if (!movimentoSuficiente(x, y, z, delta)) {
            lastAccelX = y;
            lastAccelY = x;
            lastAccelZ = z;
            return;
        }

        atualizarAngulos(degrees);
        calcularAlturaTora();
        anguloAtualTora = degrees;

        atualizarDiametroComInclinacao(degrees);
        atualizarTelaTora();

        lastAccelX = y;
        lastAccelY = x;
        lastAccelZ = z;
    }

    private float calcDegrees(float x, float y, float z) {
        float degrees = (float) Math.toDegrees(
                Math.acos(x / Math.sqrt((x * x) + (y * y) + (z * z)))
        );
        if (z > 0.0f) degrees *= -1.0f;
        return degrees;
    }

    private boolean movimentoSuficiente(float x, float y, float z, long deltaMs) {
        if (deltaMs <= 0) return true;
        double v = (Math.abs(((((y + x) + z) - lastAccelX) - lastAccelY) - lastAccelZ) / deltaMs) * 10000.0f;
        return v > 6.0f;
    }

    private void atualizarAngulos(float degrees) {
        if (dh != 0.0f && anguloB != 0.0f && etapa == 1) {
            anguloT = degrees;
            calculateMeasureHeight();
        }

        if (etapa == 0) {
            anguloBText.setText(String.format(Locale.US, "Base %.2f°", degrees));
            anguloBaseTora = degrees;
            anguloB = degrees;
        } else if (etapa == 1) {
            anguloTText.setText(String.format(Locale.US, "Topo %.2f°", degrees));
            anguloT = degrees;
        }
    }

    private void atualizarDiametroComInclinacao(float degrees) {
        double cos = Math.cos(Math.toRadians(degrees));
        if (cos == 0) return;

        disDireta = (float) (dh / cos);
        diametroMarcado = ((disDireta * (qtdPos / (float) divisorPorZoom) * CONST_CHAVE) / 100f);

        medidaRealText.setText(String.format(Locale.US, "DIÂMETRO %.4f m", diametroMarcado));
    }

    private void atualizarTelaTora() {
        if (qtdDivisao != 0) {
            text_tora_apontamento_counter.setText("T" + toraAtual);

            if ("a base".equals(parteDaTora)) imagemIlustrativaArvore.setImageResource(R.drawable.aponta_p_base);
            if ("o centro".equals(parteDaTora)) imagemIlustrativaArvore.setImageResource(R.drawable.aponta_p_meio);
            if ("o topo".equals(parteDaTora)) imagemIlustrativaArvore.setImageResource(R.drawable.aponta_p_topo);

            instrucaoTela.setText(String.format(Locale.US,
                    "Aponte para %s da %d° tora na altura %.2f m",
                    parteDaTora, toraAtual, alturaDesejada
            ));
        }

        if (anguloAtualTora < anguloBaseTora) {
            if ((-1 * alturaAtualTora) < tolerancia) {
                alturaAtual.setText(String.format(Locale.US, "ALTURA INST: -%s", alturaAtualToraString.replace("-", "")));
            } else {
                alturaAtual.setText("LEVANTE MAIS O SMARTPHONE ");
            }
        } else {
            alturaAtual.setText(String.format(Locale.US, "ALTURA INST: %s", alturaAtualToraString.replace("-", "")));
        }

        infoMedirTora.setText(String.format(Locale.US,
                "\nAltura atual: %s\nÂngulo atual: %.2f\nDiametro da base: %.2f m\nDiametro do centro: %.2f m\nDiametro do topo: %.2f m",
                alturaAtualToraString.replace("-", ""),
                anguloAtualTora,
                diametroBaseTora, diametroMedioTora, diametroTopoTora
        ));
    }

    @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    // =========================
    // CameraX (SAFE)
    // =========================
    public void startCamera(int cameraFacing) {
        if (!hasCameraPermission()) return;
        if (isFinishing() || isDestroyed()) return;

        ListenableFuture<ProcessCameraProvider> future = ProcessCameraProvider.getInstance(this);

        future.addListener(() -> {
            try {
                if (isFinishing() || isDestroyed()) return;

                cameraProvider = future.get();
                cameraProvider.unbindAll();

                Preview preview = new Preview.Builder()
                        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                        .build();

                imageCapture = new ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build();

                CameraSelector selector = new CameraSelector.Builder()
                        .requireLensFacing(cameraFacing)
                        .build();

                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                mCamera = cameraProvider.bindToLifecycle(this, selector, preview, imageCapture);

                try {
                    mCamera.getCameraControl().setZoomRatio(currentZoomLevel);
                } catch (Throwable ignored) {}

            } catch (Throwable e) {
                Log.e(TAG, "startCamera error", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void stopCameraSafely() {
        try {
            if (cameraProvider != null) {
                cameraProvider.unbindAll();
            }
        } catch (Throwable ignored) {}
        mCamera = null;
        imageCapture = null;
    }

    private void takePrint(ImageCapture capture) {
        togglePrintUI(false);
        View previewImg = findViewById(R.id.imagePreview);
        previewImg.setVisibility(View.VISIBLE);

        File file = new File(getSafeDir("DIMENSIONAL_PHOTOS"), System.currentTimeMillis() + ".png");

        ImageCapture.OutputFileOptions opts = new ImageCapture.OutputFileOptions.Builder(file).build();

        capture.takePicture(opts, cameraExecutor, new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                runOnUiThread(() -> Picasso.get().load(file).into((ImageView) previewImg, new Callback() {
                    @Override public void onSuccess() { salvarScreenShotEReabrir(); }
                    @Override public void onError(Exception e) {
                        Toast.makeText(MainActivity.this, "Erro preview: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        previewImg.setVisibility(View.GONE);
                        togglePrintUI(true);
                    }
                }));
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Erro print: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    togglePrintUI(true);
                });
            }
        });
    }

    private void salvarScreenShotEReabrir() {
        try {
            if (isFinishing() || isDestroyed()) return;

            View root = findViewById(R.id.maconha);
            if (root == null) {
                togglePrintUI(true);
                return;
            }

            Bitmap bitmap = Bitmap.createBitmap(root.getWidth(), root.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            root.draw(canvas);

            File imageFile = new File(getSafeDir("DIMENSIONAL_PARTES"), UUID.randomUUID() + ".png");

            try (FileOutputStream out = new FileOutputStream(imageFile)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            }

            Toast.makeText(this, "Imagem salva.", Toast.LENGTH_SHORT).show();
            togglePrintUI(true);

            findViewById(R.id.imagePreview).setVisibility(View.GONE);
            abrirImagem(imageFile);

        } catch (Throwable e) {
            Log.e(TAG, "salvarScreenShotEReabrir", e);
            togglePrintUI(true);
        }
    }

    private File getSafeDir(String folder) {
        File base = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File dir = new File(base, folder);
        if (!dir.exists()) dir.mkdirs();
        return dir;
    }

    private void abrirImagem(File imageFile) {
        Uri uri = FileProvider.getUriForFile(
                Objects.requireNonNull(this),
                "benicio.soluces.dimensional.provider",
                imageFile
        );

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    private void togglePrintUI(boolean visible) {
        int v = visible ? View.VISIBLE : View.INVISIBLE;
        findViewById(R.id.mais_zoom).setVisibility(v);
        findViewById(R.id.menos_zoom).setVisibility(v);
        findViewById(R.id.configuracoes).setVisibility(v);
        findViewById(R.id.maisred).setVisibility(v);
        findViewById(R.id.menosred).setVisibility(v);
        findViewById(R.id.maisyelow).setVisibility(v);
        findViewById(R.id.menosyelow).setVisibility(v);
        findViewById(R.id.print).setVisibility(v);
    }

    private void pegarZoomMaximoReal() {
        maxZoomLevel = preferences.getInt("zoomMaximo", 8);

        try {
            CameraManager cm = (CameraManager) getSystemService(CAMERA_SERVICE);
            String[] ids = cm.getCameraIdList();
            if (ids.length == 0) return;

            // tenta a traseira
            String chosen = ids[ids.length - 1];
            for (String id : ids) {
                CameraCharacteristics c = cm.getCameraCharacteristics(id);
                Integer facing = c.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                    chosen = id;
                    break;
                }
            }

            CameraCharacteristics c = cm.getCameraCharacteristics(chosen);
            Float maxZoom = c.get(CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM);
            if (maxZoom != null && maxZoom > 1f) maxZoomLevelReal = maxZoom;

        } catch (Throwable ignored) {}
    }

    // =========================
    // Relatório
    // =========================
    private void salvarArvoreRelatorio() {
        itemRelatorio.setDadosGps(dadosGps.getText().toString());
        itemRelatorio.setDadosVolume(infosGenericas.getText().toString());

        LocalDateTime agora = LocalDateTime.now();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss", Locale.getDefault());

        StringBuilder dadosTora = new StringBuilder();
        dadosTora.append("Data e Hora: ").append(agora.format(fmt)).append("\n");
        dadosTora.append("- Infos gerais -\n");
        dadosTora.append("DH: ").append(dh).append("\n");
        dadosTora.append("Altura total: ").append(alturaCalc).append("m");

        itemRelatorio.setDadosTora(dadosTora.toString());

        List<ItemRelatorio> lista = ItemRelatorioUtil.returnLista(this);
        lista.add(itemRelatorio);
        ItemRelatorioUtil.saveList(lista, this);

        relatoriobtn.setVisibility(View.VISIBLE);

        imagemIlustrativaArvore.setVisibility(View.INVISIBLE);
        text_tora_apontamento_counter.setVisibility(View.INVISIBLE);

        Toast.makeText(this, "Árvore salva no relatório.", Toast.LENGTH_SHORT).show();
        AudioIA.pararAudio();
        AudioIA.tocarAudio(this, R.raw.infossalvas);
    }

    // =========================
    // Auxiliares / Validações
    // =========================
    private float parseAlturaInstantanea() {
        try {
            return Float.parseFloat(
                    alturaAtualToraString.replace(" ", "").replace("m", "").replace(",", ".")
            );
        } catch (Exception e) {
            return 0f;
        }
    }

    private void atualizarAudioTora() {
        if (audioAtual == 0 || audioAtual == 2) {
            AudioIA.pararAudio();
            AudioIA.tocarAudio(this, R.raw.centrotora);
            audioAtual = 1;
        } else {
            AudioIA.pararAudio();
            AudioIA.tocarAudio(this, R.raw.tiopotora);
            audioAtual = 2;
        }
    }

    private boolean alturaDentroDaTolerancia(float altura, float alvo, float tol) {
        return !(altura - tol > alvo) && !(altura + tol < alvo);
    }

    private String getMsgTolerancia(float altura) {
        if ((altura - tolerancia) > alturaDesejada) return "Altura menor que a tolerância";
        return "Altura ultrapassou a tolerância";
    }

    private boolean hasPermissoes() {
        return hasCameraPermission() && hasLocationPermission();
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private static float parseFloatPref(SharedPreferences prefs, String key, String def) {
        try {
            return Float.parseFloat(prefs.getString(key, def).replace(",", "."));
        } catch (Throwable e) {
            return 0f;
        }
    }

    private static float applyFatorCorretivo(float constChave, float dh, SharedPreferences prefs) {
        int dhInt = Math.round(dh);
        if (dhInt < 3 || dhInt > 30) return constChave;

        String key = "fatorCorretivoField" + (dhInt - 2);
        float fator = prefs.getFloat(key, 0.48484848f);
        return constChave * fator;
    }

    // =========================
    // Permissões
    // =========================
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode != PERMISSIONS_GERAL) return;

        boolean ok = true;
        for (int g : grantResults) {
            if (g != PackageManager.PERMISSION_GRANTED) { ok = false; break; }
        }

        if (ok) {
            startCamera(CameraSelector.LENS_FACING_BACK);
        } else {
            Toast.makeText(this, "Permissões necessárias foram negadas.", Toast.LENGTH_LONG).show();
        }
    }
}
