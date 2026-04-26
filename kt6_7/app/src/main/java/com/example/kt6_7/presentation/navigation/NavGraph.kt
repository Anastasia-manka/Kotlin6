package com.example.kt6_7.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kt6_7.presentation.ui.screen.HeartRateMonitorScreen
import com.example.kt6_7.presentation.ui.screen.ScanScreen

sealed class Screen(val route: String) {
    object Scan : Screen("scan")
    object Monitor : Screen("monitor/{deviceAddress}") {
        fun passAddress(address: String): String = "monitor/$address"
    }
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Scan.route
    ) {
        composable(Screen.Scan.route) {
            ScanScreen(navController)
        }
        composable(
            route = Screen.Monitor.route,
            arguments = listOf(navArgument("deviceAddress") { type = NavType.StringType })
        ) { backStackEntry ->
            val deviceAddress = backStackEntry.arguments?.getString("deviceAddress") ?: ""
            HeartRateMonitorScreen(navController, deviceAddress)
        }
    }
}