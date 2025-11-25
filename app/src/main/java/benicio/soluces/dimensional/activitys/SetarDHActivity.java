package benicio.soluces.dimensional.activitys;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import java.util.Objects;

import benicio.soluces.dimensional.R;
import benicio.soluces.dimensional.databinding.ActivitySelecionarMetodoBinding;
import benicio.soluces.dimensional.databinding.ActivitySetarDhactivityBinding;
import benicio.soluces.dimensional.model.ItemRelatorio;

public class SetarDHActivity extends AppCompatActivity implements View.OnClickListener {

    private ActivitySetarDhactivityBinding mainBinding;
    private Boolean isPrimeiraVez = true;
    private Bundle b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivitySetarDhactivityBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        b = getIntent().getExtras();

        if (b != null && b.getBoolean("diametro", false)) {
            mainBinding.textView3.setText("MEÇA COM O LASER E INFORME A DISTÂNCIA DIRETA");
            mainBinding.imageView3.setImageResource(R.drawable.observacaodireta);
        }

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

        mainBinding.btnProsseguir.setOnClickListener(view -> {
            String input = mainBinding.edtDh.getText().toString().trim();

            try {
                // TENTA LER COMO INTEIRO
                int dhInt = Integer.parseInt(input);  // se tiver vírgula/ponto/decimal, vai dar erro aqui

                // Validação do intervalo 3 a 30
                if (dhInt < 3 || dhInt > 30) {
                    new AlertDialog.Builder(this)
                            .setTitle("Atenção")
                            .setMessage("A distância deve ser um número inteiro entre 3 e 30 metros.")
                            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                            .show();
                    return; // impede de continuar para a próxima tela
                }

                // Se chegou aqui, está tudo ok: inteiro e dentro do intervalo
                Intent i = new Intent(this, MainActivity.class);
                // se você ainda precisa mandar como float:
                i.putExtra("dh", (float) dhInt);
                i.putExtra("tamCadaParte", getIntent().getExtras().getFloat("tamCadaParte", 0.0f));
                i.putExtra("link", getIntent().getExtras().getString("link", ""));

                if (b != null && b.getBoolean("diametro", false)) {
                    mainBinding.textView3.setText("MEÇA COM O LASER E INFORME A DISTÂNCIA DIRETA");
                    i.putExtra("diametro", true);
                }

                startActivity(i);

            } catch (NumberFormatException e) {
                // aqui cai se o campo estiver vazio, com letras, vírgula, ponto, decimal etc.
                new AlertDialog.Builder(this)
                        .setTitle("Atenção")
                        .setMessage("A distância deve ser um número inteiro entre 3 e 50 metros.")
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        });



        mainBinding.backButton5.setOnClickListener(view -> finish());

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

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {

        Button button = (Button) view;
        String novoTexto = button.getText().toString();

        if (isPrimeiraVez || mainBinding.edtDh.getText().toString().equals("0")) {
            // Verifica se o novoTexto começa com uma vírgula ou ponto e adiciona o "0" na frente
            if (novoTexto.startsWith(",") || novoTexto.startsWith(".")) {
                novoTexto = "0" + novoTexto;
            }

            mainBinding.edtDh.setText(novoTexto);
            isPrimeiraVez = false;
        } else {
            String textoExistente = mainBinding.edtDh.getText().toString();
            mainBinding.edtDh.setText(textoExistente + novoTexto);
        }

    }
}