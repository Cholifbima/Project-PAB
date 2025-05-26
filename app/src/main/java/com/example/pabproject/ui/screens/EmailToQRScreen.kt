package com.example.pabproject.ui.screens

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pabproject.LocalHistoryManager
import com.example.pabproject.ui.components.BottomNavigationBar
import com.example.pabproject.ui.components.QRCodeDisplay
import com.example.pabproject.utils.GalleryUtils
import com.example.pabproject.utils.QRCodeGenerator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmailToQRScreen(navController: NavController) {
    val context = LocalContext.current
    val historyManager = LocalHistoryManager.current
    val scope = rememberCoroutineScope()
    
    var inputEmail by remember { mutableStateOf("") }
    var inputSubject by remember { mutableStateOf("") }
    var inputBody by remember { mutableStateOf("") }
    var generatedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val currentRoute = navController.currentDestination?.route
    
    val qrText = remember(inputEmail, inputSubject, inputBody) {
        if (inputEmail.isNotBlank()) {
            QRCodeGenerator.generateEmailQRCode(
                email = inputEmail,
                subject = inputSubject.takeIf { it.isNotBlank() },
                body = inputBody.takeIf { it.isNotBlank() }
            )
        } else {
            ""
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "Email to QR Code",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Email Input Field
            Text(
                text = "Masukan teks",
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = inputEmail,
                onValueChange = { inputEmail = it },
                placeholder = { Text("Masukan teks disini") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            
            // Subject Input Field (Optional)
            OutlinedTextField(
                value = inputSubject,
                onValueChange = { inputSubject = it },
                placeholder = { Text("Subject (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            
            // Body Input Field (Optional)
            OutlinedTextField(
                value = inputBody,
                onValueChange = { inputBody = it },
                placeholder = { Text("Message (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 3,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )
            
            // QR Code Display
            QRCodeDisplay(
                text = qrText,
                modifier = Modifier.size(200.dp),
                onQRCodeGenerated = { bitmap ->
                    generatedBitmap = bitmap
                }
            )
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Generate & Save to History Button
                Button(
                    onClick = { 
                        if (inputEmail.isNotBlank()) {
                            historyManager.addHistoryItem("Email QR", qrText)
                            Toast.makeText(context, "QR Code saved to history!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF8B5CF6)
                    ),
                    enabled = inputEmail.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = "Generate",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Generate",
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }
                
                // Save to Gallery Button
                Button(
                    onClick = {
                        generatedBitmap?.let { bitmap ->
                            scope.launch {
                                val success = GalleryUtils.saveImageToGallery(
                                    context = context,
                                    bitmap = bitmap,
                                    filename = "EmailQR_${System.currentTimeMillis()}"
                                )
                                Toast.makeText(
                                    context,
                                    if (success) "QR Code saved to gallery!" else "Failed to save QR Code",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    enabled = generatedBitmap != null
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Save",
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }
            }
            
            if (generatedBitmap != null) {
                Text(
                    text = "QR Code berhasil dibuat!",
                    color = Color(0xFF4CAF50),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
} 