package com.taeyoung.gooddoctor.global

import android.annotation.SuppressLint
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

/**
 * API URL 및 설정 관련 싱글톤 객체
 */
object Config {
    const val API_URL: String = "http://devy.tass.duia.us:1218/"

    @SuppressLint("StaticFieldLeak")
    val firestore = Firebase.firestore


    // https://corikachu.github.io/articles/android/firebase/android-firebase-realtime-chatting-app
    val realtime = Firebase.database
    val myRef = realtime.getReference("message")

    /**
     * 네이버 API
     */
    val CLIENT_ID = "HaB79gn8W1bH6hU0wBU7"
    val CLIENT_SECRET = "ViaIC_R59E"
    val BASE_URL_NAVER_API = "https://openapi.naver.com/"

}