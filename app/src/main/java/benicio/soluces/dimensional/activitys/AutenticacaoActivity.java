package benicio.soluces.dimensional.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;

import java.util.Objects;

import benicio.soluces.dimensional.databinding.ActivityAutenticacaoBinding;

public class AutenticacaoActivity extends AppCompatActivity {

    private ActivityAutenticacaoBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityAutenticacaoBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Autenticação");



    }
}