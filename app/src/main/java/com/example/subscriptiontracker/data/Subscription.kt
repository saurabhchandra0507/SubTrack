package com.example.subscriptiontracker.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

// How often you pay. perMonth lets us turn any price into a monthly cost (used in Phase 5).
enum class BillingCycle(val label: String, val shortLabel: String, val perMonth: Double) {
    WEEKLY("Weekly", "/wk", 52.0 / 12),
    MONTHLY("Monthly", "/mo", 1.0),
    QUARTERLY("Quarterly", "/qtr", 1.0 / 3),
    YEARLY("Yearly", "/yr", 1.0 / 12),
}

enum class Category(val label: String) {
    ENTERTAINMENT("Entertainment"),
    MUSIC("Music"),
    PRODUCTIVITY("Productivity"),
    CLOUD("Cloud"),
    FITNESS("Fitness"),
    NEWS("News"),
    GAMING("Gaming"),
    EDUCATION("Education"),
    OTHER("Other"),
}

// One row in the "subscriptions" table. Each property becomes a column.
@Entity(tableName = "subscriptions")
data class Subscription(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // 0 = "new", Room picks the real id
    val name: String,
    val price: Double,
    val billingCycle: BillingCycle,  // Room saves enums as text, e.g. "MONTHLY"
    val nextPaymentDate: LocalDate,  // saved as a number using Converters
    val category: Category,
    val colorArgb: Int,              // the colour as a number, e.g. 0xFFE50914
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
)
