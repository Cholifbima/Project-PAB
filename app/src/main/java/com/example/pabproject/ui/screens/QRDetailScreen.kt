package com.example.pabproject.ui.screens

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pabproject.LocalHistoryManager
import com.example.pabproject.ui.components.BottomNavigationBar
import com.example.pabproject.ui.components.QRCodeDisplay
import com.example.pabproject.utils.GalleryUtils
import kotlinx.coroutines.launch

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
    
    var generatedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val historyItem = remember(historyItemId) {
        historyManager.getHistoryItem(historyItemId)
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
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            BottomNavigationBar(navController, currentRoute)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // History Item Info
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Type: ${historyItem.type}",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Created: ${historyItem.timestamp}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Content:",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                    Text(
                        text = historyItem.content,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
            
            // QR Code Display
            QRCodeDisplay(
                text = historyItem.content,
                modifier = Modifier.size(250.dp),
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
                Button(
                    onClick = {
                        generatedBitmap?.let { bitmap ->
                            scope.launch {
                                val success = GalleryUtils.saveImageToGallery(
                                    context = context,
                                    bitmap = bitmap,
                                    filename = "${historyItem.type}_${historyItem.id}"
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
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B5CF6)
                    ),
                    enabled = generatedBitmap != null
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Save",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Save", color = Color.White, fontWeight = FontWeight.Medium)
                }
                
                // Share Button (future implementation)
                OutlinedButton(
                    onClick = {
                        Toast.makeText(context, "Share feature coming soon!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    enabled = generatedBitmap != null
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Share",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share", fontWeight = FontWeight.Medium)
                }
            }
        }
    }
} 