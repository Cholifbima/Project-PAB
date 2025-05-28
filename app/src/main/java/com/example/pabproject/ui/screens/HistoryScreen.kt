package com.example.pabproject.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pabproject.ui.components.BottomNavigationBar
import com.example.pabproject.LocalHistoryManager
import com.example.pabproject.ui.theme.AbrilFatface
import com.example.pabproject.ui.theme.Inter
import com.example.pabproject.ui.theme.Nunito
import com.example.pabproject.ui.theme.PlayfairDisplay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController) {
    val historyManager = LocalHistoryManager.current
    val currentRoute = navController.currentDestination?.route
    val historyItems = historyManager.historyItems
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        "History",
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
        if (historyItems.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "No History",
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = "No QR Code History",
                    fontFamily = AbrilFatface,
                    fontWeight = FontWeight.Normal,
                    fontSize = 24.sp,
                    letterSpacing = 0.25.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.headlineSmall
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Generated and scanned QR codes will appear here",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    letterSpacing = 0.25.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Text(
                    text = "Your QR History",
                    fontFamily = AbrilFatface,
                    fontWeight = FontWeight.Normal,
                    fontSize = 24.sp,
                    letterSpacing = 0.25.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                )
                
                // History list
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    items(historyItems) { item ->
                        HistoryItemCard(
                            item = item,
                            onClick = {
                                navController.navigate("qr_detail/${item.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryItemCard(
    item: com.example.pabproject.utils.HistoryItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() },
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
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.type,
                    modifier = Modifier.size(24.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.type,
                    fontFamily = PlayfairDisplay,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    letterSpacing = 0.15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = item.content,
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Normal,
                    fontSize = 14.sp,
                    letterSpacing = 0.25.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = item.timestamp,
                    fontFamily = Inter,
                    fontWeight = FontWeight.Light,
                    fontSize = 12.sp,
                    letterSpacing = 0.4.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            IconButton(onClick = onClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                    contentDescription = "Open",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
} 