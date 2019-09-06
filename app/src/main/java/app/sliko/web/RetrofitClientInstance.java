package app.sliko.web;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {

    private static Retrofit retrofit;
    // private static final String BASE_URL = "http://www.iebasketball.com/sliko_sk/api/";
    private static final String BASE_URL = "http://www.slikosoccer.com/api/";

    public static Retrofit getRetrofitInstance() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        if (retrofit == null) {
            OkHttpClient okClient = new OkHttpClient.Builder()
                    .readTimeout(60 * 2000, TimeUnit.MILLISECONDS).addInterceptor(logging)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create()).client(okClient)
                    .build();
        }
        return retrofit;
    }
}


