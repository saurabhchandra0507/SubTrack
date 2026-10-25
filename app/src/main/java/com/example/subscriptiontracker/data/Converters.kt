package com.example.subscriptiontracker.data

import androidx.room.TypeConverter
import java.time.LocalDate

// SQLite can't store a LocalDate, so we store it as "days since 1 Jan 1970"
class Converters {
    @TypeConverter
    fun fromDate(date: LocalDate): Long = date.toEpochDay()

    @TypeConverter
    fun toDate(epochDay: Long): LocalDate = LocalDate.ofEpochDay(epochDay)
}
