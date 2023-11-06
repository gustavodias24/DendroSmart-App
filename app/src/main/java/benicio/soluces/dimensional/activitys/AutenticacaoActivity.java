package benicio.soluces.dimensional.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.rejowan.cutetoast.CuteToast;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import benicio.soluces.dimensional.databinding.ActivityAutenticacaoBinding;
import benicio.soluces.dimensional.databinding.CarregandoLayoutBinding;
import benicio.soluces.dimensional.databinding.CredenciamentoLayoutBinding;
import benicio.soluces.dimensional.model.CredencialModel;
import benicio.soluces.dimensional.model.ResponseModel;
import benicio.soluces.dimensional.model.UsuarioModel;
import benicio.soluces.dimensional.services.ServiceCredenciamento;
import benicio.soluces.dimensional.utils.UsuariosStorageUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AutenticacaoActivity extends AppCompatActivity {

    private  String deviceId;
    private Retrofit retrofit;
    private Dialog dialogCredenciamento, dialogCarregando, dialogConfirmacao, dialogCriarUsuario;
    private ActivityAutenticacaoBinding mainBinding;

    private SharedPreferences sharedPreferences;
    private ServiceCredenciamento service;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = ActivityAutenticacaoBinding.inflate(getLayoutInflater());
        setContentView(mainBinding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Autenticação");

        new Thread(() -> {
            try {
                AdvertisingIdClient.Info adInfo = AdvertisingIdClient.getAdvertisingIdInfo(getApplicationContext());
                deviceId = adInfo.getId();
            } catch (IOException | GooglePlayServicesNotAvailableException |
                     GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            }
        }).start();

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        sharedPreferences = getSharedPreferences("preferencias_usuario", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        criarDialogDeCredenciamento();
        criarDialogNovoUsuario();
        criarDialogCarregando();
        configurarRetrofit();

        if (!sharedPreferences.getBoolean("credenciado", false)){
            dialogCredenciamento.show();
            CuteToast.ct(this, "Você ainda não ativou o produto!", CuteToast.LENGTH_SHORT, CuteToast.INFO, true).show();
        }

        if ( sharedPreferences.getBoolean("islogado", false)){
            Toast.makeText(this, "Bem-vindo de volta.", Toast.LENGTH_SHORT).show();
            irParaOutraTela();
        }

        if (sharedPreferences.getBoolean("guardadados", false)){
            mainBinding.guardarCheckBox.setChecked(true);
            mainBinding.loginField.getEditText().setText(sharedPreferences.getString("login", ""));
            mainBinding.senhaField.getEditText().setText(sharedPreferences.getString("senha", ""));
        }

        mainBinding.btnEntrar.setOnClickListener( entrarView -> {
            String login, senha;
            login = mainBinding.loginField.getEditText().getText().toString().trim();
            senha = mainBinding.senhaField.getEditText().getText().toString().trim();

            if ( !login.isEmpty() && !senha.isEmpty()){
                fazerLogin(new UsuarioModel(login, senha));
            }else{
                CuteToast.ct(this,
                        "Preencha todos os campos!",
                        CuteToast.LENGTH_SHORT,
                        CuteToast.WARN,
                        true).show();
            }

        });

        mainBinding.criarUsuarioText.setOnClickListener( criarView -> {
            dialogCriarUsuario.show();
        });

    }

    public void criarDialogDeCredenciamento(){
        AlertDialog.Builder b = new AlertDialog.Builder(AutenticacaoActivity.this);
        CredenciamentoLayoutBinding credenciamentovb = CredenciamentoLayoutBinding.inflate(getLayoutInflater());

        credenciamentovb.btnEntrar.setOnClickListener( entrarView -> {
            String chave, nome, email, telefone, nomeEmpresa, login, senha;

            chave = credenciamentovb.chaveField.getEditText().getText().toString().trim();
            nome = credenciamentovb.nomeField.getEditText().getText().toString().trim();
            email = credenciamentovb.emailField.getEditText().getText().toString().trim();
            telefone = credenciamentovb.telefoneField.getEditText().getText().toString().trim();
            nomeEmpresa = credenciamentovb.nomeEmpresaField.getEditText().getText().toString().trim();
            login = credenciamentovb.loginField.getEditText().getText().toString().trim();
            senha = credenciamentovb.senhaField.getEditText().getText().toString().trim();

            if (
                    !chave.isEmpty() &&
                            !nome.isEmpty() &&
                            !email.isEmpty() &&
                            !telefone.isEmpty() &&
                            !nomeEmpresa.isEmpty() &&
                            !login.isEmpty() &&
                            !senha.isEmpty()
            ){
                if ( validarEmail(email) ){
                    if ( validarTamanho(login, false) && validarTamanho(senha, false)){
                        if (validarTamanho(telefone, true)){
                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(AutenticacaoActivity.this);
                            alertBuilder.setMessage("Os dados de contato estão corretos?\nOs mesmos serão utilizados para recuperar acesso.");
                            alertBuilder.setNegativeButton("Não", null);
                            alertBuilder.setPositiveButton("Sim", (dialogInterface, i) -> {
                                UsuarioModel usuario = new UsuarioModel(
                                        nome, login, senha, email, nomeEmpresa, telefone
                                );

                                Calendar calendar = Calendar.getInstance();
                                @SuppressLint("DefaultLocale") String data = String.format("%02d/%02d/%s",
                                        calendar.get(Calendar.DAY_OF_MONTH),
                                        (calendar.get(Calendar.MONTH) + 1),
                                        calendar.get(Calendar.YEAR));


                                CredencialModel credencial = new CredencialModel(chave, data , usuario, deviceId);

                                ativarChaveCredencial(credencial);
                                dialogConfirmacao.dismiss();
                            });
                            dialogConfirmacao = alertBuilder.create();
                            dialogConfirmacao.show();
                        }else{
                            CuteToast.ct(this,
                                    "Insira um telefone válido!",
                                    CuteToast.LENGTH_SHORT,
                                    CuteToast.WARN,
                                    true).show();
                        }
                    }else{
                        CuteToast.ct(this,
                                "Login e Senha precisam ter tamanho maior que 4 dígitos!",
                                CuteToast.LENGTH_SHORT,
                                CuteToast.WARN,
                                true).show();
                    }
                }else{
                    CuteToast.ct(this,
                            "Insira um E-mail válido!",
                            CuteToast.LENGTH_SHORT,
                            CuteToast.WARN,
                            true).show();
                }
            }else{
                CuteToast.ct(this,
                        "Preencha todos os campos!",
                        CuteToast.LENGTH_SHORT,
                        CuteToast.WARN,
                        true).show();
            }

        });

        b.setView(credenciamentovb.getRoot());
        b.setCancelable(false);
        dialogCredenciamento = b.create();
    }
    public void criarDialogNovoUsuario(){
        AlertDialog.Builder b = new AlertDialog.Builder(AutenticacaoActivity.this);
        CredenciamentoLayoutBinding novoUsuarioBinding = CredenciamentoLayoutBinding.inflate(getLayoutInflater());
        novoUsuarioBinding.titleText.setText("Adicionar novo usuário.");
        novoUsuarioBinding.telefoneField.setVisibility(View.GONE);
        novoUsuarioBinding.chaveField.setVisibility(View.GONE);
        novoUsuarioBinding.nomeField.setVisibility(View.GONE);
        novoUsuarioBinding.nomeEmpresaField.setVisibility(View.GONE);
        novoUsuarioBinding.emailField.setVisibility(View.GONE);
        novoUsuarioBinding.btnEntrar.setText("Criar");
        novoUsuarioBinding.btnEntrar.setOnClickListener( criarUser -> {
            String login, senha;

            login = novoUsuarioBinding.loginField.getEditText().getText().toString();
            senha = novoUsuarioBinding.senhaField.getEditText().getText().toString();
            if (
                    !login.isEmpty() && !senha.isEmpty()
            ){
                dialogCriarUsuario.dismiss();

                mainBinding.loginField.getEditText().setText(login);
                mainBinding.senhaField.getEditText().setText(senha);

                editor.putString("login", login);
                editor.putString("senha", senha);
                editor.putBoolean("credenciado", true);

                editor.apply();

                persistirUsuario(new UsuarioModel(login, senha));

                CuteToast.ct(this,
                        "Usuário criado com sucesso!",
                        CuteToast.LENGTH_SHORT,
                        CuteToast.SUCCESS,
                        true).show();
            }else{
                CuteToast.ct(this,
                        "Preencha todos os campos!",
                        CuteToast.LENGTH_SHORT,
                        CuteToast.WARN,
                        true).show();
            }
        });

        b.setView(novoUsuarioBinding.getRoot());
        dialogCriarUsuario = b.create();
    }

    public void criarDialogCarregando(){
        AlertDialog.Builder b = new AlertDialog.Builder(AutenticacaoActivity.this);
        b.setView(CarregandoLayoutBinding.inflate(getLayoutInflater()).getRoot());
        b.setCancelable(false);
        dialogCarregando = b.create();
    }

    public void configurarRetrofit(){
        retrofit = new Retrofit.Builder()
                .baseUrl("https://autencacao.vercel.app/")
                .addConverterFactory(GsonConverterFactory.create()).build();

        service = retrofit.create(ServiceCredenciamento.class);
    }

    public void ativarChaveCredencial(CredencialModel credencialModel){

        dialogCarregando.show();

        service.ativacaoChave(credencialModel).enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                if ( response.isSuccessful() ){
                    ResponseModel responseModel = response.body();

                    if ( responseModel.issuccess() ){
                        CuteToast.ct(getApplicationContext(),
                                responseModel.getMsg(),
                                CuteToast.LENGTH_SHORT, CuteToast.SUCCESS, true).show();

                        dialogCredenciamento.dismiss();

                        mainBinding.loginField.getEditText().setText(credencialModel.getUsuario().getLogin());
                        mainBinding.senhaField.getEditText().setText(credencialModel.getUsuario().getSenha());

                        editor.putString("login", credencialModel.getUsuario().getLogin());
                        editor.putString("senha", credencialModel.getUsuario().getSenha());
                        editor.putBoolean("credenciado", true);

                        editor.apply();

                        persistirUsuario(credencialModel.getUsuario());


                    }else{
                        CuteToast.ct(getApplicationContext(),
                                responseModel.getMsg(),
                                CuteToast.LENGTH_SHORT,
                                CuteToast.ERROR,
                                true).show();
                    }

                    dialogCarregando.dismiss();
                }else{
                    CuteToast.ct(getApplicationContext(),
                            response.message(),
                            CuteToast.LENGTH_SHORT,
                            CuteToast.ERROR,
                            true).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                CuteToast.ct(getApplicationContext(),
                        t.getMessage(),
                        CuteToast.LENGTH_SHORT,
                        CuteToast.ERROR,
                        true).show();
                dialogCarregando.dismiss();
            }
        });
    }

    public void persistirUsuario(UsuarioModel usuario){
        List<UsuarioModel> listaUsuariosExistentes = UsuariosStorageUtil.loadUsuarios(getApplicationContext());

        if ( listaUsuariosExistentes != null) {
            listaUsuariosExistentes.add(usuario);
            UsuariosStorageUtil.saveUsuarios(getApplicationContext(), listaUsuariosExistentes);
        }
        else{
            listaUsuariosExistentes = new ArrayList<>();
            listaUsuariosExistentes.add(usuario);
            UsuariosStorageUtil.saveUsuarios(getApplicationContext(), listaUsuariosExistentes);
        }

    }

    public void fazerLogin(UsuarioModel usuario){

        List<UsuarioModel> listaUsuariosExistentes = UsuariosStorageUtil.loadUsuarios(getApplicationContext());

        if ( listaUsuariosExistentes != null){
            UsuarioModel usuarioCorrespondente = null;

            for (UsuarioModel usuarioModel : listaUsuariosExistentes){
                if (usuario.getLogin().equals(usuarioModel.getLogin())){
                    usuarioCorrespondente = usuarioModel;
                    break;
                }
            }

            if (usuarioCorrespondente != null){
                if (usuarioCorrespondente.getSenha().equals(usuario.getSenha())){
                    CuteToast.ct(getApplicationContext(),
                            "Login feito com sucesso!",
                            CuteToast.LENGTH_SHORT, CuteToast.SUCCESS, true).show();
                    editor.putBoolean("guardadados", true);
                    editor.putString("login", usuario.getLogin());
                    editor.putString("senha", usuario.getSenha());
                    editor.putBoolean("islogado", true);
                    editor.apply();
                    irParaOutraTela();
                }else{
                    CuteToast.ct(getApplicationContext(),
                            "Senha errada!",
                            CuteToast.LENGTH_SHORT,
                            CuteToast.ERROR,
                            true).show();
                }
            }else{
                CuteToast.ct(getApplicationContext(),
                        "Usuário não encontrado!",
                        CuteToast.LENGTH_SHORT,
                        CuteToast.ERROR,
                        true).show();
            }
        }
        /*
        dialogCarregando.show();
        service.fazerLogin(usuario).enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                dialogCarregando.dismiss();

                ResponseModel responseModel = response.body();

                if ( responseModel.issuccess() ){
                    CuteToast.ct(getApplicationContext(),
                            responseModel.getMsg(),
                            CuteToast.LENGTH_SHORT, CuteToast.SUCCESS, true).show();
                    editor.putBoolean("islogado", true);
                    editor.apply();
                    irParaOutraTela();
                }else {
                    CuteToast.ct(getApplicationContext(),
                            responseModel.getMsg(),
                            CuteToast.LENGTH_SHORT,
                            CuteToast.ERROR,
                            true).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                dialogCarregando.dismiss();
                CuteToast.ct(getApplicationContext(),
                        t.getMessage(),
                        CuteToast.LENGTH_SHORT,
                        CuteToast.ERROR,
                        true).show();
            }
        });*/
    }

    public void irParaOutraTela(){
        finish();
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }


    public Boolean validarEmail(String email){

        boolean isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email).matches();

        if (isEmailValid) {
            return true;
        } else {
            return false;
        }
    }

    public Boolean validarTamanho(String campo, Boolean telefone){
        int tamanho = telefone ? 6 : 4;
        if ( campo.length() >= tamanho){
            return true;
        }else{
            return false;
        }
    }
}