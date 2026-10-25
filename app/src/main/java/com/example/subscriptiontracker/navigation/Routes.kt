package com.example.subscriptiontracker.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material.icons.rounded.PieChart
import androidx.compose.ui.graphics.vector.ImageVector

// Every screen in the app has a unique route name
object Routes {
    const val SPLASH = "splash"
    const val LOGIN = "login"
    const val SIGN_UP = "sign_up"
    const val HOME = "home"
    const val INSIGHTS = "insights"
    const val PROFILE = "profile"
    // {id} is an optional argument: "add_edit" = new, "add_edit?id=5" = edit #5
    const val ADD_EDIT = "add_edit?id={id}"

    fun addEdit(id: Long? = null): String = if (id == null) "add_edit" else "add_edit?id=$id"
}

// The three tabs shown in the bottom bar
enum class BottomTab(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    Home(Routes.HOME, "Home", Icons.Rounded.Home, Icons.Outlined.Home),
    Insights(Routes.INSIGHTS, "Insights", Icons.Rounded.PieChart, Icons.Outlined.PieChart),
    Profile(Routes.PROFILE, "Profile", Icons.Rounded.Person, Icons.Outlined.Person),
}
