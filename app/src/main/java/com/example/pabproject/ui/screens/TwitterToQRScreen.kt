package com.example.pabproject.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.pabproject.utils.QRCodeGenerator

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TwitterToQRScreen(navController: NavController) {
    var isProfileOption by remember { mutableStateOf(true) }
    var username by remember { mutableStateOf("") }
    var tweetContent by remember { mutableStateOf("") }
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
                        "Twitter QR Code",
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
                text = "Generate Twitter QR Code",
                fontFamily = AbrilFatface,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                letterSpacing = 0.25.sp,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Form container
            FormContainer(title = "Twitter QR Options") {
                // Option Selection
                Text(
                    text = "Choose an option",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .selectable(
                                selected = isProfileOption,
                                onClick = { 
                                    isProfileOption = true
                                    isSuccessVisible = false
                                }
                            )
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        RadioButton(
                            selected = isProfileOption,
                            onClick = { 
                                isProfileOption = true
                                isSuccessVisible = false
                            }
                        )
                        Text(
                            text = "Link to profile",
                            fontFamily = Nunito,
                            fontSize = 14.sp
                        )
                    }
                    
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .selectable(
                                selected = !isProfileOption,
                                onClick = { 
                                    isProfileOption = false
                                    isSuccessVisible = false
                                }
                            )
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        RadioButton(
                            selected = !isProfileOption,
                            onClick = { 
                                isProfileOption = false
                                isSuccessVisible = false
                            }
                        )
                        Text(
                            text = "Post a tweet",
                            fontFamily = Nunito,
                            fontSize = 14.sp
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                if (isProfileOption) {
                    // Profile option
                    Text(
                        text = "Username (with or without @)",
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    QRTextField(
                        value = username,
                        onValueChange = { 
                            username = it 
                            isSuccessVisible = false
                        },
                        placeholder = "@username",
                        keyboardType = KeyboardType.Text
                    )
                } else {
                    // Tweet option
                    Text(
                        text = "Tweet Text",
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    QRTextField(
                        value = tweetContent,
                        onValueChange = { 
                            tweetContent = it 
                            isSuccessVisible = false
                        },
                        placeholder = "What's happening?",
                        keyboardType = KeyboardType.Text,
                        singleLine = false,
                        minLines = 3,
                        maxLines = 5
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                QRActionButton(
                    text = "Generate QR Code",
                    icon = Icons.Default.QrCode,
                    onClick = {
                        if (isProfileOption && username.isBlank()) {
                            Toast.makeText(context, "Please enter a username", Toast.LENGTH_SHORT).show()
                            return@QRActionButton
                        }
                        
                        if (!isProfileOption && tweetContent.isBlank()) {
                            Toast.makeText(context, "Please enter tweet content", Toast.LENGTH_SHORT).show()
                            return@QRActionButton
                        }
                        
                        isGenerating = true
                        
                        // Generate Twitter QR code
                        val twitterQrContent = if (isProfileOption) {
                            QRCodeGenerator.generateTwitterQRCode(username, isProfile = true)
                        } else {
                            QRCodeGenerator.generateTwitterQRCode(tweetContent, isProfile = false)
                        }
                        
                        // Add to history
                        val titlePrefix = if (isProfileOption) "Twitter Profile" else "Twitter Tweet"
                        historyManager.addHistoryItem("$titlePrefix QR", twitterQrContent)
                        
                        // Show success message
                        isSuccessVisible = true
                        isGenerating = false
                    },
                    enabled = (isProfileOption && username.isNotBlank()) || 
                             (!isProfileOption && tweetContent.isNotBlank()) && 
                             !isGenerating,
                    modifier = Modifier.fillMaxWidth(),
                    isPrimary = true
                )
            }
            
            // Success message
            SuccessMessage(
                message = "Twitter QR Code generated and saved to history!",
                isVisible = isSuccessVisible
            )
            
            // QR Code Preview
            if ((isProfileOption && username.isNotBlank()) || (!isProfileOption && tweetContent.isNotBlank())) {
                val twitterQrContent = if (isProfileOption) {
                    QRCodeGenerator.generateTwitterQRCode(username, isProfile = true)
                } else {
                    QRCodeGenerator.generateTwitterQRCode(tweetContent, isProfile = false)
                }
                
                QRCodeDisplay(
                    text = twitterQrContent,
                    modifier = Modifier.size(220.dp)
                )
            }
            
            // Bottom spacing for scrolling
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
} 