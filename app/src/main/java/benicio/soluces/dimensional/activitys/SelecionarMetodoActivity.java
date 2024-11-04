package benicio.soluces.dimensional.activitys;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import benicio.soluces.dimensional.R;
import benicio.soluces.dimensional.databinding.ActivitySelecionarMetodoBinding;
import benicio.soluces.dimensional.databinding.NomeProjetoLayoutBinding;

public class SelecionarMetodoActivity extends AppCompatActivity {

    ActivitySelecionarMetodoBinding mainBinding;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;
    Dialog d;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivitySelecionarMetodoBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        prefs = getSharedPreferences("configPreferences", Context.MODE_PRIVATE);
        editor = prefs.edit();

        // Define que o teclado será sempre fixo e completo
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);



        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Atenção!");
        b.setMessage("Essa método só está disponível na versão 2 do aplicativo.");
        b.setPositiveButton("OK", null);
        d = b.create();

        Picasso.get().load(R.raw.pinheiro).into(mainBinding.imageView);

        mainBinding.newton.setOnClickListener(view -> escolherMetodo("Newton"));
        mainBinding.smalian.setOnClickListener(view -> escolherMetodo("Smalian"));

        mainBinding.diametro.setOnClickListener(view -> {
            Intent i = new Intent(this, SetarDHActivity.class);
            i.putExtra("diametro", true);
            startActivity(i);
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissões concedidas", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permissões negadas", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void voltar(View view) {
        finish();
    }

    public void disponivelApeenasV2(View view) {
        d.show();
    }

    public void escolherMetodo(String metodo) {

        if (
                ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        ) {
            editor.putString("metodo", metodo).apply();
            AlertDialog.Builder confimarNomeDialog = new AlertDialog.Builder(SelecionarMetodoActivity.this);
            NomeProjetoLayoutBinding projetoLayoutBinding = NomeProjetoLayoutBinding.inflate(getLayoutInflater());

            projetoLayoutBinding.nomeProjeto.setOnFocusChangeListener((v, hasFocus) -> {
                if (hasFocus) {
                    // Força a exibição do teclado completo ao focar no EditText
                    projetoLayoutBinding.nomeProjeto.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
                    projetoLayoutBinding.nomeProjeto.requestFocus();
                }
            });

            projetoLayoutBinding.nomeProjeto.setText(
                    prefs.getString("nomeProjeto", "")
            );
            projetoLayoutBinding.confirmar.setOnClickListener(v -> {
                editor.putString("nomeProjeto", projetoLayoutBinding.nomeProjeto.getText().toString()).apply();
                startActivity(new Intent(this, BaterFotoArvoreActivity.class));

            });

            confimarNomeDialog.setView(projetoLayoutBinding.getRoot());
            confimarNomeDialog.create().show();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA}, 1);
        }

    }

    public void goToRelatórios(View view) {
        startActivity(new Intent(this, RelatoriosActivity.class));
    }
}