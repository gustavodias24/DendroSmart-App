package benicio.soluces.dimensional.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
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


        mainBinding.face.setOnClickListener( v -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/profile.php?id=61559403886899&mibextid=LQQJ4d&rdid=mHTgiHw38cfK62A7&share_url=https%3A%2F%2Fwww.facebook.com%2Fshare%2FZUfre1CMmYJsHrWH%2F%3Fmibextid%3DLQQJ4d"));
            startActivity(i);
        });
        mainBinding.insta.setOnClickListener( v -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/sinapses.solutionss/"));
            startActivity(i);
        });
        mainBinding.linke.setOnClickListener( v -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/company/sinapsesaplicativos/"));
            startActivity(i);
        });
        mainBinding.web.setOnClickListener( v -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.sinapsessolutions.com.br/"));
            startActivity(i);
        });
    }

    public void escolherMetodoCubagem(View view) {
        startActivity(new Intent(this, SelecionarMetodoActivity.class));
    }

    public void relatorios(View view) {
        startActivity(new Intent(this, RelatoriosActivity.class));
    }

    public void irConfiguracoes(View view) {
        startActivity(new Intent(this, ConfiguracoesActivity.class));
    }

}