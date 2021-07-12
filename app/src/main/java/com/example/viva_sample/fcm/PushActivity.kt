package com.example.viva_sample.fcm

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.viva_sample.R
import com.example.viva_sample.databinding.ActivityPushBinding
import com.google.firebase.messaging.FirebaseMessaging
import com.orhanobut.logger.Logger

class PushActivity : AppCompatActivity() {
    private val binding: ActivityPushBinding by lazy { DataBindingUtil.setContentView(this, R.layout.activity_push) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_push)

        binding.lifecycleOwner = this

        initFirebase()
        updateResult()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        setIntent(intent)
        updateResult(true)
    }

    private fun initFirebase() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.d("## token ==> ${task.result}")
                    binding.tvTokenValue.text = task.result
                }
            }
    }

    @SuppressLint("SetTextI18n")
    private fun updateResult(isNewIntent: Boolean = false) {
        binding.tvResultValue.text = (intent.getStringExtra("notificationType") ?: "앱 런처") +
        if (isNewIntent) {
            "(으)로 갱신했습니다."
        } else {
            "(으)로 실행했습니다."
        }
    }
}