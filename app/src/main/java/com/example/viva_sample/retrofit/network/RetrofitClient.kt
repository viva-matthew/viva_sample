package com.example.viva_sample.retrofit.network

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.taeyoung.gooddoctor.global.Config
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * Retrofit 설정 및 객체 선언 클래스
 */
object RetrofitClient {
    val apiService: RetrofitApi

    init {
        val builder = OkHttpClient.Builder()
        builder.addInterceptor { chain: Interceptor.Chain ->
            val request = chain.request()
            val newRequest: Request = request.newBuilder()
                    .build()
            chain.proceed(newRequest)
        }



        val client = builder
            .addNetworkInterceptor(StethoInterceptor()) // Stetho Interceptor 추가해야 Chrome Inspect tool 에서 확인 가능, 필수 아님
            .build()


        val retrofit = Retrofit.Builder()
                .baseUrl(Config.API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()

        apiService = retrofit.create(RetrofitApi::class.java)
    }

}