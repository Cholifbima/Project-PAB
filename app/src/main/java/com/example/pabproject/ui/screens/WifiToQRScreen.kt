package com.example.pabproject.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
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
fun WifiToQRScreen(navController: NavController) {
    var ssid by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isHidden by remember { mutableStateOf(false) }
    var encryptionType by remember { mutableStateOf("WPA") }
    var isGenerating by remember { mutableStateOf(false) }
    var isSuccessVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val historyManager = LocalHistoryManager.current
    val currentRoute = navController.currentDestination?.route
    
    val encryptionOptions = listOf("none", "WPA", "WEP")
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "WiFi QR Code",
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
                text = "Generate WiFi QR Code",
                fontFamily = AbrilFatface,
                fontWeight = FontWeight.Normal,
                fontSize = 24.sp,
                letterSpacing = 0.25.sp,
                color = MaterialTheme.colorScheme.primary
            )
            
            // Form container
            FormContainer(title = "Enter WiFi Information") {
                Text(
                    text = "Network Name (SSID) (Required)",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                QRTextField(
                    value = ssid,
                    onValueChange = { 
                        ssid = it 
                        isSuccessVisible = false
                    },
                    placeholder = "Enter WiFi network name",
                    keyboardType = KeyboardType.Text
                )
                
                // Hidden SSID checkbox
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isHidden,
                        onCheckedChange = { 
                            isHidden = it
                            isSuccessVisible = false
                        }
                    )
                    Text(
                        text = "Hidden network",
                        fontFamily = Nunito,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Encryption type selection
                Text(
                    text = "Encryption",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                // Dropdown for encryption type
                var expanded by remember { mutableStateOf(false) }
                
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = encryptionType,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )
                    
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        encryptionOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(text = option) },
                                onClick = {
                                    encryptionType = option
                                    expanded = false
                                    isSuccessVisible = false
                                }
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "Password",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                
                QRTextField(
                    value = password,
                    onValueChange = { 
                        password = it 
                        isSuccessVisible = false
                    },
                    placeholder = "Enter WiFi password",
                    keyboardType = KeyboardType.Password
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                QRActionButton(
                    text = "Generate QR Code",
                    icon = Icons.Default.QrCode,
                    onClick = {
                        if (ssid.isBlank()) {
                            Toast.makeText(context, "Please enter a network name (SSID)", Toast.LENGTH_SHORT).show()
                            return@QRActionButton
                        }
                        
                        if (encryptionType != "none" && password.isBlank()) {
                            Toast.makeText(context, "Please enter a password for the selected encryption type", Toast.LENGTH_SHORT).show()
                            return@QRActionButton
                        }
                        
                        isGenerating = true
                        
                        // Generate WiFi QR code
                        val wifiQrContent = QRCodeGenerator.generateWifiQRCode(
                            ssid = ssid,
                            password = password,
                            encryptionType = encryptionType,
                            isHidden = isHidden
                        )
                        
                        // Add to history
                        historyManager.addHistoryItem("WiFi QR", wifiQrContent)
                        
                        // Show success message
                        isSuccessVisible = true
                        isGenerating = false
                    },
                    enabled = ssid.isNotBlank() && !isGenerating && 
                             (encryptionType == "none" || password.isNotBlank()),
                    modifier = Modifier.fillMaxWidth(),
                    isPrimary = true
                )
            }
            
            // Success message
            SuccessMessage(
                message = "WiFi QR Code generated and saved to history!",
                isVisible = isSuccessVisible
            )
            
            // QR Code Preview
            if (ssid.isNotBlank() && (encryptionType == "none" || password.isNotBlank())) {
                val wifiQrContent = QRCodeGenerator.generateWifiQRCode(
                    ssid = ssid,
                    password = password,
                    encryptionType = encryptionType,
                    isHidden = isHidden
                )
                
                QRCodeDisplay(
                    text = wifiQrContent,
                    modifier = Modifier.size(220.dp)
                )
            }
            
            // Bottom spacing for scrolling
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
} 