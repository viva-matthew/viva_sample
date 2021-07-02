package com.example.viva_sample.room

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.viva_sample.R
import com.example.viva_sample.databinding.ActivityRoomBinding
import com.example.viva_sample.room.entitiy.HospitalEntity

class RoomActivity : AppCompatActivity() {
    private val binding: ActivityRoomBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_room)

    }
//    private val roomViewModel: RoomViewModel by viewModels()
    private val roomViewModel: RoomViewModel by viewModels()


    private var hospitalList = ArrayList<HospitalEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room)

        binding.lifecycleOwner = this
        binding.viewModel = roomViewModel

        initUI()
        initHospital()
    }

    private fun initHospital() {
//        roomViewModel.selectHosptal()?.observe(this, {
//
//            hospitalList.addAll(it)
//            binding.rvResult.adapter?.notifyDataSetChanged()
//        })

        val roomAdapter = RoomAdapter(hospitalList)
        binding.rvResult.adapter = roomAdapter
    }


    private fun initUI() {
        binding.btnSelect.setOnClickListener {
//            roomViewModel.selectHosptal()?.observe(this, {
//
//            })
        }

        binding.btnInsert.setOnClickListener {

        }

        binding.btnDelete.setOnClickListener {

        }
    }
}