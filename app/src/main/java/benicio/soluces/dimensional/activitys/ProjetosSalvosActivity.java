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

import java.util.Collections;
import java.util.List;

import benicio.soluces.dimensional.R;
import benicio.soluces.dimensional.adapter.AdapterProjetos;
import benicio.soluces.dimensional.databinding.ActivityProjetosSalvosBinding;
import benicio.soluces.dimensional.databinding.ActivityRelatoriosBinding;
import benicio.soluces.dimensional.model.ProjetoModel;
import benicio.soluces.dimensional.utils.ProjetosUtils;

public class ProjetosSalvosActivity extends AppCompatActivity {

    private ActivityProjetosSalvosBinding mainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityProjetosSalvosBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getSupportActionBar().setTitle("Projetos");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mainBinding.rvProjetos.setHasFixedSize(true);
        mainBinding.rvProjetos.setLayoutManager(new LinearLayoutManager(this));
        mainBinding.rvProjetos.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        List<ProjetoModel> projetos = ProjetosUtils.returnList(
                this
        );
        Collections.reverse(projetos);
        mainBinding.rvProjetos.setAdapter(new AdapterProjetos(projetos, this));

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}