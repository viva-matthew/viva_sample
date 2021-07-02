package com.example.viva_sample.retrofit.network


import com.taeyoung.gooddoctor.feature.hospital.model.Hospital
import com.taeyoung.gooddoctor.feature.hospital.model.HospitalRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

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

}