package com.example.viva_sample.lottie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.viva_sample.R
import com.example.viva_sample.databinding.ActivityLottieBinding

class LottieActivity : AppCompatActivity() {
    private val binding: ActivityLottieBinding by lazy { DataBindingUtil.setContentView(this, R.layout.activity_lottie) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.lifecycleOwner = this

        init()
    }

    private fun init() {

    }
}