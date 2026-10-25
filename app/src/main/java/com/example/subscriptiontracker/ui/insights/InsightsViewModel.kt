package com.example.subscriptiontracker.ui.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subscriptiontracker.data.Category
import com.example.subscriptiontracker.data.Subscription
import com.example.subscriptiontracker.data.SubscriptionRepository
import com.example.subscriptiontracker.data.monthlyCost
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

// How much one category costs per month, and its share of the total (0.0 to 1.0)
data class CategorySpend(val category: Category, val monthly: Double, val share: Float)

data class InsightsUiState(
    val isLoading: Boolean = true,
    val monthlyTotal: Double = 0.0,
    val yearlyTotal: Double = 0.0,
    val averagePerSubscription: Double = 0.0,
    val categories: List<CategorySpend> = emptyList(),   // biggest first
    val mostExpensive: List<Subscription> = emptyList(), // top 3 by monthly cost
)

class InsightsViewModel(repository: SubscriptionRepository) : ViewModel() {

    val uiState: StateFlow<InsightsUiState> = repository.subscriptions
        .map { subscriptions ->
            val total = subscriptions.sumOf { it.monthlyCost }

            // groupBy makes a map like { MUSIC -> [Spotify, YouTube Music], CLOUD -> [iCloud] }
            val categories = subscriptions
                .groupBy { it.category }
                .map { (category, subs) ->
                    val monthly = subs.sumOf { it.monthlyCost }
                    CategorySpend(category, monthly, if (total > 0) (monthly / total).toFloat() else 0f)
                }
                .sortedByDescending { it.monthly }

            InsightsUiState(
                isLoading = false,
                monthlyTotal = total,
                yearlyTotal = total * 12,
                averagePerSubscription = if (subscriptions.isEmpty()) 0.0 else total / subscriptions.size,
                categories = categories,
                mostExpensive = subscriptions.sortedByDescending { it.monthlyCost }.take(3),
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = InsightsUiState()
        )
}
