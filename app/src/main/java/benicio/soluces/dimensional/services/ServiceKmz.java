package benicio.soluces.dimensional.services;

import benicio.soluces.dimensional.model.UploadResponse;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ServiceKmz {

    @Multipart
    @POST("/upload_kmz")
    Call<UploadResponse> uploadKmz(
            @Part("id_do_usuario") RequestBody idDoUsuario,
            @Part MultipartBody.Part file
    );

}
