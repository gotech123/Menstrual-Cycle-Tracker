package com.example.customapp

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity
data class DiaryEntity(@PrimaryKey val date: LocalDate, @ColumnInfo(name = "diary") val diary: String)