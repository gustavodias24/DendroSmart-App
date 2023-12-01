package benicio.soluces.dimensional.activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;

import benicio.soluces.dimensional.databinding.ActivityConfiguracoesBinding;

public class ConfiguracoesActivity extends AppCompatActivity {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private ActivityConfiguracoesBinding mainBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityConfiguracoesBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getSupportActionBar().setTitle("Configurações");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        preferences = getSharedPreferences("configPreferences", Context.MODE_PRIVATE);
        editor = preferences.edit();

        configurarDadosSalvos();

        mainBinding.logo.setOnClickListener( view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1);
        });
        
        mainBinding.dapField.getEditText().setText(
                preferences.getString("dap", "")
        );
        
        mainBinding.toleranciaField.getEditText().setText(
                preferences.getString("tolerancia", "")
        );
        
        
        mainBinding.exibirGps.setOnClickListener( view -> {
            if ( mainBinding.exibirGps.isChecked() ){
                editor.putBoolean("gps", true);
            }else{
                editor.putBoolean("gps", false);
            }

            editor.apply();

        });
        
        mainBinding.btnSalvar.setOnClickListener( view -> {
            editor.putString("dap", mainBinding.dapField.getEditText().getText().toString()).apply();
            editor.putString("tolerancia", mainBinding.toleranciaField.getEditText().getText().toString()).apply();
            Toast.makeText(this, "Configurações salvas!", Toast.LENGTH_SHORT).show();
        });

    }
    private void configurarDadosSalvos(){
        if ( preferences.getString("logoImage", null) != null){
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
        if ( item.getItemId() == android.R.id.home){
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