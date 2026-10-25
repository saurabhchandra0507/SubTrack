package com.example.subscriptiontracker

import com.example.subscriptiontracker.data.BillingCycle
import com.example.subscriptiontracker.data.Category
import com.example.subscriptiontracker.data.Subscription
import com.example.subscriptiontracker.data.monthlyCost
import com.example.subscriptiontracker.data.nextPaymentFrom
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

// Runs on your computer (no phone needed): right-click this file > Run 'BillingMathTest'
class BillingMathTest {

    private fun sub(cycle: BillingCycle, date: LocalDate, price: Double = 100.0) = Subscription(
        name = "Test",
        price = price,
        billingCycle = cycle,
        nextPaymentDate = date,
        category = Category.OTHER,
        colorArgb = 0,
    )

    private val today = LocalDate.of(2026, 10, 21)

    @Test
    fun futureDate_staysTheSame() {
        val s = sub(BillingCycle.MONTHLY, LocalDate.of(2026, 11, 5))
        assertEquals(LocalDate.of(2026, 11, 5), s.nextPaymentFrom(today))
    }

    @Test
    fun today_countsAsDue() {
        val s = sub(BillingCycle.MONTHLY, today)
        assertEquals(today, s.nextPaymentFrom(today))
    }

    @Test
    fun pastMonthly_rollsToNextMonth() {
        val s = sub(BillingCycle.MONTHLY, LocalDate.of(2026, 10, 1))
        assertEquals(LocalDate.of(2026, 11, 1), s.nextPaymentFrom(today))
    }

    @Test
    fun pastWeekly_rollsByWeeks() {
        val s = sub(BillingCycle.WEEKLY, LocalDate.of(2026, 10, 1)) // Thursday
        assertEquals(LocalDate.of(2026, 10, 22), s.nextPaymentFrom(today))
    }

    @Test
    fun monthEnd_keepsDay31WhenPossible() {
        val s = sub(BillingCycle.MONTHLY, LocalDate.of(2026, 1, 31))
        assertEquals(LocalDate.of(2026, 2, 28), s.nextPaymentFrom(LocalDate.of(2026, 2, 1)))
        assertEquals(LocalDate.of(2026, 3, 31), s.nextPaymentFrom(LocalDate.of(2026, 3, 1)))
    }

    @Test
    fun pastYearly_rollsByYears() {
        val s = sub(BillingCycle.YEARLY, LocalDate.of(2024, 3, 10))
        assertEquals(LocalDate.of(2027, 3, 10), s.nextPaymentFrom(today))
    }

    @Test
    fun monthlyCost_convertsEachCycle() {
        assertEquals(100.0, sub(BillingCycle.MONTHLY, today, 100.0).monthlyCost, 0.001)
        assertEquals(100.0, sub(BillingCycle.YEARLY, today, 1200.0).monthlyCost, 0.001)
        assertEquals(100.0, sub(BillingCycle.QUARTERLY, today, 300.0).monthlyCost, 0.001)
        assertEquals(433.333, sub(BillingCycle.WEEKLY, today, 100.0).monthlyCost, 0.001)
    }
}
