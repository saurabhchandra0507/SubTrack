package com.example.subscriptiontracker.data

import java.time.LocalDate

// What this subscription costs per month, so different cycles can be added together.
// e.g. ₹1,200 yearly -> ₹100 a month, ₹100 weekly -> about ₹433 a month
val Subscription.monthlyCost: Double
    get() = price * billingCycle.perMonth

// The saved date may already be in the past (e.g. saved "1 Oct", today is 21 Oct).
// Step forward one cycle at a time until we reach today or later.
fun Subscription.nextPaymentFrom(today: LocalDate): LocalDate {
    var cyclesAhead = 0L
    var date = nextPaymentDate
    while (date.isBefore(today)) {
        cyclesAhead++
        // Always count from the original date, so "31 Jan" monthly gives 28 Feb, then 31 Mar (not 28 Mar)
        date = when (billingCycle) {
            BillingCycle.WEEKLY -> nextPaymentDate.plusWeeks(cyclesAhead)
            BillingCycle.MONTHLY -> nextPaymentDate.plusMonths(cyclesAhead)
            BillingCycle.QUARTERLY -> nextPaymentDate.plusMonths(3 * cyclesAhead)
            BillingCycle.YEARLY -> nextPaymentDate.plusYears(cyclesAhead)
        }
    }
    return date
}
