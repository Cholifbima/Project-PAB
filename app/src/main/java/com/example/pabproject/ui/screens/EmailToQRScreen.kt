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
fun EmailToQRScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var body by remember { mutableStateOf("") }
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
                        "Email to QR Code",
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
                text = "Generate Email QR Code",
                fontFamily = AbrilFatface,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                letterSpacing = 0.25.sp,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Form container
            FormContainer(title = "Enter Email Information") {
                Text(
                    text = "Email Address (Required)",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                QRTextField(
                    value = email,
                    onValueChange = { 
                        email = it 
                        isSuccessVisible = false
                    },
                    placeholder = "example@email.com",
                    keyboardType = KeyboardType.Email
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Subject (Optional)",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                QRTextField(
                    value = subject,
                    onValueChange = { 
                        subject = it 
                        isSuccessVisible = false
                    },
                    placeholder = "Email subject",
                    keyboardType = KeyboardType.Text
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Message Body (Optional)",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                QRTextField(
                    value = body,
                    onValueChange = { 
                        body = it 
                        isSuccessVisible = false
                    },
                    placeholder = "Email message body",
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
                        if (email.isBlank()) {
                            Toast.makeText(context, "Please enter an email address", Toast.LENGTH_SHORT).show()
                            return@QRActionButton
                        }
                        
                        // Basic email validation
                        if (!email.contains("@") || !email.contains(".")) {
                            Toast.makeText(context, "Please enter a valid email address", Toast.LENGTH_SHORT).show()
                            return@QRActionButton
                        }
                        
                        isGenerating = true
                        
                        // Generate email QR code
                        val emailQrContent = QRCodeGenerator.generateEmailQRCode(
                            email = email,
                            subject = subject.takeIf { it.isNotBlank() },
                            body = body.takeIf { it.isNotBlank() }
                        )
                        
                        // Add to history
                        historyManager.addHistoryItem("Email QR", emailQrContent)
                        
                        // Show success message
                        isSuccessVisible = true
                        isGenerating = false
                    },
                    enabled = email.isNotBlank() && !isGenerating,
                    modifier = Modifier.fillMaxWidth(),
                    isPrimary = true
                )
            }
            
            // Success message
            SuccessMessage(
                message = "Email QR Code generated and saved to history!",
                isVisible = isSuccessVisible
            )
            
            // QR Code Preview
            if (email.isNotBlank()) {
                val emailQrContent = QRCodeGenerator.generateEmailQRCode(
                    email = email,
                    subject = subject.takeIf { it.isNotBlank() },
                    body = body.takeIf { it.isNotBlank() }
                )
                
                QRCodeDisplay(
                    text = emailQrContent,
                    modifier = Modifier.size(220.dp)
                )
            }
            
            // Bottom spacing for scrolling
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
} 