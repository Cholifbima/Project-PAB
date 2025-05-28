package com.example.pabproject.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import com.example.pabproject.R
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object GalleryUtils {
    private const val NOTIFICATION_CHANNEL_ID = "qr_code_channel"
    private const val NOTIFICATION_ID = 1001
    
    fun saveImageToGallery(context: Context, bitmap: Bitmap, filename: String = "QRCode_${System.currentTimeMillis()}"): Boolean {
        return try {
            val contentResolver = context.contentResolver
            val imageCollection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else {
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }
            
            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, "$filename.png")
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/QRCodes")
                    put(MediaStore.Images.Media.IS_PENDING, 1)
                }
            }
            
            val imageUri: Uri? = contentResolver.insert(imageCollection, contentValues)
            
            imageUri?.let { uri ->
                contentResolver.openOutputStream(uri)?.use { outputStream ->
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                }
                
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    contentValues.clear()
                    contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
                    contentResolver.update(uri, contentValues, null, null)
                }
                
                // Show success message instead of notification to avoid potential issues
                Toast.makeText(context, "QR Code saved as $filename", Toast.LENGTH_SHORT).show()
                true
            } ?: false
            
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
    
    fun saveImageToCache(context: Context, bitmap: Bitmap, filename: String = "temp_qr_${System.currentTimeMillis()}.png"): File? {
        return try {
            val cacheDir = context.cacheDir
            val file = File(cacheDir, filename)
            
            FileOutputStream(file).use { outputStream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            }
            
            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
    
    fun formatFilename(prefix: String): String {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        return "${prefix}_$timestamp"
    }
    
    // This function is currently disabled to avoid compilation issues
    /*
    private fun showSaveNotification(context: Context, filename: String) {
        val notificationManager = ContextCompat.getSystemService(context, NotificationManager::class.java)
        
        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "QR Code Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for QR code generation and scanning"
                enableLights(true)
                lightColor = Color.parseColor("#6F4E37") // Brown color
            }
            notificationManager?.createNotificationChannel(channel)
        }
        
        // Build the notification
        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_qr_code)
            .setContentTitle("QR Code Saved")
            .setContentText("Your QR code has been saved as $filename")
            .setColor(Color.parseColor("#6F4E37"))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        
        // Show the notification
        notificationManager?.notify(NOTIFICATION_ID, notification)
    }
    */
} 