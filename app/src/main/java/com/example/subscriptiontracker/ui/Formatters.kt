package com.example.subscriptiontracker.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.subscriptiontracker.data.AppCurrency
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// The chosen currency, available to every screen without passing it around.
// MainActivity sets it from the saved setting (see CompositionLocalProvider there).
val LocalCurrency = staticCompositionLocalOf { AppCurrency.INR }

private val moneyFormat = DecimalFormat("#,##0.##")
private val dateFormat = DateTimeFormatter.ofPattern("d MMM yyyy")
private val shortDateFormat = DateTimeFormatter.ofPattern("d MMM")

fun formatPrice(amount: Double, currency: AppCurrency): String = currency.symbol + moneyFormat.format(amount)

// Use inside composables: money(649.0) -> "₹649" (or "$649" if the user picked dollars)
@Composable
@ReadOnlyComposable
fun money(amount: Double): String = formatPrice(amount, LocalCurrency.current)

fun formatDate(date: LocalDate): String = date.format(dateFormat)

fun formatShortDate(date: LocalDate): String = date.format(shortDateFormat)

// "Due today", "Due tomorrow", "Due in 5 days"
fun dueLabel(daysLeft: Long): String = when (daysLeft) {
    0L -> "Due today"
    1L -> "Due tomorrow"
    else -> "Due in $daysLeft days"
}
