package com.example.viva_sample.papgo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.example.viva_sample.R
import com.example.viva_sample.databinding.ActivityPapagoBinding

class PapagoActivity : AppCompatActivity() {
    private val binding: ActivityPapagoBinding by lazy { DataBindingUtil.setContentView(this, R.layout.activity_papago) }
    private val papagoViewModel: PapagoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.lifecycleOwner = this
        binding.viewModel = papagoViewModel


        binding.btnStart.setOnClickListener {
            papagoViewModel.translate()
        }
    }

}