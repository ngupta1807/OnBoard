package com.tipbox.app.restclient

import com.tipbox.app.util.MApplication
import com.tipbox.app.util.ServerUrls
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit




class RestClient(var mInstance: MApplication) {
    val  riseService : Poaservice
    companion object {
        private var retrofit:Retrofit ? = null
    }

    init{
       // mInstance = application;
        var interceptor =  HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        var client = OkHttpClient.Builder().addInterceptor(interceptor)
            .addNetworkInterceptor( AddHeaderInterceptor())
            .readTimeout(60, TimeUnit.SECONDS)  // 10 seconds
            .connectTimeout(60, TimeUnit.SECONDS)
            .build();
        retrofit = Retrofit.Builder()
            .baseUrl(ServerUrls.BASE_URL)
            .addConverterFactory(NullOnEmptyConverterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build();

        riseService = retrofit!!.create(Poaservice::class.java)
    }


    inner class AddHeaderInterceptor : Interceptor {

        override fun intercept(chain: Interceptor.Chain?): Response {
            var builder = chain?.request()!!.newBuilder();
            builder.addHeader("authCode", ""+mInstance.getStringFromPreference(ServerUrls.AUTH));
            return chain.proceed(builder.build())
        }
    }


}