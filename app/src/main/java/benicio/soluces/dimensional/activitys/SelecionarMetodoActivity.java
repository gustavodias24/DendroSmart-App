package benicio.soluces.dimensional.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.squareup.picasso.Picasso;

import benicio.soluces.dimensional.R;
import benicio.soluces.dimensional.databinding.ActivitySelecionarMetodoBinding;

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


        AlertDialog.Builder b = new AlertDialog.Builder(this);
        b.setTitle("Atenção!");
        b.setMessage("Essa método só está disponível na versão 2 do aplicativo.");
        b.setPositiveButton("OK", null);
        d = b.create();

        Picasso.get().load(R.raw.pinheiro).into(mainBinding.imageView);

        mainBinding.newton.setOnClickListener( view -> escolherMetodo("Newton"));
        mainBinding.smalian.setOnClickListener( view -> escolherMetodo("Smalian"));

        mainBinding.diametro.setOnClickListener( view -> {
            Intent i = new Intent(this, SetarDHActivity.class);
            i.putExtra("diametro", true);
            startActivity(i);
        });

    }

    public void voltar(View view){
        finish();
    }
    public void disponivelApeenasV2(View view){
        d.show();
    }

    public void escolherMetodo(String metodo){
        editor.putString("metodo", metodo).apply();
        startActivity(new Intent(this, BaterFotoArvoreActivity.class));
    }

    public void goToRelatórios(View view){
        startActivity(new Intent(this, RelatoriosActivity.class));
    }
}