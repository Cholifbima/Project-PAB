package com.example.pabproject.utils

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.util.Hashtable
import androidx.compose.ui.graphics.toArgb
import com.example.pabproject.ui.theme.Brown
import com.example.pabproject.ui.theme.PaleYellow
import androidx.core.graphics.createBitmap
import androidx.core.graphics.set

object QRCodeGenerator {
    
    fun generateQRCode(
        text: String,
        width: Int = 512,
        height: Int = 512,
        darkColor: androidx.compose.ui.graphics.Color = Brown,
        lightColor: androidx.compose.ui.graphics.Color = PaleYellow
    ): Bitmap? {
        return try {
            val writer = QRCodeWriter()
            val hints = Hashtable<EncodeHintType, Any>()
            hints[EncodeHintType.CHARACTER_SET] = "UTF-8"
            hints[EncodeHintType.MARGIN] = 1
            
            val bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE, width, height, hints)
            val bitmap = createBitmap(width, height)
            
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap[x, y] = if (bitMatrix[x, y]) darkColor.toArgb() else lightColor.toArgb()
                }
            }
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
} 