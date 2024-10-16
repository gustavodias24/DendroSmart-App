package benicio.soluces.dimensional.utils;

import benicio.soluces.dimensional.services.ServiceKmz;
import benicio.soluces.dimensional.services.ServiceMsg;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitKmz {

    public static Retrofit createRetrofitKmz() {
        return new Retrofit.Builder().baseUrl("http://191.252.110.178:5000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static ServiceKmz createServiceKmz(Retrofit retrofit) {
        return retrofit.create(ServiceKmz.class);
    }
}
