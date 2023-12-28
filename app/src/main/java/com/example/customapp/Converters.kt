package com.example.customapp

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromString(value: String?): LocalDate? {
        val splitted = value?.split("-")?.map { x -> x.toInt() }
        return splitted?.let { LocalDate.of(it[0], it[1], it[2]) }
    }

    @TypeConverter
    fun fromLocalDate(value: LocalDate?): String?  {
        return value?.toString()
    }
}