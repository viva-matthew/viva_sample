package com.example.viva_sample.room

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.example.viva_sample.room.entitiy.HospitalEntity
import com.orhanobut.logger.Logger
import com.taeyoung.gooddoctor.feature.hospital.model.Hospital
import java.util.concurrent.Executor
import java.util.concurrent.Executors

class RoomViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val mExecutor: Executor = Executors.newSingleThreadExecutor()

    fun selectHospital(): LiveData<List<HospitalEntity>>? {
        return db?.hospitalDao()?.selectHospital()
    }

    fun insertHospital(hospital: HospitalEntity) {
        Logger.d("## hospital ==> $hospital")
        mExecutor.execute { db?.hospitalDao()?.insert(hospital) }
    }

    fun deleteAllHospital() {
        mExecutor.execute { db?.hospitalDao()?.deleteAllHospital() }
    }

}