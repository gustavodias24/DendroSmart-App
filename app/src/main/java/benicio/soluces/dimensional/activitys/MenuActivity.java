package benicio.soluces.dimensional.activitys;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
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
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/people/Sinapses/61559403886899/"));
            startActivity(i);
        });

        mainBinding.btnContato.setOnClickListener(v -> {
            Intent i = new Intent(this, InfosContatoActivity.class);
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


        mainBinding.btnKey.setOnClickListener(v -> {
            AlertDialog.Builder b = new AlertDialog.Builder(this);
            b.setTitle("Escolha uma Opção");
            b.setMessage("Você pode copiar a Chave de acesso ou ir para o  Website Sinapses");
            b.setPositiveButton("Copiar Chave", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Toast.makeText(MenuActivity.this, "Chave de Acesso Copiado", Toast.LENGTH_SHORT).show();
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clip = ClipData.newPlainText("token", getSharedPreferences("preferencias_usuario", MODE_PRIVATE).getString("token", ""));
                    clipboardManager.setPrimaryClip(clip);
                }
            });
            b.setNegativeButton("Ir para o Site", (d, i) -> {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://191.252.110.178:5000/")));
            });
            b.create().show();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Área restrita");

        final EditText input = new EditText(this);
        input.setHint("Digite a senha");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        builder.setView(input);

        builder.setPositiveButton("Entrar", (dialog, which) -> {
            String senha = input.getText().toString().trim();

            if ("D3ndr02026".equals(senha)) {
                // senha correta -> abre configurações
                Intent i = new Intent(this, ConfiguracoesActivity.class);
                startActivity(i);
            } else {
                // senha errada
                Toast.makeText(this, "Senha incorreta", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.show();
    }

}