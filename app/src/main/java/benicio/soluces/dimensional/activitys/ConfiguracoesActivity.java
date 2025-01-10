package benicio.soluces.dimensional.activitys;

import static benicio.soluces.dimensional.activitys.MainActivity.getIPAddress;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

import benicio.soluces.dimensional.R;
import benicio.soluces.dimensional.databinding.ActivityConfiguracoesBinding;

public class ConfiguracoesActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private ActivityConfiguracoesBinding mainBinding;
    private TextView textIp;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityConfiguracoesBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getSupportActionBar().setTitle("Configurações");


        mainBinding.dapField.setOnFocusChangeListener((v, focus) -> {
            if (focus)
                mainBinding.dapField.setText("");
        });
        mainBinding.toleranciaField.setOnFocusChangeListener((v, focus) -> {
            if (focus)
                mainBinding.toleranciaField.setText("");
        });
        mainBinding.fatorCorretivoField.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                mainBinding.fatorCorretivoField.setText("");
            }
        });

        mainBinding.zoomInicialField.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                mainBinding.zoomInicialField.setText("");
            }
        });

        mainBinding.zoomMaxField.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                mainBinding.zoomMaxField.setText("");
            }
        });

        mainBinding.trocarSenhaAdm.setOnClickListener(v -> {
            String old_pass = mainBinding.velhaSenhaAdm.getText().toString();
            String new_pass = mainBinding.novaSenhaAdm.getText().toString();

            String atual_pass = getSharedPreferences("preferencias_usuario", MODE_PRIVATE).getString("senhaAdmin", "123");

            if (old_pass.equals(atual_pass)) {
                getSharedPreferences("preferencias_usuario", MODE_PRIVATE).edit().putString("senhaAdmin", new_pass).apply();
                Toast.makeText(this, "Senha trocada!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Senha do adm antiga errada", Toast.LENGTH_SHORT).show();
            }


        });

        String androidVersion = "Android " + Build.VERSION.RELEASE;

        // Obtendo o tamanho da tela
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        String screenSize = screenWidth + "x" + screenHeight;

        // Obtendo a marca e o modelo do dispositivo
        String deviceManufacturer = Build.MANUFACTURER;
        String deviceModel = Build.MODEL;

        // Construindo a string formatada
        StringBuilder deviceInfo = new StringBuilder();
        deviceInfo.append("* Informações Sobre o Aparelho *").append("\n");
        deviceInfo.append("Android Version: ").append(androidVersion).append("\n");
        deviceInfo.append("Screen Size: ").append(screenSize).append("\n");
        deviceInfo.append("Manufacturer: ").append(deviceManufacturer).append("\n");
        deviceInfo.append("Model: ").append(deviceModel);


        mainBinding.informacaoDoAparelho.setText(deviceInfo.toString());

        textIp = findViewById(R.id.textIpConfig);

        new Thread(() -> {
            while (true) {
                runOnUiThread(() -> textIp.setText("IP do controle: " + getIPAddress()));
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        preferences = getSharedPreferences("configPreferences", Context.MODE_PRIVATE);
        editor = preferences.edit();

        mainBinding.fatorCorretivoField.setText(
                String.valueOf(
                        preferences.getFloat("corretivo", 0.48484848f)
                ).replace(".", ",")
        );

        configurarDadosSalvos();

        mainBinding.logo.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        });

        mainBinding.dapField.setText(
                preferences.getString("dap", "")
        );

        mainBinding.toleranciaField.setText(
                preferences.getString("tolerancia", "0,10").replace(".", ",")
        );

        Objects.requireNonNull(mainBinding.zoomMaxField).setText(
                preferences.getInt("zoomMaximo", 8) + ""
        );

        Objects.requireNonNull(mainBinding.zoomInicialField).setText(
                preferences.getInt("zoomInicial", 4) + ""
        );


        mainBinding.exibirGps.setOnClickListener(view -> {
            if (mainBinding.exibirGps.isChecked()) {
                editor.putBoolean("gps", true);
            } else {
                editor.putBoolean("gps", false);
            }

            editor.apply();

        });

        mainBinding.btnSalvar.setOnClickListener(view -> {

            String senhaDigitada = mainBinding.senhaField.getEditText().getText().toString();

            if (senhaDigitada.equals(getSharedPreferences("preferencias_usuario", MODE_PRIVATE).getString("token", "123"))) {


                editor.putString("dap", mainBinding.dapField.getText().toString()).apply();
                editor.putString("tolerancia", mainBinding.toleranciaField.getText().toString().replace(",", ".")).apply();
                editor.putInt("zoomMaximo", Integer.parseInt(mainBinding.zoomMaxField.getText().toString())).apply();
                editor.putInt("zoomInicial", Integer.parseInt(mainBinding.zoomInicialField.getText().toString())).apply();
                try {
                    String fatorString = mainBinding.fatorCorretivoField.getText().toString().replace(",", ".").trim();
                    editor.putFloat("corretivo",
                            Float.parseFloat(fatorString)).apply();
                    Toast.makeText(this, "Configurações salvas!", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (Exception e) {
                    Toast.makeText(this, "Valor de fator inválido", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Senha Incorreta", Toast.LENGTH_SHORT).show();
            }


        });
        mainBinding.btnDefault.setOnClickListener(v -> {
            editor.remove("dap").apply();
            editor.putString("tolerancia", "0,10").apply();
            editor.putInt("zoomMaximo", 8).apply();
            finish();
            startActivity(new Intent(this, ConfiguracoesActivity.class));
            Toast.makeText(this, "Atualizado!", Toast.LENGTH_SHORT).show();
        });
    }

    private void configurarDadosSalvos() {
        if (preferences.getString("logoImage", null) != null) {
            byte[] decodedBytes = Base64.decode(preferences.getString("logoImage", null), Base64.DEFAULT);
            Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
            mainBinding.logo.setImageBitmap(decodedBitmap);
        }

        mainBinding.exibirGps.setChecked(
                preferences.getBoolean("gps", false)
        );
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public static String imageToBase64(Uri imageUri, Context c) {
        try {
            InputStream inputStream = c.getContentResolver().openInputStream(imageUri);
            byte[] bytes = new byte[inputStream.available()];
            inputStream.read(bytes);
            return Base64.encodeToString(bytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("imageUtils", e.getMessage());
        }
        return "";
    }
}