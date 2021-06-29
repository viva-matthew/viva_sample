package com.example.viva_sample.firesotre

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.example.viva_sample.common.Config
import com.orhanobut.logger.Logger


class FireStoreViewModel(application: Application) : AndroidViewModel(application) {
    private val db = Config.firestore
    var fireStoreMessage: MutableLiveData<String> = MutableLiveData()

    fun createCollection() {
        Logger.d("## createCollection")
        val user = hashMapOf(
            "first" to "Ada",
            "last" to "Lovelace",
            "born" to 1815
        )

        db.collection("users")
            .add(user)
            .addOnSuccessListener { documentReference ->
                fireStoreMessage.value = documentReference.id
                Logger.d("## DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                fireStoreMessage.value = e.message
                Logger.e("Error adding document ${e}")
            }
    }
}