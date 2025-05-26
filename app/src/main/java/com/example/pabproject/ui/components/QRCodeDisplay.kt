package com.example.pabproject.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.example.pabproject.utils.QRCodeGenerator

@Composable
fun QRCodeDisplay(
    text: String,
    modifier: Modifier = Modifier,
    onQRCodeGenerated: (Bitmap?) -> Unit = {}
) {
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    
    LaunchedEffect(text) {
        if (text.isNotBlank()) {
            val bitmap = QRCodeGenerator.generateQRCode(text)
            qrBitmap = bitmap
            onQRCodeGenerated(bitmap)
        }
    }
    
    Box(
        modifier = modifier
            .size(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        qrBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Generated QR Code",
                modifier = Modifier
                    .size(180.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit
            )
        } ?: run {
            if (text.isBlank()) {
                Text(
                    text = "Enter text to generate QR code",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            } else {
                CircularProgressIndicator()
            }
        }
    }
} 