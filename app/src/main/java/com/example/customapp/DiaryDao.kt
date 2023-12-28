package com.example.customapp

import androidx.room.*
import java.time.LocalDate

@Dao
interface DiaryDao {

    @Query("SELECT * FROM DiaryEntity WHERE date = :date")
    fun get(date: String): DiaryEntity?

    @Insert
    fun insert(DiaryEntity: DiaryEntity)

    @Query("DELETE FROM DiaryEntity WHERE date= :date")
    fun delete(date: String)

    @Update
    fun update(DiaryEntity: DiaryEntity)

    @Query("DELETE FROM DiaryEntity") //debug
    fun deleteAll()
}