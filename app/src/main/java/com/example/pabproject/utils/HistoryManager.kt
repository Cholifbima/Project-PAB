package com.example.pabproject.utils

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.*

data class HistoryItem(
    val id: String = UUID.randomUUID().toString(),
    val type: String,
    val content: String,
    val timestamp: String = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
)

class HistoryManager : ViewModel() {
    private val _historyItems = mutableStateListOf<HistoryItem>()
    val historyItems: List<HistoryItem> = _historyItems
    
    fun addHistoryItem(type: String, content: String) {
        val newItem = HistoryItem(
            type = type,
            content = content
        )
        _historyItems.add(0, newItem) // Add to beginning of list
        
        // Keep only last 50 items
        if (_historyItems.size > 50) {
            _historyItems.removeAt(_historyItems.size - 1)
        }
    }
    
    fun getHistoryItem(id: String): HistoryItem? {
        return _historyItems.find { it.id == id }
    }
    
    fun clearHistory() {
        _historyItems.clear()
    }
    
    init {
        // Add sample data for testing
        addHistoryItem("Text QR", "Hello World!")
        addHistoryItem("URL QR", "https://www.google.com")
        addHistoryItem("Email QR", "test@example.com")
        addHistoryItem("Scanned", "https://www.figma.com")
    }
} 