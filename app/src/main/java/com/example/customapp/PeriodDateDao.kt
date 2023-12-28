package com.example.customapp

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface PeriodDateDao {

    @Query("SELECT * FROM PeriodDateEntity limit 1")
    fun get(): PeriodDateEntity

    @Query("SELECT * FROM PeriodDateEntity")
    fun getAll(): List<PeriodDateEntity>

    @Insert
    fun insert(PeriodDateEntity: PeriodDateEntity)

    @Query("DELETE FROM PeriodDateEntity")
    fun deleteAll()
}