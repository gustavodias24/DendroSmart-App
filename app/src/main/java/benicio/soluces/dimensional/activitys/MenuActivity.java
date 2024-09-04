package benicio.soluces.dimensional.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import benicio.soluces.dimensional.R;
import benicio.soluces.dimensional.databinding.ActivityMenuBinding;
import benicio.soluces.dimensional.databinding.ActivitySelecionarMetodoBinding;
import benicio.soluces.dimensional.model.PostagemModel;
import benicio.soluces.dimensional.utils.RetrofitUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MenuActivity extends AppCompatActivity {

    private ActivityMenuBinding mainBinding;
    private List<PostagemModel> listaPostagem = null;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        Picasso.get().load(R.raw.pinheiro).into(mainBinding.imageView);

        sharedPreferences = getSharedPreferences("user", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        mainBinding.face.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/profile.php?id=61559403886899&mibextid=LQQJ4d&rdid=mHTgiHw38cfK62A7&share_url=https%3A%2F%2Fwww.facebook.com%2Fshare%2FZUfre1CMmYJsHrWH%2F%3Fmibextid%3DLQQJ4d"));
            startActivity(i);
        });
        mainBinding.insta.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/sinapses.solutionss/"));
            startActivity(i);
        });
        mainBinding.linke.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/company/sinapsesaplicativos/"));
            startActivity(i);
        });
        mainBinding.web.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.sinapsessolutions.com.br/"));
            startActivity(i);
        });

        mainBinding.button2.setOnClickListener(v -> startActivity(new Intent(this, InformacoesActivity.class)));


        RetrofitUtil.createServiceMsg(
                RetrofitUtil.createRetrofitMsg()
        ).getPostagens().enqueue(new Callback<List<PostagemModel>>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<List<PostagemModel>> call, Response<List<PostagemModel>> response) {
                if (response.isSuccessful()) {
                    listaPostagem = new ArrayList<>();

                    assert response.body() != null;
                    for (PostagemModel postagemModel : response.body()) {
                        if (postagemModel.getApp().equals("Dendro Smart")) {
                            listaPostagem.add(postagemModel);
                        }
                    }

                    int quantidadeVista = sharedPreferences.getInt("notifications", 0);
                    int calculo = (listaPostagem.size() - quantidadeVista);
                    if (calculo < 0) {
                        calculo = 0;
                        editor.putInt("notifications", 0).apply();

                    }
                    mainBinding.textView7.setText("" + calculo);
                }
            }

            @Override
            public void onFailure(Call<List<PostagemModel>> call, Throwable t) {

            }
        });
    }

    public void gotonotifications(View view) {
        if (listaPostagem != null) {
            Intent i = new Intent(this, NotificacoesActivity.class);
            i.putExtra("lista", new Gson().toJson(listaPostagem));
            editor.putInt("notifications", listaPostagem.size()).apply();
            startActivity(i);
        } else {
            Toast.makeText(this, "Tente Novamente...", Toast.LENGTH_SHORT).show();
        }

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