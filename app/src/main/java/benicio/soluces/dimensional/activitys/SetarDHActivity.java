package benicio.soluces.dimensional.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import benicio.soluces.dimensional.R;
import benicio.soluces.dimensional.databinding.ActivitySelecionarMetodoBinding;
import benicio.soluces.dimensional.databinding.ActivitySetarDhactivityBinding;

public class SetarDHActivity extends AppCompatActivity implements View.OnClickListener{

    private ActivitySetarDhactivityBinding mainBinding;
    private Boolean isPrimeiraVez = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivitySetarDhactivityBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

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
        mainBinding.btnApagar.setOnClickListener( view -> {
            String textoExistente = mainBinding.edtDh.getText().toString();

            if ( textoExistente.length() == 1 ){
                mainBinding.edtDh.setText("0");
            }else{
                mainBinding.edtDh.setText(
                        removerUltimaLetra(textoExistente)
                );
            }
        });

        mainBinding.btnProsseguir.setOnClickListener( view -> {
            try{
                Float dhFloat = Float.parseFloat(
                        mainBinding.edtDh.getText().toString().replace(",", ".")
                );

                Intent i = new Intent(this, MainActivity.class);
                i.putExtra("dh", dhFloat);

                startActivity(i);
            }catch (Exception e){
                Toast.makeText(this, "Digite um número válido!", Toast.LENGTH_SHORT).show();
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

        if (isPrimeiraVez || mainBinding.edtDh.getText().equals("0")){
            mainBinding.edtDh.setText(novoTexto);
            isPrimeiraVez = false;
        }else{
            String textoExistente = mainBinding.edtDh.getText().toString();
            mainBinding.edtDh.setText(textoExistente + novoTexto);
        }

    }
}