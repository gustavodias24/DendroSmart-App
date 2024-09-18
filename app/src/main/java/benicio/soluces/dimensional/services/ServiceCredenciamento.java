package benicio.soluces.dimensional.services;

import benicio.soluces.dimensional.model.CredencialModel;
import benicio.soluces.dimensional.model.ResponseModel;
import benicio.soluces.dimensional.model.UsuarioModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ServiceCredenciamento {


        
        @POST("ativacao")
        Call<ResponseModel> ativacaoChave(@Body CredencialModel credencial);

        @POST("login")
        Call<ResponseModel> fazerLogin(@Body UsuarioModel usuario);

        @POST("{key}/key")
        Call<ResponseModel> pegar_dados_key(@Path("key") String key);
}