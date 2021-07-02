package com.taeyoung.gooddoctor.feature.hospital.model

/**
 * 병원 리스트 API Request 클래스
 */
data class HospitalRequest(
    var category: String,
    var city: String,
    var pro: Boolean,
    var emergency: Boolean,
    var lat: Double?,
    var lon: Double?
)