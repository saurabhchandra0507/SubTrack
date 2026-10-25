package com.example.subscriptiontracker.ui.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subscriptiontracker.data.AuthRepository
import com.example.subscriptiontracker.data.Subscription
import com.example.subscriptiontracker.data.SubscriptionRepository
import com.example.subscriptiontracker.data.monthlyCost
import com.example.subscriptiontracker.data.nextPaymentFrom
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

// A subscription plus its real next payment date (already rolled forward)
data class UpcomingPayment(
    val subscription: Subscription,
    val date: LocalDate,
    val daysLeft: Long,
)

data class DashboardUiState(
    val isLoading: Boolean = true,
    val firstName: String = "",
    val monthlyTotal: Double = 0.0,
    val yearlyTotal: Double = 0.0,
    val activeCount: Int = 0,
    val dueThisWeek: List<UpcomingPayment> = emptyList(),
    val all: List<UpcomingPayment> = emptyList(), // sorted, soonest first
)

// "Upcoming" means due within this many days
const val UPCOMING_DAYS = 7

class DashboardViewModel(
    subscriptionRepository: SubscriptionRepository,
    authRepository: AuthRepository,
) : ViewModel() {

    // combine() runs the block whenever EITHER flow sends a new value
    val uiState: StateFlow<DashboardUiState> = combine(
        subscriptionRepository.subscriptions,
        authRepository.account,
    ) { subscriptions, account ->
        val today = LocalDate.now()

        val payments = subscriptions
            .map { sub ->
                val date = sub.nextPaymentFrom(today)
                UpcomingPayment(sub, date, ChronoUnit.DAYS.between(today, date))
            }
            .sortedBy { it.date }

        val monthly = subscriptions.sumOf { it.monthlyCost }

        DashboardUiState(
            isLoading = false,
            firstName = account?.name?.substringBefore(" ").orEmpty(),
            monthlyTotal = monthly,
            yearlyTotal = monthly * 12,
            activeCount = subscriptions.size,
            dueThisWeek = payments.filter { it.daysLeft <= UPCOMING_DAYS },
            all = payments,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = DashboardUiState()
    )
}
