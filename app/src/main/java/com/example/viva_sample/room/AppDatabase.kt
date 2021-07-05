package com.example.viva_sample.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.viva_sample.room.dao.HospitalDao
import com.example.viva_sample.room.entitiy.HospitalEntity
import com.taeyoung.gooddoctor.feature.hospital.model.Hospital

// AppDatabase.kt
@Database(entities = [HospitalEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun hospitalDao(): HospitalDao
    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase? {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        "viva_sample.db"

                    )
                        .fallbackToDestructiveMigration() // 마이그레이션 처리안해도 됌
                        .build()
                }
            }
            return INSTANCE
        }
    }
}
