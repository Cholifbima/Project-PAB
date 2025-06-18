package com.example.pabproject.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.pabproject.utils.HistoryItem

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey
    val id: String,
    val type: String,
    val content: String,
    val timestamp: String,
    val qrColor: Int,
    val backgroundColor: Int
) {
    fun toHistoryItem(icon: ImageVector, iconTint: Color): HistoryItem {
        return HistoryItem(
            id = id,
            type = type,
            content = content,
            timestamp = timestamp,
            icon = icon,
            iconTint = iconTint,
            qrColor = qrColor,
            backgroundColor = backgroundColor
        )
    }

    companion object {
        fun fromHistoryItem(item: HistoryItem): HistoryEntity {
            return HistoryEntity(
                id = item.id,
                type = item.type,
                content = item.content,
                timestamp = item.timestamp,
                qrColor = item.qrColor,
                backgroundColor = item.backgroundColor
            )
        }
    }
} 