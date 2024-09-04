package benicio.soluces.dimensional.activitys;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import benicio.soluces.dimensional.R;
import benicio.soluces.dimensional.databinding.ActivityInformacoesBinding;

public class InformacoesActivity extends AppCompatActivity {

    private ActivityInformacoesBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityInformacoesBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getSupportActionBar().setTitle("Info Device");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        StringBuilder deviceInfo = new StringBuilder();

        // Informações gerais do dispositivo
        deviceInfo.append("Versão do app: ").append(getString(R.string.verison_app)).append("\n");
        deviceInfo.append("MANUFACTURER: ").append(Build.MANUFACTURER).append("\n");
        deviceInfo.append("MODEL: ").append(Build.MODEL).append("\n");
        deviceInfo.append("BRAND: ").append(Build.BRAND).append("\n");
        deviceInfo.append("DEVICE: ").append(Build.DEVICE).append("\n");
        deviceInfo.append("PRODUCT: ").append(Build.PRODUCT).append("\n");
        deviceInfo.append("BOARD: ").append(Build.BOARD).append("\n");
        deviceInfo.append("HARDWARE: ").append(Build.HARDWARE).append("\n");
        deviceInfo.append("DISPLAY: ").append(Build.DISPLAY).append("\n");
        deviceInfo.append("ID: ").append(Build.ID).append("\n");
        deviceInfo.append("SERIAL: ").append(Build.SERIAL).append("\n");
        deviceInfo.append("BOOTLOADER: ").append(Build.BOOTLOADER).append("\n");

        // Informações do sistema operacional
        deviceInfo.append("VERSION.RELEASE: ").append(Build.VERSION.RELEASE).append("\n");
        deviceInfo.append("VERSION.SDK_INT: ").append(Build.VERSION.SDK_INT).append("\n");
        deviceInfo.append("VERSION.INCREMENTAL: ").append(Build.VERSION.INCREMENTAL).append("\n");
        deviceInfo.append("VERSION.CODENAME: ").append(Build.VERSION.CODENAME).append("\n");

        // Informações do dispositivo (Secure Settings)
        @SuppressLint("HardwareIds") String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        deviceInfo.append("ANDROID_ID: ").append(androidId).append("\n");

        // Exibir as informações no TextView
        mainBinding.deviceInfoTextView.setText(deviceInfo.toString());


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}