package com.example.pabproject.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pabproject.ui.screens.DashboardScreen
import com.example.pabproject.ui.screens.MenuScreen
import com.example.pabproject.ui.screens.SettingsScreen
import com.example.pabproject.ui.screens.TextToQRScreen
import com.example.pabproject.ui.screens.URLToQRScreen
import com.example.pabproject.ui.screens.EmailToQRScreen
import com.example.pabproject.ui.screens.QRScannerScreen
import com.example.pabproject.ui.screens.HistoryScreen
import com.example.pabproject.ui.screens.QRDetailScreen
import com.example.pabproject.ui.screens.SmsToQRScreen
import com.example.pabproject.ui.screens.TwitterToQRScreen
import com.example.pabproject.ui.screens.WifiToQRScreen

sealed class Screen(val route: String) {
    data object Dashboard : Screen("dashboard")
    data object Menu : Screen("menu")
    data object Settings : Screen("settings")
    data object TextToQR : Screen("text_to_qr")
    data object URLToQR : Screen("url_to_qr")
    data object EmailToQR : Screen("email_to_qr")
    data object SmsToQR : Screen("sms_to_qr")
    data object TwitterToQR : Screen("twitter_to_qr")
    data object WifiToQR : Screen("wifi_to_qr")
    data object QRScanner : Screen("qr_scanner")
    data object History : Screen("history")
    data object QRDetail : Screen("qr_detail")
}

@Composable
fun QRNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(navController = navController)
        }
        composable(Screen.Menu.route) {
            MenuScreen(navController = navController)
        }
        composable(Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }
        composable(Screen.TextToQR.route) {
            TextToQRScreen(navController = navController)
        }
        composable(Screen.URLToQR.route) {
            URLToQRScreen(navController = navController)
        }
        composable(Screen.EmailToQR.route) {
            EmailToQRScreen(navController = navController)
        }
        composable(Screen.SmsToQR.route) {
            SmsToQRScreen(navController = navController)
        }
        composable(Screen.TwitterToQR.route) {
            TwitterToQRScreen(navController = navController)
        }
        composable(Screen.WifiToQR.route) {
            WifiToQRScreen(navController = navController)
        }
        composable(Screen.QRScanner.route) {
            QRScannerScreen(navController = navController)
        }
        composable(Screen.History.route) {
            HistoryScreen(navController = navController)
        }
        composable("${Screen.QRDetail.route}/{historyItemId}") { backStackEntry ->
            val historyItemId = backStackEntry.arguments?.getString("historyItemId") ?: ""
            QRDetailScreen(
                navController = navController,
                historyItemId = historyItemId
            )
        }
    }
} 