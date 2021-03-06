package com.example.viva_sample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.viva_sample.ble.DeviceScanActivity
import com.example.viva_sample.databinding.ActivityMainBinding
import com.example.viva_sample.fcm.PushActivity
import com.example.viva_sample.firestore.FireStoreActivity
import com.example.viva_sample.lottie.LottieActivity
import com.example.viva_sample.mqtt.MqttActivity
import com.example.viva_sample.naver.NaverMapActivity
import com.example.viva_sample.realtimedb.RealtimeDbActivity
import com.example.viva_sample.room.RoomActivity
import com.example.viva_sample.stt.SttActivity
import com.example.viva_sample.tts.TtsActivity


class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { DataBindingUtil.setContentView(this, R.layout.activity_main) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding.lifecycleOwner = this

        binding.btnBLE.setOnClickListener {
            val intent = Intent(this, DeviceScanActivity::class.java)
            startActivity(intent)
        }

        binding.btnSTT.setOnClickListener {
            val intent = Intent(this, SttActivity::class.java)
            startActivity(intent)
        }

        binding.btnTTS.setOnClickListener {
            val intent = Intent(this, TtsActivity::class.java)
            startActivity(intent)
        }

        binding.btnFireStore.setOnClickListener {
            val intent = Intent(this, FireStoreActivity::class.java)
            startActivity(intent)
        }

        binding.btnRealtimeDB.setOnClickListener {
            val intent = Intent(this, RealtimeDbActivity::class.java)
            startActivity(intent)
        }

        binding.btnNaver.setOnClickListener {
            val intent = Intent(this, NaverMapActivity::class.java)
            startActivity(intent)
        }

        binding.btnRoom.setOnClickListener {
            val intent = Intent(this, RoomActivity::class.java)
            startActivity(intent)
        }

        binding.btnMqtt.setOnClickListener {
            val intent = Intent(this, MqttActivity::class.java)
            startActivity(intent)
        }

        binding.btnLottie.setOnClickListener {
            val intent = Intent(this, LottieActivity::class.java)
            startActivity(intent)
        }

        binding.btnFCM.setOnClickListener {
            val intent = Intent(this, PushActivity::class.java)
            startActivity(intent)
        }


    }
}