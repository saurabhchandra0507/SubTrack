package com.example.subscriptiontracker.data

enum class ThemeMode(val label: String) {
    SYSTEM("System"),
    LIGHT("Light"),
    DARK("Dark"),
}

// Only changes the symbol shown; amounts are not converted between currencies
enum class AppCurrency(val symbol: String, val label: String) {
    INR("₹", "Indian rupee"),
    USD("$", "US dollar"),
    EUR("€", "Euro"),
    GBP("£", "British pound"),
    JPY("¥", "Japanese yen"),
}
