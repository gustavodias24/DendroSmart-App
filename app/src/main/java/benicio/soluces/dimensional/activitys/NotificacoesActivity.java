package benicio.soluces.dimensional.activitys;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

import benicio.soluces.dimensional.R;
import benicio.soluces.dimensional.adapter.AdapterMsg;
import benicio.soluces.dimensional.databinding.ActivityMenuBinding;
import benicio.soluces.dimensional.databinding.ActivityNotificacoesBinding;
import benicio.soluces.dimensional.model.PostagemModel;

public class NotificacoesActivity extends AppCompatActivity {

    private ActivityNotificacoesBinding mainBinding;
    private Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityNotificacoesBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getSupportActionBar().setTitle("Informes para o usuário");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        b = getIntent().getExtras();
        Gson gson = new Gson();

        Type type = new TypeToken<List<PostagemModel>>() {
        }.getType();

        List<PostagemModel> lista = gson.fromJson(b.getString("lista", ""), type);
        Collections.reverse(lista);

        mainBinding.rv.setLayoutManager(new LinearLayoutManager(this));
        mainBinding.rv.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mainBinding.rv.setHasFixedSize(true);
        mainBinding.rv.setAdapter(new AdapterMsg(lista, this));

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}