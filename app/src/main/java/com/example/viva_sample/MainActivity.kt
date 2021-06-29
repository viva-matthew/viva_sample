package com.example.viva_sample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.viva_sample.BLE.ble.DeviceScanActivity
import com.example.viva_sample.firesotre.FireStoreActivity
import com.example.viva_sample.stt.SttActivity
import com.example.viva_sample.tts.TtsActivity
import xyz.arpith.blearduino.R
import xyz.arpith.blearduino.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        DataBindingUtil.setContentView(
            this,
            R.layout.activity_main
        )
    }

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
    }
}