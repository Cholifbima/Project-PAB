package com.example.pabproject.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ThemeManager : ViewModel() {
    private var _isDarkMode by mutableStateOf(false)
    val isDarkMode: Boolean get() = _isDarkMode
    
    fun toggleDarkMode() {
        _isDarkMode = !_isDarkMode
    }
    
    fun setDarkMode(enabled: Boolean) {
        _isDarkMode = enabled
    }
} 