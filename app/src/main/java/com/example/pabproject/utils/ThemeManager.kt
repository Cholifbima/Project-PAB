package com.example.pabproject.utils

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ThemeManager : ViewModel() {
    private var _isDarkMode by mutableStateOf(false)
    val isDarkMode: Boolean get() = _isDarkMode
    
    // Animation properties
    private val scope = CoroutineScope(Dispatchers.Default)
    private val _animationProgress = Animatable(0f)
    val animationProgress: Float get() = _animationProgress.value
    
    // Theme transition duration in milliseconds
    private var transitionDuration: Int = 300
    
    // For improved accessibility
    private var _highContrastMode by mutableStateOf(false)
    val highContrastMode: Boolean get() = _highContrastMode
    
    fun toggleDarkMode() {
        _isDarkMode = !_isDarkMode
        animateThemeTransition()
    }
    
    fun setDarkMode(enabled: Boolean) {
        if (_isDarkMode != enabled) {
            _isDarkMode = enabled
            animateThemeTransition()
        }
    }
    
    fun setHighContrastMode(enabled: Boolean) {
        _highContrastMode = enabled
    }
    
    fun setTransitionDuration(durationMs: Int) {
        transitionDuration = durationMs
    }
    
    private fun animateThemeTransition() {
        scope.launch {
            _animationProgress.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = transitionDuration)
            )
            _animationProgress.snapTo(0f)
        }
    }
} 