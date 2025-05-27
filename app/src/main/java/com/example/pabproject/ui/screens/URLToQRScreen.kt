package com.example.pabproject.ui.screens

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pabproject.LocalHistoryManager
import com.example.pabproject.ui.components.*
import com.example.pabproject.ui.theme.AbrilFatface
import com.example.pabproject.ui.theme.Nunito
import com.example.pabproject.ui.theme.PlayfairDisplay
import com.example.pabproject.utils.GalleryUtils
import com.example.pabproject.utils.QRCodeGenerator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun URLToQRScreen(navController: NavController) {
    var url by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }
    var isSuccessVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val historyManager = LocalHistoryManager.current
    val scope = rememberCoroutineScope()
    val currentRoute = navController.currentDestination?.route
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "URL to QR Code",
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
            Text(
                text = "Generate URL QR Code",
                fontFamily = AbrilFatface,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                letterSpacing = 0.25.sp,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Form container
            FormContainer(title = "Enter Website URL") {
                QRTextField(
                    value = url,
                    onValueChange = { 
                        url = it 
                        isSuccessVisible = false
                    },
                    placeholder = "e.g., google.com or https://example.com",
                    keyboardType = KeyboardType.Uri
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "The 'https://' will be added automatically if missing",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Normal,
                    fontSize = 13.sp,
                    letterSpacing = 0.4.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                QRActionButton(
                    text = "Generate QR Code",
                    icon = Icons.Default.QrCode,
                    onClick = {
                        if (url.isBlank()) {
                            Toast.makeText(context, "Please enter a URL", Toast.LENGTH_SHORT).show()
                            return@QRActionButton
                        }
                        
                        isGenerating = true
                        
                        // Process URL to ensure it has https:// prefix
                        val processedUrl = QRCodeGenerator.generateUrlQRCode(url)
                        
                        // Add to history
                        historyManager.addHistoryItem("URL QR", processedUrl)
                        
                        // Show success message
                        isSuccessVisible = true
                        isGenerating = false
                    },
                    enabled = url.isNotBlank() && !isGenerating,
                    modifier = Modifier.fillMaxWidth(),
                    isPrimary = true
                )
            }
            
            // Success message
            SuccessMessage(
                message = "URL QR Code generated and saved to history!",
                isVisible = isSuccessVisible
            )
            
            // QR Code Preview
            if (url.isNotBlank()) {
                val processedUrl = QRCodeGenerator.generateUrlQRCode(url)
                QRCodeDisplay(
                    text = processedUrl,
                    modifier = Modifier.size(220.dp)
                )
            }
            
            // Bottom spacing for scrolling
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
} 