package benicio.soluces.dimensional.utils;

import benicio.soluces.dimensional.services.ServiceMsg;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitUtil {

    public static Retrofit createRetrofitMsg() {
        return new Retrofit.Builder().baseUrl("https://comunicao-clientes-kaizen.vercel.app/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static ServiceMsg createServiceMsg(Retrofit retrofit) {
        return retrofit.create(ServiceMsg.class);
    }

}
