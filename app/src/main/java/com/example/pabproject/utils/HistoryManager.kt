package com.example.pabproject.utils

import android.content.Context
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
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pabproject.data.AppDatabase
import com.example.pabproject.data.HistoryEntity
import com.example.pabproject.ui.theme.Brown
import com.example.pabproject.ui.theme.Gold
import com.example.pabproject.ui.theme.LightBrown
import com.example.pabproject.ui.theme.SandyBeige
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class HistoryItem(
    val id: String = UUID.randomUUID().toString(),
    val type: String,
    val content: String,
    val timestamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date()),
    val icon: ImageVector = Icons.Default.QrCode,
    val iconTint: Color = Brown,
    // Custom QR colors
    val qrColor: Int = Color.Black.toArgb(),
    val backgroundColor: Int = Color.White.toArgb()
)

class HistoryManager(private val context: Context) : ViewModel() {
    private val database = AppDatabase.getDatabase(context)
    private val historyDao = database.historyDao()
    
    private val _historyItems = mutableStateListOf<HistoryItem>()
    val historyItems: List<HistoryItem> = _historyItems
    
    init {
        viewModelScope.launch {
            historyDao.getAllHistory().collect { entities ->
                _historyItems.clear()
                entities.forEach { entity ->
                    val (icon, color) = getIconAndColor(entity.type)
                    _historyItems.add(entity.toHistoryItem(icon, color))
                }
            }
        }
    }
    
    private fun getIconAndColor(type: String): Pair<ImageVector, Color> {
        return when {
            type.contains("Email", ignoreCase = true) -> Icons.Default.Email to Gold
            type.contains("URL", ignoreCase = true) -> Icons.Default.Link to LightBrown
            type.contains("Text", ignoreCase = true) -> Icons.Default.TextFields to SandyBeige
            type.contains("Scanned", ignoreCase = true) -> Icons.Default.QrCodeScanner to Brown
            type.contains("SMS", ignoreCase = true) -> Icons.Default.Sms to SandyBeige
            type.contains("Twitter", ignoreCase = true) -> Icons.Default.Tag to LightBrown
            type.contains("WiFi", ignoreCase = true) -> Icons.Default.Wifi to Gold
            else -> Icons.Default.QrCode to Brown
        }
    }
    
    fun addHistoryItem(
        type: String, 
        content: String, 
        qrColor: Color = Color.Black, 
        backgroundColor: Color = Color.White
    ) {
        viewModelScope.launch {
            val (icon, color) = getIconAndColor(type)
            val newItem = HistoryItem(
                type = type,
                content = content,
                icon = icon,
                iconTint = color,
                qrColor = qrColor.toArgb(),
                backgroundColor = backgroundColor.toArgb()
            )
            historyDao.insertHistory(HistoryEntity.fromHistoryItem(newItem))
        }
    }
    
    fun removeHistoryItem(id: String) {
        viewModelScope.launch {
            historyDao.getHistoryById(id)?.let { entity ->
                historyDao.deleteHistory(entity)
            }
        }
    }
    
    fun getHistoryItem(id: String): HistoryItem? {
        return _historyItems.find { it.id == id }
    }
    
    fun clearHistory(): Boolean {
        if (_historyItems.isNotEmpty()) {
            viewModelScope.launch {
                historyDao.clearHistory()
            }
            return true
        }
        return false
    }
} 