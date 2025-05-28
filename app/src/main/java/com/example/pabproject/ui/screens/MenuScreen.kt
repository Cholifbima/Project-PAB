package com.example.pabproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pabproject.LocalThemeManager
import com.example.pabproject.navigation.Screen
import com.example.pabproject.ui.components.BottomNavigationBar
import com.example.pabproject.ui.components.QRActionButton
import com.example.pabproject.ui.theme.AbrilFatface
import com.example.pabproject.ui.theme.Nunito
import com.example.pabproject.ui.theme.PlayfairDisplay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(navController: NavController) {
    val currentRoute = navController.currentDestination?.route
    val scrollState = rememberScrollState()
    val themeManager = LocalThemeManager.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "QR Code Options",
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
                actions = {
                    IconButton(onClick = { themeManager.toggleDarkMode() }) {
                        Icon(
                            imageVector = if (themeManager.isDarkMode) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (themeManager.isDarkMode) "Switch to Light Mode" else "Switch to Dark Mode",
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Choose QR Code Type",
                fontFamily = AbrilFatface,
                fontWeight = FontWeight.Normal,
                fontSize = 26.sp,
                letterSpacing = 0.25.sp,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 16.dp)
            )
            
            QRActionButton(
                text = "URL QR Code",
                icon = Icons.Default.Link,
                onClick = {
                    navController.navigate(Screen.URLToQR.route)
                },
                modifier = Modifier.fillMaxWidth(),
                isPrimary = true
            )
            
            QRActionButton(
                text = "Text QR Code",
                icon = Icons.Default.TextFields,
                onClick = {
                    navController.navigate(Screen.TextToQR.route)
                },
                modifier = Modifier.fillMaxWidth(),
                isPrimary = false
            )
            
            QRActionButton(
                text = "Email QR Code",
                icon = Icons.Default.Email,
                onClick = {
                    navController.navigate(Screen.EmailToQR.route)
                },
                modifier = Modifier.fillMaxWidth(),
                isPrimary = true
            )
            
            QRActionButton(
                text = "SMS QR Code",
                icon = Icons.Default.Sms,
                onClick = {
                    navController.navigate(Screen.SmsToQR.route)
                },
                modifier = Modifier.fillMaxWidth(),
                isPrimary = false
            )
            
            QRActionButton(
                text = "Twitter QR Code",
                icon = Icons.Default.Tag,
                onClick = {
                    navController.navigate(Screen.TwitterToQR.route)
                },
                modifier = Modifier.fillMaxWidth(),
                isPrimary = true
            )
            
            QRActionButton(
                text = "WiFi QR Code",
                icon = Icons.Default.Wifi,
                onClick = {
                    navController.navigate(Screen.WifiToQR.route)
                },
                modifier = Modifier.fillMaxWidth(),
                isPrimary = false
            )
            
            QRActionButton(
                text = "Scan QR Code",
                icon = Icons.Default.QrCodeScanner,
                onClick = {
                    navController.navigate(Screen.QRScanner.route)
                },
                modifier = Modifier.fillMaxWidth(),
                isPrimary = true
            )
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "About QR Codes",
                        fontFamily = PlayfairDisplay,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        letterSpacing = 0.15.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "QR codes (Quick Response codes) are versatile 2D barcodes that can store various types of information. They can be scanned with any smartphone camera.",
                        fontFamily = Nunito,
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        letterSpacing = 0.25.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.9f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun MenuOption(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(90.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp)),
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Go",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
} 