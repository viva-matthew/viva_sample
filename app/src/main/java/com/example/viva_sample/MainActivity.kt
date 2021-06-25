package com.example.viva_sample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.viva_sample.BLE.bluetoothlegatt.DeviceScanActivity
import com.example.viva_sample.stt.SttActivity
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
    }
}