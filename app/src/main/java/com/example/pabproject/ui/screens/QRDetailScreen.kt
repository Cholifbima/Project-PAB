package com.example.pabproject.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.core.content.FileProvider
import com.example.pabproject.LocalHistoryManager
import com.example.pabproject.ui.components.*
import com.example.pabproject.ui.theme.FiraCode
import com.example.pabproject.ui.theme.Nunito
import com.example.pabproject.ui.theme.PlayfairDisplay
import com.example.pabproject.utils.GalleryUtils
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QRDetailScreen(
    navController: NavController,
    historyItemId: String
) {
    val context = LocalContext.current
    val historyManager = LocalHistoryManager.current
    val currentRoute = navController.currentDestination?.route
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    
    var generatedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val historyItem = remember(historyItemId) {
        historyManager.getHistoryItem(historyItemId)
    }
    
    // Function to share QR code
    fun shareQRCode(bitmap: Bitmap) {
        try {
            // Create a file in the cache directory
            val cachePath = File(context.cacheDir, "images")
            cachePath.mkdirs()
            val file = File(cachePath, "shared_qr.png")
            
            // Write the bitmap to the file
            FileOutputStream(file).use { stream ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)
            }
            
            // Get the content URI using FileProvider
            val contentUri = FileProvider.getUriForFile(
                context,
                "com.example.pabproject.fileprovider",
                file
            )
            
            // Create the share intent
            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, contentUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            
            // Start the share activity
            context.startActivity(Intent.createChooser(shareIntent, "Share QR Code"))
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Failed to share QR Code: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
    
    if (historyItem == null) {
        // Handle case where history item is not found
        LaunchedEffect(Unit) {
            navController.popBackStack()
        }
        return
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "QR Code Detail",
                        fontFamily = PlayfairDisplay,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack, 
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(navController, currentRoute)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // History Item Info
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(4.dp, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = "Type: ${historyItem.type}",
                        fontFamily = PlayfairDisplay,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        letterSpacing = 0.15.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Created: ${historyItem.timestamp}",
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        letterSpacing = 0.25.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(20.dp))
                    
                    Text(
                        text = "Content:",
                        fontFamily = PlayfairDisplay,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        letterSpacing = 0.15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Text(
                        text = historyItem.content,
                        fontFamily = FiraCode,
                        fontWeight = FontWeight.Normal,
                        fontSize = 14.sp,
                        letterSpacing = 0.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
            
            // QR Code Display
            QRCodeDisplay(
                text = historyItem.content,
                modifier = Modifier.size(220.dp),
                onQRCodeGenerated = { bitmap ->
                    generatedBitmap = bitmap
                }
            )
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Save to Gallery Button
                QRActionButton(
                    text = "Save",
                    icon = Icons.Default.Download,
                    onClick = {
                        generatedBitmap?.let { bitmap ->
                            scope.launch {
                                val filename = GalleryUtils.formatFilename("QR_${historyItem.type}")
                                val success = GalleryUtils.saveImageToGallery(
                                    context = context,
                                    bitmap = bitmap,
                                    filename = filename
                                )
                                Toast.makeText(
                                    context,
                                    if (success) "QR Code saved to gallery!" else "Failed to save QR Code",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = generatedBitmap != null,
                    isPrimary = true
                )
                
                // Share Button
                Button(
                    onClick = {
                        generatedBitmap?.let { bitmap ->
                            shareQRCode(bitmap)
                        } ?: run {
                            Toast.makeText(
                                context,
                                "QR Code not yet generated",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    enabled = generatedBitmap != null
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Share", 
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        letterSpacing = 0.5.sp
                    )
                }
            }
            
            // Bottom spacing for scrolling
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
} 