package com.example.kt6_3.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.kt6_3.presentation.ui.screen.LoginScreen
import com.example.kt6_3.presentation.ui.screen.UserDetailScreen
import com.example.kt6_3.presentation.ui.screen.UsersListScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object UsersList : Screen("users_list")
    object UserDetail : Screen("user_detail/{userId}") {
        fun passId(userId: Int): String = "user_detail/$userId"
    }
}

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.UsersList.route) {
            UsersListScreen(navController)
        }
        composable(
            route = Screen.UserDetail.route,
            arguments = listOf(navArgument("userId") { type = NavType.IntType })
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getInt("userId") ?: -1
            UserDetailScreen(navController, userId)
        }
    }
}