package com.example.pabproject.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pabproject.ui.theme.FiraCode
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
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(
                2.dp, 
                MaterialTheme.colorScheme.primary.copy(alpha = 0.5f), 
                RoundedCornerShape(16.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        qrBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Generated QR Code",
                modifier = Modifier
                    .size(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .border(
                        1.dp, 
                        MaterialTheme.colorScheme.tertiary.copy(alpha = 0.3f), 
                        RoundedCornerShape(12.dp)
                    ),
                contentScale = ContentScale.Fit
            )
        } ?: run {
            if (text.isBlank()) {
                Text(
                    text = "Enter text to generate QR code",
                    fontFamily = FiraCode,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.sp,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                    modifier = Modifier.size(48.dp),
                    strokeWidth = 4.dp
                )
            }
        }
    }
} 