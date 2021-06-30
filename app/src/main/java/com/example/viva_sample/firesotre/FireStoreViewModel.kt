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

        db.collection("rooms")
            .add(user)
            .addOnSuccessListener { documentReference ->
                fireStoreMessage.value = documentReference.id
                Logger.d("## DocumentSnapshot added with ID: ${documentReference.id}")
            }
            .addOnFailureListener { e ->
                fireStoreMessage.value = e.message
                Logger.e("Error adding document ==> ${e}")
            }
    }

    fun selectCollection() {
        Logger.d("## selectCollection")



        db.collection("users")
            .document("DEm0m3hv7G1XryhWRMVj") // 문서 조회
//            .whereEqualTo("born", 18151) // where born = 18151
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Document found in the offline cache
                    val document = task.result
                    Logger.d("## Cached document data ==> ${document?.data}")
                } else {
                    Logger.e("## Cached get failed:  ==> ${task.exception}")
                }
            }
//            .addOnSuccessListener { result ->
//                for (document in result) {
//                    Logger.d("## DocumentSnapshot added with ID: ${document.id} => ${document.data}")
//                }
//            }
            .addOnFailureListener { exception ->
                Logger.e("## Error getting documents. ==> ${exception}")
            }
    }

    fun deleteFirestore() {
        Logger.d("## deleteFirestore")
        db.collection("users").document("6MHZzX5dhxFPSs3CqGDf")
            .delete()
            .addOnSuccessListener {
                fireStoreMessage.value = "삭제되었습니다."
                Logger.d("## DocumentSnapshot successfully deleted!")
            }
            .addOnFailureListener { e ->
                Logger.e("## Error deleting document ==> ${e}")
            }
    }
}