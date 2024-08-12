package benicio.soluces.dimensional.activitys;

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

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.concurrent.ExecutionException;

import benicio.soluces.dimensional.R;
import benicio.soluces.dimensional.databinding.ActivityBaterFotoArvoreBinding;
import benicio.soluces.dimensional.databinding.ActivityMenuBinding;
import benicio.soluces.dimensional.model.ItemRelatorio;

public class BaterFotoArvoreActivity extends AppCompatActivity {

    private ActivityBaterFotoArvoreBinding mainBinding;
    int cameraFacing = CameraSelector.LENS_FACING_BACK;

    private PreviewView previewView;

    private static final int PERMISSIONS_GERAL = 1;

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
        mainBinding = ActivityBaterFotoArvoreBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );

        mainBinding.backButton3.setOnClickListener(view -> finish());

        previewView = mainBinding.cameraPreview;

        mainBinding.flipcam.setOnClickListener( view -> {
            if ( cameraFacing == CameraSelector.LENS_FACING_BACK){
                cameraFacing = CameraSelector.LENS_FACING_FRONT;
            }else{
                cameraFacing = CameraSelector.LENS_FACING_BACK;
            }
            startCamera(cameraFacing);
        });


        if (
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera(cameraFacing);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA}, PERMISSIONS_GERAL);
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

                mainBinding.capture.setOnClickListener( view -> {
                    File photoFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                            System.currentTimeMillis() + ".jpg");

                    ImageCapture.OutputFileOptions outputOptions = new ImageCapture.OutputFileOptions.Builder(photoFile).build();
                    imageCapture.takePicture(outputOptions, ContextCompat.getMainExecutor(this), new ImageCapture.OnImageSavedCallback() {
                        @Override
                        public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                            Uri savedUri = Uri.fromFile(photoFile);
                            // Aqui você pode fazer o que quiser com o URI, como salvar em uma variável
                            Log.d("ImageCapture", "Imagem salva em: " + savedUri.toString());

                            // Salva a imagem na galeria
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.MediaColumns.DISPLAY_NAME, photoFile.getName());
                            values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

                            ContentResolver resolver = getContentResolver();
                            Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                            try (OutputStream out = resolver.openOutputStream(imageUri)) {
                                Files.copy(photoFile.toPath(), out);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            // Inicie outra atividade ou faça qualquer outra ação
                            Intent i = new Intent(BaterFotoArvoreActivity.this, SetarComprimentoToraActivity.class);
                            i.putExtra("link", savedUri.toString());
                            startActivity(i);
                        }

                        @Override
                        public void onError(@NonNull ImageCaptureException exception) {
                            Log.e("ImageCapture", "Erro ao salvar imagem: " + exception.getMessage());
                        }
                    });
//                    startActivity(new Intent(this, SetarComprimentoToraActivity.class));
                });


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
}