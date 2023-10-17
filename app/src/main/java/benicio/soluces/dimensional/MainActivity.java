package benicio.soluces.dimensional;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import benicio.soluces.dimensional.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        // Obtendo a referência do TextView
        TextView textViewDimensions = binding.textView;

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

        // Atualizando o TextView com as dimensões em centímetros
        textViewDimensions.setText("Altura: " + String.format("%.2f", heightCm) + " cm\n"
                + "Largura: " + String.format("%.2f", widthCm) + " cm");
    }
}