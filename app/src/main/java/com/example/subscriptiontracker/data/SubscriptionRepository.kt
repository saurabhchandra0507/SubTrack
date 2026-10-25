package com.example.subscriptiontracker.data

import kotlinx.coroutines.flow.Flow

// The only class that talks to the subscriptions table
class SubscriptionRepository(private val dao: SubscriptionDao) {

    val subscriptions: Flow<List<Subscription>> = dao.getAll()

    suspend fun getById(id: Long): Subscription? = dao.getById(id)

    suspend fun add(subscription: Subscription) = dao.insert(subscription)

    suspend fun update(subscription: Subscription) = dao.update(subscription)

    suspend fun delete(subscription: Subscription) = dao.delete(subscription)

    suspend fun deleteAll() = dao.deleteAll()
}
