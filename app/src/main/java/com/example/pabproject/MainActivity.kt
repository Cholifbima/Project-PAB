package com.example.pabproject

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.pabproject.navigation.QRNavigation
import com.example.pabproject.ui.theme.PABProjectTheme
import com.example.pabproject.utils.ThemeManager
import com.example.pabproject.utils.HistoryManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

val LocalThemeManager = staticCompositionLocalOf<ThemeManager> {
    error("ThemeManager not provided")
}

val LocalHistoryManager = staticCompositionLocalOf<HistoryManager> {
    error("HistoryManager not provided")
}

class MainActivity : ComponentActivity() {
    private val themeManager: ThemeManager by viewModels()
    private val historyManager: HistoryManager by viewModels { 
        HistoryManagerFactory(applicationContext) 
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CompositionLocalProvider(
                LocalThemeManager provides themeManager,
                LocalHistoryManager provides historyManager
            ) {
                // Use a safe way to read the isDarkMode value
                val isDarkMode = themeManager.isDarkMode
                
                PABProjectTheme(darkTheme = isDarkMode) {
                    QRNavigation()
                }
            }
        }
    }
}

class HistoryManagerFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HistoryManager::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HistoryManager(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}