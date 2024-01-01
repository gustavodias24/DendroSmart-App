package benicio.soluces.dimensional.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import benicio.soluces.dimensional.R;
import benicio.soluces.dimensional.databinding.ActivityRelatoriosBinding;
import benicio.soluces.dimensional.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding mainBinding;
    private SharedPreferences sharedPreferences;
    private static final int SPLASH_DURATION = 3000;
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Picasso.get().load(R.raw.splashdendro).into(mainBinding.imageView2);
        sharedPreferences = getSharedPreferences("preferencias_usuario", MODE_PRIVATE);

        new Handler().postDelayed(() -> {
            Intent intent;
            if ( sharedPreferences.getBoolean("islogado", false) ){
                Toast.makeText(this, "Bem-vindo de volta.", Toast.LENGTH_SHORT).show();
                intent = new Intent(getApplicationContext(), SelecionarMetodoActivity.class);
            }else{
                intent = new Intent(getApplicationContext(), AutenticacaoActivity.class);
            }
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);

    }
}