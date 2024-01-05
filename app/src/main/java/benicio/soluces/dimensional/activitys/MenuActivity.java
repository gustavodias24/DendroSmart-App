package benicio.soluces.dimensional.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.squareup.picasso.Picasso;

import benicio.soluces.dimensional.R;
import benicio.soluces.dimensional.databinding.ActivityMenuBinding;
import benicio.soluces.dimensional.databinding.ActivitySelecionarMetodoBinding;

public class MenuActivity extends AppCompatActivity {

    private ActivityMenuBinding mainBinding;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Picasso.get().load(R.raw.pinheiro).into(mainBinding.imageView);
    }

    public void escolherMetodoCubagem(View view){
        finish();
        startActivity(new Intent(this, SelecionarMetodoActivity.class));
    }
    public void relatorios(View view){
        startActivity(new Intent(this, RelatoriosActivity.class));
    }

    public void irConfiguracoes(View view){
        startActivity(new Intent(this, ConfiguracoesActivity.class));
    }
}