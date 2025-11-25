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

        mainBinding.fatorCorretivoField1.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                mainBinding.fatorCorretivoField1.setText("");
            }
        });

        mainBinding.fatorCorretivoField2.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                mainBinding.fatorCorretivoField2.setText("");
            }
        });

        mainBinding.fatorCorretivoField3.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                mainBinding.fatorCorretivoField3.setText("");
            }
        });

        mainBinding.fatorCorretivoField4.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                mainBinding.fatorCorretivoField4.setText("");
            }
        });

        mainBinding.fatorCorretivoField5.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                mainBinding.fatorCorretivoField5.setText("");
            }
        });

        mainBinding.fatorCorretivoField6.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                mainBinding.fatorCorretivoField6.setText("");
            }
        });

        mainBinding.fatorCorretivoField7.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                mainBinding.fatorCorretivoField7.setText("");
            }
        });

        mainBinding.fatorCorretivoField8.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                mainBinding.fatorCorretivoField8.setText("");
            }
        });

        mainBinding.fatorCorretivoField9.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                mainBinding.fatorCorretivoField9.setText("");
            }
        });

        mainBinding.fatorCorretivoField10.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                mainBinding.fatorCorretivoField10.setText("");
            }
        });

        mainBinding.fatorCorretivoField11.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                mainBinding.fatorCorretivoField11.setText("");
            }
        });

        mainBinding.fatorCorretivoField12.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                mainBinding.fatorCorretivoField12.setText("");
            }
        });

        mainBinding.fatorCorretivoField13.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                mainBinding.fatorCorretivoField13.setText("");
            }
        });

        mainBinding.fatorCorretivoField14.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                mainBinding.fatorCorretivoField14.setText("");
            }
        });

        mainBinding.fatorCorretivoField15.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                mainBinding.fatorCorretivoField15.setText("");
            }
        });

        mainBinding.fatorCorretivoField16.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                mainBinding.fatorCorretivoField16.setText("");
            }
        });

        mainBinding.fatorCorretivoField17.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                mainBinding.fatorCorretivoField17.setText("");
            }
        });

        mainBinding.fatorCorretivoField18.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                mainBinding.fatorCorretivoField18.setText("");
            }
        });

        mainBinding.fatorCorretivoField19.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                mainBinding.fatorCorretivoField19.setText("");
            }
        });

        mainBinding.fatorCorretivoField20.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                mainBinding.fatorCorretivoField20.setText("");
            }
        });

        mainBinding.fatorCorretivoField21.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                mainBinding.fatorCorretivoField21.setText("");
            }
        });

        mainBinding.fatorCorretivoField22.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                mainBinding.fatorCorretivoField22.setText("");
            }
        });

        mainBinding.fatorCorretivoField23.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                mainBinding.fatorCorretivoField23.setText("");
            }
        });

        mainBinding.fatorCorretivoField24.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                mainBinding.fatorCorretivoField24.setText("");
            }
        });

        mainBinding.fatorCorretivoField25.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                mainBinding.fatorCorretivoField25.setText("");
            }
        });

        mainBinding.fatorCorretivoField26.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                mainBinding.fatorCorretivoField26.setText("");
            }
        });

        mainBinding.fatorCorretivoField27.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                mainBinding.fatorCorretivoField27.setText("");
            }
        });

        mainBinding.fatorCorretivoField28.setOnFocusChangeListener((v, focus) -> {
            if (focus) {
                mainBinding.fatorCorretivoField28.setText("");
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

        mainBinding.fatorCorretivoField1.setText(
                String.valueOf(
                        preferences.getFloat("fatorCorretivoField1", 0.48484848f)
                ).replace(".", ",")
        );

        mainBinding.fatorCorretivoField2.setText(
                String.valueOf(
                        preferences.getFloat("fatorCorretivoField2", 0.48484848f)
                ).replace(".", ",")
        );

        mainBinding.fatorCorretivoField3.setText(
                String.valueOf(
                        preferences.getFloat("fatorCorretivoField3", 0.48484848f)
                ).replace(".", ",")
        );

        mainBinding.fatorCorretivoField4.setText(
                String.valueOf(
                        preferences.getFloat("fatorCorretivoField4", 0.48484848f)
                ).replace(".", ",")
        );

        mainBinding.fatorCorretivoField5.setText(
                String.valueOf(
                        preferences.getFloat("fatorCorretivoField5", 0.48484848f)
                ).replace(".", ",")
        );

        mainBinding.fatorCorretivoField6.setText(
                String.valueOf(
                        preferences.getFloat("fatorCorretivoField6", 0.48484848f)
                ).replace(".", ",")
        );

        mainBinding.fatorCorretivoField7.setText(
                String.valueOf(
                        preferences.getFloat("fatorCorretivoField7", 0.48484848f)
                ).replace(".", ",")
        );

        mainBinding.fatorCorretivoField8.setText(
                String.valueOf(
                        preferences.getFloat("fatorCorretivoField8", 0.48484848f)
                ).replace(".", ",")
        );

        mainBinding.fatorCorretivoField9.setText(
                String.valueOf(
                        preferences.getFloat("fatorCorretivoField9", 0.48484848f)
                ).replace(".", ",")
        );

        mainBinding.fatorCorretivoField10.setText(
                String.valueOf(
                        preferences.getFloat("fatorCorretivoField10", 0.48484848f)
                ).replace(".", ",")
        );

        mainBinding.fatorCorretivoField11.setText(
                String.valueOf(
                        preferences.getFloat("fatorCorretivoField11", 0.48484848f)
                ).replace(".", ",")
        );

        mainBinding.fatorCorretivoField12.setText(
                String.valueOf(
                        preferences.getFloat("fatorCorretivoField12", 0.48484848f)
                ).replace(".", ",")
        );

        mainBinding.fatorCorretivoField13.setText(
                String.valueOf(
                        preferences.getFloat("fatorCorretivoField13", 0.48484848f)
                ).replace(".", ",")
        );

        mainBinding.fatorCorretivoField14.setText(
                String.valueOf(
                        preferences.getFloat("fatorCorretivoField14", 0.48484848f)
                ).replace(".", ",")
        );

        mainBinding.fatorCorretivoField15.setText(
                String.valueOf(
                        preferences.getFloat("fatorCorretivoField15", 0.48484848f)
                ).replace(".", ",")
        );

        mainBinding.fatorCorretivoField16.setText(
                String.valueOf(
                        preferences.getFloat("fatorCorretivoField16", 0.48484848f)
                ).replace(".", ",")
        );

        mainBinding.fatorCorretivoField17.setText(
                String.valueOf(
                        preferences.getFloat("fatorCorretivoField17", 0.48484848f)
                ).replace(".", ",")
        );

        mainBinding.fatorCorretivoField18.setText(
                String.valueOf(
                        preferences.getFloat("fatorCorretivoField18", 0.48484848f)
                ).replace(".", ",")
        );

        mainBinding.fatorCorretivoField19.setText(
                String.valueOf(
                        preferences.getFloat("fatorCorretivoField19", 0.48484848f)
                ).replace(".", ",")
        );

        mainBinding.fatorCorretivoField20.setText(
                String.valueOf(
                        preferences.getFloat("fatorCorretivoField20", 0.48484848f)
                ).replace(".", ",")
        );

        mainBinding.fatorCorretivoField21.setText(
                String.valueOf(
                        preferences.getFloat("fatorCorretivoField21", 0.48484848f)
                ).replace(".", ",")
        );

        mainBinding.fatorCorretivoField22.setText(
                String.valueOf(
                        preferences.getFloat("fatorCorretivoField22", 0.48484848f)
                ).replace(".", ",")
        );

        mainBinding.fatorCorretivoField23.setText(
                String.valueOf(
                        preferences.getFloat("fatorCorretivoField23", 0.48484848f)
                ).replace(".", ",")
        );

        mainBinding.fatorCorretivoField24.setText(
                String.valueOf(
                        preferences.getFloat("fatorCorretivoField24", 0.48484848f)
                ).replace(".", ",")
        );

        mainBinding.fatorCorretivoField25.setText(
                String.valueOf(
                        preferences.getFloat("fatorCorretivoField25", 0.48484848f)
                ).replace(".", ",")
        );

        mainBinding.fatorCorretivoField26.setText(
                String.valueOf(
                        preferences.getFloat("fatorCorretivoField26", 0.48484848f)
                ).replace(".", ",")
        );

        mainBinding.fatorCorretivoField27.setText(
                String.valueOf(
                        preferences.getFloat("fatorCorretivoField27", 0.48484848f)
                ).replace(".", ",")
        );

        mainBinding.fatorCorretivoField28.setText(
                String.valueOf(
                        preferences.getFloat("fatorCorretivoField28", 0.48484848f)
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

//            if (senhaDigitada.equals(getSharedPreferences("preferencias_usuario", MODE_PRIVATE).getString("token", "123"))) {
            if (true) {


                editor.putString("dap", mainBinding.dapField.getText().toString()).apply();
                editor.putString("tolerancia", mainBinding.toleranciaField.getText().toString().replace(",", ".")).apply();
                editor.putInt("zoomMaximo", Integer.parseInt(mainBinding.zoomMaxField.getText().toString())).apply();
                editor.putInt("zoomInicial", Integer.parseInt(mainBinding.zoomInicialField.getText().toString())).apply();
                try {

                    String fatoString1 = mainBinding.fatorCorretivoField1.getText().toString().replace(",", ".").trim();
                    editor.putFloat("fatorCorretivoField1", Float.parseFloat(fatoString1)).apply();

                    String fatoString2 = mainBinding.fatorCorretivoField2.getText().toString().replace(",", ".").trim();
                    editor.putFloat("fatorCorretivoField2", Float.parseFloat(fatoString2)).apply();

                    String fatoString3 = mainBinding.fatorCorretivoField3.getText().toString().replace(",", ".").trim();
                    editor.putFloat("fatorCorretivoField3", Float.parseFloat(fatoString3)).apply();

                    String fatoString4 = mainBinding.fatorCorretivoField4.getText().toString().replace(",", ".").trim();
                    editor.putFloat("fatorCorretivoField4", Float.parseFloat(fatoString4)).apply();

                    String fatoString5 = mainBinding.fatorCorretivoField5.getText().toString().replace(",", ".").trim();
                    editor.putFloat("fatorCorretivoField5", Float.parseFloat(fatoString5)).apply();

                    String fatoString6 = mainBinding.fatorCorretivoField6.getText().toString().replace(",", ".").trim();
                    editor.putFloat("fatorCorretivoField6", Float.parseFloat(fatoString6)).apply();

                    String fatoString7 = mainBinding.fatorCorretivoField7.getText().toString().replace(",", ".").trim();
                    editor.putFloat("fatorCorretivoField7", Float.parseFloat(fatoString7)).apply();

                    String fatoString8 = mainBinding.fatorCorretivoField8.getText().toString().replace(",", ".").trim();
                    editor.putFloat("fatorCorretivoField8", Float.parseFloat(fatoString8)).apply();

                    String fatoString9 = mainBinding.fatorCorretivoField9.getText().toString().replace(",", ".").trim();
                    editor.putFloat("fatorCorretivoField9", Float.parseFloat(fatoString9)).apply();

                    String fatoString10 = mainBinding.fatorCorretivoField10.getText().toString().replace(",", ".").trim();
                    editor.putFloat("fatorCorretivoField10", Float.parseFloat(fatoString10)).apply();

                    String fatoString11 = mainBinding.fatorCorretivoField11.getText().toString().replace(",", ".").trim();
                    editor.putFloat("fatorCorretivoField11", Float.parseFloat(fatoString11)).apply();

                    String fatoString12 = mainBinding.fatorCorretivoField12.getText().toString().replace(",", ".").trim();
                    editor.putFloat("fatorCorretivoField12", Float.parseFloat(fatoString12)).apply();

                    String fatoString13 = mainBinding.fatorCorretivoField13.getText().toString().replace(",", ".").trim();
                    editor.putFloat("fatorCorretivoField13", Float.parseFloat(fatoString13)).apply();

                    String fatoString14 = mainBinding.fatorCorretivoField14.getText().toString().replace(",", ".").trim();
                    editor.putFloat("fatorCorretivoField14", Float.parseFloat(fatoString14)).apply();

                    String fatoString15 = mainBinding.fatorCorretivoField15.getText().toString().replace(",", ".").trim();
                    editor.putFloat("fatorCorretivoField15", Float.parseFloat(fatoString15)).apply();

                    String fatoString16 = mainBinding.fatorCorretivoField16.getText().toString().replace(",", ".").trim();
                    editor.putFloat("fatorCorretivoField16", Float.parseFloat(fatoString16)).apply();

                    String fatoString17 = mainBinding.fatorCorretivoField17.getText().toString().replace(",", ".").trim();
                    editor.putFloat("fatorCorretivoField17", Float.parseFloat(fatoString17)).apply();

                    String fatoString18 = mainBinding.fatorCorretivoField18.getText().toString().replace(",", ".").trim();
                    editor.putFloat("fatorCorretivoField18", Float.parseFloat(fatoString18)).apply();

                    String fatoString19 = mainBinding.fatorCorretivoField19.getText().toString().replace(",", ".").trim();
                    editor.putFloat("fatorCorretivoField19", Float.parseFloat(fatoString19)).apply();

                    String fatoString20 = mainBinding.fatorCorretivoField20.getText().toString().replace(",", ".").trim();
                    editor.putFloat("fatorCorretivoField20", Float.parseFloat(fatoString20)).apply();

                    String fatoString21 = mainBinding.fatorCorretivoField21.getText().toString().replace(",", ".").trim();
                    editor.putFloat("fatorCorretivoField21", Float.parseFloat(fatoString21)).apply();

                    String fatoString22 = mainBinding.fatorCorretivoField22.getText().toString().replace(",", ".").trim();
                    editor.putFloat("fatorCorretivoField22", Float.parseFloat(fatoString22)).apply();

                    String fatoString23 = mainBinding.fatorCorretivoField23.getText().toString().replace(",", ".").trim();
                    editor.putFloat("fatorCorretivoField23", Float.parseFloat(fatoString23)).apply();

                    String fatoString24 = mainBinding.fatorCorretivoField24.getText().toString().replace(",", ".").trim();
                    editor.putFloat("fatorCorretivoField24", Float.parseFloat(fatoString24)).apply();

                    String fatoString25 = mainBinding.fatorCorretivoField25.getText().toString().replace(",", ".").trim();
                    editor.putFloat("fatorCorretivoField25", Float.parseFloat(fatoString25)).apply();

                    String fatoString26 = mainBinding.fatorCorretivoField26.getText().toString().replace(",", ".").trim();
                    editor.putFloat("fatorCorretivoField26", Float.parseFloat(fatoString26)).apply();

                    String fatoString27 = mainBinding.fatorCorretivoField27.getText().toString().replace(",", ".").trim();
                    editor.putFloat("fatorCorretivoField27", Float.parseFloat(fatoString27)).apply();

                    String fatoString28 = mainBinding.fatorCorretivoField28.getText().toString().replace(",", ".").trim();
                    editor.putFloat("fatorCorretivoField28", Float.parseFloat(fatoString28)).apply();


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