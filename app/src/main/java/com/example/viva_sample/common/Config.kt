package com.example.viva_sample.common

import android.annotation.SuppressLint
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

object Config {


    @SuppressLint("StaticFieldLeak")
    val firestore = Firebase.firestore


    // https://corikachu.github.io/articles/android/firebase/android-firebase-realtime-chatting-app
    val realtime = Firebase.database
    val myRef = realtime.getReference("message")

}