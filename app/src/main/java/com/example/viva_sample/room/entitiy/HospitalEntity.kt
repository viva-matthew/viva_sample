package com.example.viva_sample.room.entitiy

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "HOSPITAL")
class HospitalEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "hospital_id", index = true)
    var hospitalId: Long = 0,

    @ColumnInfo(name = "title")
    var title: String = "",

) {

}