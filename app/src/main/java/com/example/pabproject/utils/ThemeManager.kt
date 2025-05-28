package com.example.pabproject.utils

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class ThemeManager : ViewModel() {
    // Use backing properties to avoid name conflicts with setter methods
    private val _isDarkMode = mutableStateOf(false)
    val isDarkMode: Boolean get() = _isDarkMode.value
    
    private val _highContrastMode = mutableStateOf(false)
    val highContrastMode: Boolean get() = _highContrastMode.value
    
    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
    }
    
    fun setDarkMode(enabled: Boolean) {
        _isDarkMode.value = enabled
    }
    
    fun setHighContrastMode(enabled: Boolean) {
        _highContrastMode.value = enabled
    }
} 