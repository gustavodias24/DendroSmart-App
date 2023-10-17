package benicio.soluces.dimensional;

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

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

import benicio.soluces.dimensional.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final int PERMISSIONS_GERAL = 1;
    int cameraFacing = CameraSelector.LENS_FACING_BACK;
    private PreviewView previewView;
    private  final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if ( result ){
                startCamera(cameraFacing);
            }
        }
    });
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        previewView = binding.cameraPreview;

        if (
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera(cameraFacing);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.CAMERA }, PERMISSIONS_GERAL);
        }

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
                    Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
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

    public String calcularTamanhoDaTela(){
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

        return "Altura: " + String.format("%.2f", heightCm) + " cm\n"
                + "Largura: " + String.format("%.2f", widthCm) + " cm";
    }
}