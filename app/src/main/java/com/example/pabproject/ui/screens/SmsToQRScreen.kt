package com.example.pabproject.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pabproject.LocalHistoryManager
import com.example.pabproject.ui.components.*
import com.example.pabproject.ui.theme.AbrilFatface
import com.example.pabproject.ui.theme.Nunito
import com.example.pabproject.ui.theme.PlayfairDisplay
import com.example.pabproject.utils.QRCodeGenerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmsToQRScreen(navController: NavController) {
    var phoneNumber by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }
    var isSuccessVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val historyManager = LocalHistoryManager.current
    val currentRoute = navController.currentDestination?.route
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "SMS QR Code",
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
                text = "Generate SMS QR Code",
                fontFamily = AbrilFatface,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                letterSpacing = 0.25.sp,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Form container
            FormContainer(title = "Enter SMS Information") {
                Text(
                    text = "Phone Number (Required)",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                QRTextField(
                    value = phoneNumber,
                    onValueChange = { 
                        phoneNumber = it 
                        isSuccessVisible = false
                    },
                    placeholder = "Enter phone number",
                    keyboardType = KeyboardType.Phone
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Message (Optional)",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                QRTextField(
                    value = message,
                    onValueChange = { 
                        message = it 
                        isSuccessVisible = false
                    },
                    placeholder = "Enter message text",
                    keyboardType = KeyboardType.Text,
                    singleLine = false,
                    minLines = 3,
                    maxLines = 5
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                QRActionButton(
                    text = "Generate QR Code",
                    icon = Icons.Default.QrCode,
                    onClick = {
                        if (phoneNumber.isBlank()) {
                            Toast.makeText(context, "Please enter a phone number", Toast.LENGTH_SHORT).show()
                            return@QRActionButton
                        }
                        
                        isGenerating = true
                        
                        // Generate SMS QR code
                        val smsQrContent = QRCodeGenerator.generateSmsQRCode(
                            phoneNumber = phoneNumber,
                            message = message.takeIf { it.isNotBlank() }
                        )
                        
                        // Add to history
                        historyManager.addHistoryItem("SMS QR", smsQrContent)
                        
                        // Show success message
                        isSuccessVisible = true
                        isGenerating = false
                    },
                    enabled = phoneNumber.isNotBlank() && !isGenerating,
                    modifier = Modifier.fillMaxWidth(),
                    isPrimary = true
                )
            }
            
            // Success message
            SuccessMessage(
                message = "SMS QR Code generated and saved to history!",
                isVisible = isSuccessVisible
            )
            
            // QR Code Preview
            if (phoneNumber.isNotBlank()) {
                val smsQrContent = QRCodeGenerator.generateSmsQRCode(
                    phoneNumber = phoneNumber,
                    message = message.takeIf { it.isNotBlank() }
                )
                
                QRCodeDisplay(
                    text = smsQrContent,
                    modifier = Modifier.size(220.dp)
                )
                
                // Info card about scanning functionality
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.7f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Information",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Text(
                            text = "When this QR code is scanned, it will automatically open the SMS app with the phone number and message pre-filled.",
                            fontFamily = Nunito,
                            fontSize = 14.sp,
                            lineHeight = 20.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
            
            // Bottom spacing for scrolling
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
} 