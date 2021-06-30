package com.example.viva_sample.firesotre

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.example.viva_sample.R
import com.example.viva_sample.databinding.ActivityFireStoreBinding
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.orhanobut.logger.Logger

class FireStoreActivity : AppCompatActivity() {
    private val binding: ActivityFireStoreBinding by lazy { DataBindingUtil.setContentView(this, R.layout.activity_fire_store) }
    private val fireStoreViewModel: FireStoreViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fire_store)

        binding.lifecycleOwner = this
        binding.viewModel = fireStoreViewModel

        init()


    }

    private fun init() {
        binding.btnCreate.setOnClickListener { fireStoreViewModel.createCollection() }
        binding.btnSelect.setOnClickListener { fireStoreViewModel.selectCollection() }
        binding.btnDelete.setOnClickListener { fireStoreViewModel.deleteFirestore() }


        fireStoreViewModel.fireStoreMessage.observe(this) {
            Toast.makeText(this, "컬렉션 아이디 ==> $it", Toast.LENGTH_LONG).show()
        }


    }

}