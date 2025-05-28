package com.example.pabproject.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import java.util.Hashtable
import androidx.compose.ui.graphics.toArgb
import com.example.pabproject.ui.theme.Brown
import com.example.pabproject.ui.theme.PaleYellow
import com.example.pabproject.ui.theme.Black
import com.example.pabproject.ui.theme.White
import java.net.URLEncoder

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
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) darkColor.toArgb() else lightColor.toArgb())
                }
            }
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun generateEmailQRCode(email: String, subject: String? = null, body: String? = null): String {
        val emailString = buildString {
            append("mailto:$email")
            val params = mutableListOf<String>()
            subject?.let { params.add("subject=$it") }
            body?.let { params.add("body=$it") }
            if (params.isNotEmpty()) {
                append("?")
                append(params.joinToString("&"))
            }
        }
        return emailString
    }
    
    fun generateUrlQRCode(url: String): String {
        return if (url.startsWith("http://") || url.startsWith("https://")) {
            url
        } else {
            "https://$url"
        }
    }
    
    fun generateSmsQRCode(phoneNumber: String, message: String? = null): String {
        // Clean the phone number by removing spaces, dashes, etc.
        val cleanNumber = phoneNumber.replace(Regex("[^+0-9]"), "")
        
        return if (message.isNullOrBlank()) {
            // If no message, use simple SMS URI
            "smsto:$cleanNumber"
        } else {
            // Use SMSTO format which doesn't require encoding and preserves spaces
            "smsto:$cleanNumber:$message"
        }
    }
    
    fun generateTwitterQRCode(username: String, isProfile: Boolean = true): String {
        return if (isProfile) {
            val formattedUsername = username.trimStart('@')
            "https://twitter.com/$formattedUsername"
        } else {
            val tweetContent = URLEncoder.encode(username, "UTF-8")
            "https://twitter.com/intent/tweet?text=$tweetContent"
        }
    }
    
    fun generateWifiQRCode(ssid: String, password: String, encryptionType: String, isHidden: Boolean = false): String {
        return buildString {
            append("WIFI:")
            append("S:$ssid;")
            if (password.isNotBlank() && encryptionType != "none") {
                append("P:$password;")
            }
            append("T:$encryptionType;")
            if (isHidden) {
                append("H:true;")
            }
            append(";")
        }
    }
} 