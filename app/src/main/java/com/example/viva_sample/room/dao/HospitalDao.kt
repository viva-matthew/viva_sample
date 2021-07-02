package com.example.viva_sample.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import com.example.viva_sample.room.entitiy.HospitalEntity
import com.taeyoung.gooddoctor.feature.hospital.model.Hospital

@Dao
interface HospitalDao {
    @Query("SELECT * FROM HOSPITAL")
    fun selectHospital(): LiveData<List<HospitalEntity>>
}