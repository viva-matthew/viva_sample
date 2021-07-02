package com.example.viva_sample.room.dao

import androidx.room.*

interface BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE) // 충돌이 발생할 경우 덮어쓰기
    fun insert(obj: T)

    @Update
    fun update(obj: T)

    @Delete
    fun delete(obj: T)

}

/*
OnConflictStrategy.ABORT        // 충돌이 발생할 경우 처리 중단
OnConflictStrategy.FAIL         // 충돌이 발생할 경우 실패처리
OnConflictStrategy.IGNORE       // 충돌이 발생할 경우 무시
OnConflictStrategy.REPLACE      // 충돌이 발생할 경우 덮어쓰기
OnConflictStrategy.ROLLBACK     // 충돌이 발생할 경우 이전으로 되돌리기
 */

