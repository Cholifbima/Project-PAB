package com.example.pabproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.pabproject.navigation.QRNavigation
import com.example.pabproject.ui.theme.PABProjectTheme
import com.example.pabproject.utils.ThemeManager

val LocalThemeManager = staticCompositionLocalOf<ThemeManager> {
    error("ThemeManager not provided")
}

class MainActivity : ComponentActivity() {
    private val themeManager: ThemeManager by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(LocalThemeManager provides themeManager) {
                PABProjectTheme(darkTheme = themeManager.isDarkMode) {
                    QRNavigation()
                }
            }
        }
    }
}