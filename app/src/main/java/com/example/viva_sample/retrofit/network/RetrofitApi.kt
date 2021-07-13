package com.example.viva_sample.retrofit.network


import com.example.viva_sample.papgo.ResultTransferPapago
import com.taeyoung.gooddoctor.feature.hospital.model.Hospital
import com.taeyoung.gooddoctor.feature.hospital.model.HospitalRequest
import retrofit2.Call
import retrofit2.http.*

/**
 * API 인터페이스
 */
interface RetrofitApi {
    @POST("hoslist")
    fun getHoslist(@Body hospitalRequest: HospitalRequest): Call<List<Hospital>>

    @GET("list")
    fun getList(): Call<List<String>>

    @GET("category")
    fun getCategory(): Call<List<String>>


    // PAPAGO
    @FormUrlEncoded
    @POST("v1/papago/n2mt")
    fun transferPapago(
        @Header("X-Naver-Client-Id") clientId: String,
        @Header("X-Naver-Client-Secret") clientSecret: String,
        @Field("source") source: String,
        @Field("target") target: String,
        @Field("text") text: String
    ): Call<ResultTransferPapago>

}