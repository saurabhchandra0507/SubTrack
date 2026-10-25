package com.example.subscriptiontracker.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

// Lists every table (entities) and the version number.
// If you change the Subscription columns later, the version must go up.
@Database(entities = [Subscription::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun subscriptionDao(): SubscriptionDao
}
