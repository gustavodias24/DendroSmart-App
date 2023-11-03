package benicio.soluces.dimensional.services;

import benicio.soluces.dimensional.model.CredencialModel;
import benicio.soluces.dimensional.model.ResponseModel;
import benicio.soluces.dimensional.model.UsuarioModel;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ServiceCredenciamento {

        @POST("ativacao")
        Call<ResponseModel> ativacaoChave(@Body CredencialModel credencial);

        @POST("login")
        Call<ResponseModel> fazerLogin(@Body UsuarioModel usuario);
}