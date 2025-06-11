package com.example.pabproject.utils

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel

data class CustomColorEntry(
    val color: Color,
    val hexCode: String,
    val usageCount: Int = 1,
    val lastUsed: Long = System.currentTimeMillis()
)

class CustomColorManager : ViewModel() {
    private val _customColors = mutableStateListOf<CustomColorEntry>()
    val customColors: List<CustomColorEntry> = _customColors
    
    fun addCustomColor(color: Color) {
        val hexCode = color.toHexString()
        
        // Check if color already exists
        val existingIndex = _customColors.indexOfFirst { it.hexCode == hexCode }
        
        if (existingIndex >= 0) {
            // Update existing color usage
            val existing = _customColors[existingIndex]
            _customColors[existingIndex] = existing.copy(
                usageCount = existing.usageCount + 1,
                lastUsed = System.currentTimeMillis()
            )
        } else {
            // Add new custom color
            val newEntry = CustomColorEntry(
                color = color,
                hexCode = hexCode
            )
            _customColors.add(0, newEntry) // Add to beginning
            
            // Keep only last 20 custom colors
            if (_customColors.size > 20) {
                _customColors.removeAt(_customColors.size - 1)
            }
        }
        
        // Sort by usage count and recency
        _customColors.sortWith(compareByDescending<CustomColorEntry> { it.usageCount }.thenByDescending { it.lastUsed })
    }
    
    fun getRecentColors(limit: Int = 6): List<Color> {
        return _customColors.take(limit).map { it.color }
    }
    
    fun getMostUsedColors(limit: Int = 6): List<Color> {
        return _customColors.sortedByDescending { it.usageCount }.take(limit).map { it.color }
    }
    
    fun clearCustomColors() {
        _customColors.clear()
    }
}

// Extension function
private fun Color.toHexString(): String {
    val red = (this.red * 255).toInt()
    val green = (this.green * 255).toInt()
    val blue = (this.blue * 255).toInt()
    return String.format("#%02X%02X%02X", red, green, blue)
} 