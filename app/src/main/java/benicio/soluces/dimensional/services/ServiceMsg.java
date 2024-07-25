package benicio.soluces.dimensional.services;

import java.io.Serializable;
import java.util.List;

import benicio.soluces.dimensional.model.PostagemModel;
import benicio.soluces.dimensional.model.ResponseMsg;
import retrofit2.Call;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ServiceMsg  {

    @POST("get_postagens")
    Call<List<PostagemModel>> getPostagens();

    @POST("/{id}/get_imagem")
    Call<ResponseMsg> getImagePostagem(@Path("id") String _id);
}
