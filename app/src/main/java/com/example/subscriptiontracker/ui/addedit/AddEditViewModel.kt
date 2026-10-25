package com.example.subscriptiontracker.ui.addedit

import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.subscriptiontracker.data.BillingCycle
import com.example.subscriptiontracker.data.Category
import com.example.subscriptiontracker.data.Subscription
import com.example.subscriptiontracker.data.SubscriptionRepository
import com.example.subscriptiontracker.ui.theme.SubscriptionColors
import java.time.LocalDate
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddEditUiState(
    val isEditing: Boolean = false,
    val name: String = "",
    val price: String = "", // kept as text while typing; turned into a number on save
    val billingCycle: BillingCycle = BillingCycle.MONTHLY,
    val nextPaymentDate: LocalDate = LocalDate.now(),
    val category: Category = Category.ENTERTAINMENT,
    val colorArgb: Int = SubscriptionColors.first().toArgb(),
    val notes: String = "",
    val nameError: String? = null,
    val priceError: String? = null,
    val isSaving: Boolean = false,
    val isDone: Boolean = false, // true after save/delete, so the screen closes
)

// Up to 7 digits, then optionally a dot and up to 2 decimals: "649", "1299.5", "99.99"
private val PRICE_PATTERN = Regex("""^\d{0,7}(\.\d{0,2})?$""")

class AddEditViewModel(
    savedStateHandle: SavedStateHandle,
    private val repository: SubscriptionRepository,
) : ViewModel() {

    // The "id" from the route add_edit?id=5. -1 means we're adding a new one.
    private val subscriptionId: Long = savedStateHandle.get<Long>("id") ?: -1L

    // The original row when editing, so we keep its id and createdAt
    private var original: Subscription? = null

    private val _uiState = MutableStateFlow(AddEditUiState(isEditing = subscriptionId != -1L))
    val uiState: StateFlow<AddEditUiState> = _uiState.asStateFlow()

    init {
        if (subscriptionId != -1L) {
            viewModelScope.launch {
                val saved = repository.getById(subscriptionId) ?: return@launch
                original = saved
                _uiState.update {
                    it.copy(
                        name = saved.name,
                        price = formatPriceInput(saved.price),
                        billingCycle = saved.billingCycle,
                        nextPaymentDate = saved.nextPaymentDate,
                        category = saved.category,
                        colorArgb = saved.colorArgb,
                        notes = saved.notes,
                    )
                }
            }
        }
    }

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value, nameError = null) }

    fun onPriceChange(value: String) {
        // Ignore key presses that would make the price invalid, like a second dot or letters
        if (PRICE_PATTERN.matches(value)) {
            _uiState.update { it.copy(price = value, priceError = null) }
        }
    }

    fun onCycleChange(value: BillingCycle) = _uiState.update { it.copy(billingCycle = value) }
    fun onDateChange(value: LocalDate) = _uiState.update { it.copy(nextPaymentDate = value) }
    fun onCategoryChange(value: Category) = _uiState.update { it.copy(category = value) }
    fun onColorChange(value: Int) = _uiState.update { it.copy(colorArgb = value) }
    fun onNotesChange(value: String) = _uiState.update { it.copy(notes = value) }

    fun save() {
        val state = _uiState.value
        val price = state.price.toDoubleOrNull()
        val nameError = if (state.name.isBlank()) "Enter a name" else null
        val priceError = if (price == null || price <= 0.0) "Enter a price above 0" else null
        if (nameError != null || priceError != null) {
            _uiState.update { it.copy(nameError = nameError, priceError = priceError) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val subscription = Subscription(
                id = original?.id ?: 0,
                name = state.name.trim(),
                price = price!!,
                billingCycle = state.billingCycle,
                nextPaymentDate = state.nextPaymentDate,
                category = state.category,
                colorArgb = state.colorArgb,
                notes = state.notes.trim(),
                createdAt = original?.createdAt ?: System.currentTimeMillis(),
            )
            if (original == null) repository.add(subscription) else repository.update(subscription)
            _uiState.update { it.copy(isSaving = false, isDone = true) }
        }
    }

    fun delete() {
        val toDelete = original ?: return
        viewModelScope.launch {
            repository.delete(toDelete)
            _uiState.update { it.copy(isDone = true) }
        }
    }

    // 649.0 -> "649", 99.5 -> "99.5"
    private fun formatPriceInput(price: Double): String =
        if (price % 1.0 == 0.0) price.toLong().toString() else price.toString()
}
