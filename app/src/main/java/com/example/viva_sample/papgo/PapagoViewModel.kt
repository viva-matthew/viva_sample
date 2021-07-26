package com.example.viva_sample.papgo

import com.example.viva_sample.retrofit.network.RetrofitClient
import com.orhanobut.logger.Logger
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PapagoViewModel {

    fun translate() {
        val call: Call<List<String>>? = RetrofitClient.apiService.getList()


        call?.enqueue(object : Callback<List<String>?> {
            override fun onResponse(call: Call<List<String>?>?, response: Response<List<String>?>?) {
                if (response?.code() == 200) {
                    val cityList: List<String>? = response!!.body()
                    listMutableLiveData.value = cityList
                } else {
                    return
                }
            }

            override fun onFailure(call: Call<List<String>?>?, t: Throwable?) {
                Logger.e("## onFailure ==> ${t?.message}")
            }
        })
    }
}