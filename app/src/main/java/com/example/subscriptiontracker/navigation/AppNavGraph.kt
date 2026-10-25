package com.example.subscriptiontracker.navigation

import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.subscriptiontracker.ui.addedit.AddEditScreen
import com.example.subscriptiontracker.ui.auth.LoginScreen
import com.example.subscriptiontracker.ui.auth.SignUpScreen
import com.example.subscriptiontracker.ui.dashboard.DashboardScreen
import com.example.subscriptiontracker.ui.insights.InsightsScreen
import com.example.subscriptiontracker.ui.profile.ProfileScreen
import com.example.subscriptiontracker.ui.splash.SplashScreen

@Composable
fun AppNavGraph(navController: NavHostController = rememberNavController()) {
    // Which screen is showing right now? Used to decide if the bottom bar appears
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar = BottomTab.entries.any { it.route == currentRoute }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                BottomBar(
                    currentRoute = currentRoute,
                    onTabClick = { tab -> navController.navigateToTab(tab.route) }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH,
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding) // so imePadding() on screens doesn't add space twice
        ) {
            composable(Routes.SPLASH) {
                SplashScreen(
                    onFinished = { isLoggedIn ->
                        // Already logged in? Skip the login screen
                        navController.navigate(if (isLoggedIn) Routes.HOME else Routes.LOGIN) {
                            popUpTo(Routes.SPLASH) { inclusive = true } // Back won't return to splash
                        }
                    }
                )
            }

            composable(Routes.LOGIN) {
                LoginScreen(
                    onLoginSuccess = { navController.goHomeClearingAuth() },
                    onSignUpClick = { navController.navigate(Routes.SIGN_UP) }
                )
            }

            composable(Routes.SIGN_UP) {
                SignUpScreen(
                    onSignUpSuccess = { navController.goHomeClearingAuth() },
                    onBackToLogin = { navController.popBackStack() }
                )
            }

            composable(Routes.HOME) {
                DashboardScreen(
                    onAddClick = { navController.navigate(Routes.addEdit()) },
                    onSubscriptionClick = { id -> navController.navigate(Routes.addEdit(id)) },
                    onProfileClick = { navController.navigateToTab(Routes.PROFILE) }
                )
            }

            composable(Routes.INSIGHTS) {
                InsightsScreen(onSubscriptionClick = { id -> navController.navigate(Routes.addEdit(id)) })
            }

            composable(Routes.PROFILE) {
                ProfileScreen(
                    onLogout = {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(0) { inclusive = true } // clear everything
                        }
                    }
                )
            }

            composable(
                route = Routes.ADD_EDIT,
                arguments = listOf(navArgument("id") {
                    type = NavType.LongType
                    defaultValue = -1L // -1 = adding a new subscription
                })
            ) {
                AddEditScreen(onBack = { navController.popBackStack() })
            }
        }
    }
}

// After login/sign up: open Home and remove Login + Sign Up from the back stack
private fun NavHostController.goHomeClearingAuth() {
    navigate(Routes.HOME) {
        popUpTo(Routes.LOGIN) { inclusive = true }
    }
}

// Switching tabs: keep only Home under the current tab, and don't stack duplicate copies
private fun NavHostController.navigateToTab(route: String) {
    navigate(route) {
        popUpTo(Routes.HOME) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
