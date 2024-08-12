package benicio.soluces.dimensional.activitys;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Objects;

import benicio.soluces.dimensional.R;
import benicio.soluces.dimensional.databinding.ActivityBaterFotoArvoreBinding;
import benicio.soluces.dimensional.databinding.ActivitySetarComprimentoToraBinding;
import benicio.soluces.dimensional.databinding.ActivitySetarDhactivityBinding;
import benicio.soluces.dimensional.model.ItemRelatorio;

public class SetarComprimentoToraActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivitySetarComprimentoToraBinding mainBinding;
    private Boolean isPrimeiraVez = true;

    @SuppressLint({"ResourceType"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivitySetarComprimentoToraBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        mainBinding.backButton4.setOnClickListener(view -> finish());
        Picasso.get().load(R.raw.pinheirocortado).into(mainBinding.imageView4);

        mainBinding.btnProsseguir.setOnClickListener(view -> {
            try {
                Float tamCadaParte = Float.parseFloat(
                        mainBinding.edtDh.getText().toString().replace(",", ".")
                );

                Intent i = new Intent(this, SetarDHActivity.class);
                i.putExtra("tamCadaParte", tamCadaParte);
                i.putExtra("link", getIntent().getExtras().getString("link", ""));

                startActivity(i);
            } catch (Exception e) {
                AlertDialog.Builder b = new AlertDialog.Builder(this);
                b.setTitle("Atenção!");
                b.setMessage("Você digitou " + mainBinding.edtDh.getText().toString() + " e é um número inválido...\nDevido:\n" + e.getMessage());
                b.setPositiveButton("ok", null);
                b.show();

                Toast.makeText(this, "Digite um número válido!", Toast.LENGTH_SHORT).show();
            }

        });


        mainBinding.btn0.setOnClickListener(this);
        mainBinding.btn1.setOnClickListener(this);
        mainBinding.btn2.setOnClickListener(this);
        mainBinding.btn3.setOnClickListener(this);
        mainBinding.btn4.setOnClickListener(this);
        mainBinding.btn5.setOnClickListener(this);
        mainBinding.btn6.setOnClickListener(this);
        mainBinding.btn7.setOnClickListener(this);
        mainBinding.btn8.setOnClickListener(this);
        mainBinding.btn9.setOnClickListener(this);
        mainBinding.btnVirgula.setOnClickListener(this);
        mainBinding.btnApagar.setOnClickListener(view -> {
            String textoExistente = mainBinding.edtDh.getText().toString();

            if (textoExistente.length() == 1) {
                mainBinding.edtDh.setText("0");
            } else {
                mainBinding.edtDh.setText(
                        removerUltimaLetra(textoExistente)
                );
            }
        });
    }

    public static String removerUltimaLetra(String str) {
        if (str != null && str.length() > 1) {
            // Use o método substring para obter uma parte da string, excluindo o último caractere
            return str.substring(0, str.length() - 1);
        } else {
            // Se a string for nula ou vazia, retorne a própria string
            return "0";
        }
    }

    @Override
    public void onClick(View view) {
        Button button = (Button) view;
        String novoTexto = button.getText().toString();

        if (isPrimeiraVez || mainBinding.edtDh.getText().equals("0")) {
            mainBinding.edtDh.setText(novoTexto);
            isPrimeiraVez = false;
        } else {
            String textoExistente = mainBinding.edtDh.getText().toString();
            mainBinding.edtDh.setText(textoExistente + novoTexto);
        }
    }
}