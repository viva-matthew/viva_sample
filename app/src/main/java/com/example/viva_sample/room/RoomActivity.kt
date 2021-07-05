package com.example.viva_sample.room

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.viva_sample.R
import com.example.viva_sample.databinding.ActivityRoomBinding
import com.example.viva_sample.room.entitiy.HospitalEntity

class RoomActivity : AppCompatActivity() {
    private val binding: ActivityRoomBinding by lazy { DataBindingUtil.setContentView(this, R.layout.activity_room) }
    private val roomViewModel: RoomViewModel by viewModels()
    //private var hospitalList = ArrayList<HospitalEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)

        binding.lifecycleOwner = this
        binding.viewModel = roomViewModel

        initUI()
        initHospital()
    }

    private fun initHospital() {
        val hospitalList = ArrayList<HospitalEntity>()
        roomViewModel.selectHospital()?.observe(this, {
            hospitalList.clear()
            hospitalList.addAll(it)
            binding.rvResult.adapter?.notifyDataSetChanged()
        })

        Toast.makeText(this, "hi", Toast.LENGTH_LONG).show()

        val roomAdapter = RoomAdapter(hospitalList)
        binding.rvResult.adapter = roomAdapter
    }


    private fun initUI() {
        binding.btnInsert.setOnClickListener {
            val hospital = HospitalEntity(
                title = binding.etSendText.text.toString()
            )
            roomViewModel.insertHospital(hospital)
            binding.etSendText.text.clear()
        }

        binding.btnDelete.setOnClickListener {
            roomViewModel.deleteAllHospital()
        }
    }
}