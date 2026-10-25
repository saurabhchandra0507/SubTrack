package com.example.subscriptiontracker.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

// You write the function + SQL, Room generates the code that runs it
@Dao
interface SubscriptionDao {

    // Returns a Flow: the list is sent again automatically whenever the table changes
    @Query("SELECT * FROM subscriptions ORDER BY nextPaymentDate ASC")
    fun getAll(): Flow<List<Subscription>>

    @Query("SELECT * FROM subscriptions WHERE id = :id")
    suspend fun getById(id: Long): Subscription?

    @Insert
    suspend fun insert(subscription: Subscription)

    @Update
    suspend fun update(subscription: Subscription)

    @Delete
    suspend fun delete(subscription: Subscription)

    @Query("DELETE FROM subscriptions")
    suspend fun deleteAll()
}
