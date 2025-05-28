package com.example.pabproject.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Sms
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import com.example.pabproject.ui.theme.Brown
import com.example.pabproject.ui.theme.Gold
import com.example.pabproject.ui.theme.LightBrown
import com.example.pabproject.ui.theme.SandyBeige
import java.text.SimpleDateFormat
import java.util.*

data class HistoryItem(
    val id: String = UUID.randomUUID().toString(),
    val type: String,
    val content: String,
    val timestamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),
    val icon: ImageVector = Icons.Default.QrCode,
    val iconTint: Color = Brown
)

class HistoryManager : ViewModel() {
    private val _historyItems = mutableStateListOf<HistoryItem>()
    val historyItems: List<HistoryItem> = _historyItems
    
    fun addHistoryItem(type: String, content: String) {
        val (icon, color) = when {
            type.contains("Email", ignoreCase = true) -> Icons.Default.Email to Gold
            type.contains("URL", ignoreCase = true) -> Icons.Default.Link to LightBrown
            type.contains("Text", ignoreCase = true) -> Icons.Default.TextFields to SandyBeige
            type.contains("Scanned", ignoreCase = true) -> Icons.Default.QrCodeScanner to Brown
            type.contains("SMS", ignoreCase = true) -> Icons.Default.Sms to SandyBeige
            type.contains("Twitter", ignoreCase = true) -> Icons.Default.Tag to LightBrown
            type.contains("WiFi", ignoreCase = true) -> Icons.Default.Wifi to Gold
            else -> Icons.Default.QrCode to Brown
        }
        
        val newItem = HistoryItem(
            type = type,
            content = content,
            icon = icon,
            iconTint = color
        )
        _historyItems.add(0, newItem) // Add to beginning of list
        
        // Keep only last 50 items
        if (_historyItems.size > 50) {
            _historyItems.removeAt(_historyItems.size - 1)
        }
    }
    
    fun removeHistoryItem(id: String) {
        val indexToRemove = _historyItems.indexOfFirst { it.id == id }
        if (indexToRemove >= 0) {
            _historyItems.removeAt(indexToRemove)
        }
    }
    
    fun getHistoryItem(id: String): HistoryItem? {
        return _historyItems.find { it.id == id }
    }
    
    fun clearHistory(): Boolean {
        if (_historyItems.isNotEmpty()) {
            _historyItems.clear()
            return true
        }
        return false
    }
    
    init {
        // Add sample data for testing
        addHistoryItem("Text QR", "Hello World!")
        addHistoryItem("URL QR", "https://www.google.com")
        addHistoryItem("Email QR", "test@example.com")
        addHistoryItem("SMS QR", "0123456789:Hello from SMS")
        addHistoryItem("Twitter Profile QR", "https://twitter.com/username")
        addHistoryItem("WiFi QR", "WIFI:S:MyNetwork;T:WPA;P:mypassword;;")
        addHistoryItem("Scanned", "https://www.figma.com")
    }
} 