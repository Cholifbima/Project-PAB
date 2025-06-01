package com.example.pabproject.utils

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class ThemeManager : ViewModel() {
    // Use backing properties to avoid name conflicts with setter methods
    private val _isDarkMode = mutableStateOf(false)
    val isDarkMode: Boolean get() = _isDarkMode.value

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }
    
    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }

}