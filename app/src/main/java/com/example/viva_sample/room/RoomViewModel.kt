package com.example.viva_sample.room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.viva_sample.room.entitiy.HospitalEntity
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class RoomViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val mExecutor: Executor = Executors.newSingleThreadExecutor()

//    fun selectHosptal(): LiveData<List<HospitalEntity>>? {
//        return db?.hospitalDao()?.selectHospital()
//    }
}