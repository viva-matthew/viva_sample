package com.taeyoung.gooddoctor.feature.hospital.model

import androidx.room.Entity
import com.google.gson.annotations.SerializedName

/**
 * 병원 리스트 API Response 클래스
 */
data class Hospital(
    @SerializedName("todayOpen")
    val todayOpen: String,
    @SerializedName("emergencyDay")
    val emergencyDay: Boolean,
    @SerializedName("emergencyNight")
    val emergencyNight: Boolean,
    @SerializedName("hosName")
    val hosName: String,
    @SerializedName("tag")
    val tag: List<String>,
    @SerializedName("rateInjectionKey")
    val rateInjectionKey: Int,
    @SerializedName("rateMedicineKey")
    val rateMedicineKey: Int,
    @SerializedName("address")
    val address: String,
    @SerializedName("rateAntibioticKey")
    val rateAntibioticKey: Int,
    @SerializedName("isPro")
    val isPro: Boolean,
    @SerializedName("todayClose")
    val todayClose: String,
    @SerializedName("distance")
    val distance: String,
    @SerializedName("mainCategory")
    val mainCategory: List<String>
)