package com.example.pabproject.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.pabproject.ui.components.BottomNavigationBar
import com.example.pabproject.LocalHistoryManager
import com.example.pabproject.ui.theme.*
import com.example.pabproject.utils.HistoryItem
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

enum class QRCodeType(val displayName: String, val icon: ImageVector, val color: Color) {
    ALL("All", Icons.Default.GridView, Brown),
    EMAIL("Email", Icons.Default.Email, Gold),
    URL("URL", Icons.Default.Link, LightBrown),
    TEXT("Text", Icons.Default.TextFields, SandyBeige),
    SCANNED("Scanned", Icons.Default.QrCodeScanner, Brown),
    SMS("SMS", Icons.Default.Sms, LightPeach),
    TWITTER("Twitter", Icons.Default.Tag, Brown),
    WIFI("WiFi", Icons.Default.Wifi, LightBrown)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(navController: NavController) {
    val historyManager = LocalHistoryManager.current
    val currentRoute = navController.currentDestination?.route
    val scope = rememberCoroutineScope()
    var isSearchActive by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var selectedFilter by remember { mutableStateOf<QRCodeType>(QRCodeType.ALL) }
    
    // Collect history items from StateFlow
    val historyItems by historyManager.historyItems.collectAsState()
    
    // Group and filter items
    val filteredAndGroupedItems = remember(historyItems, searchQuery.text, selectedFilter) {
        // First apply filters
        val filtered = historyItems.filter { item ->
            // Filter by type if not ALL
            val typeMatches = if (selectedFilter == QRCodeType.ALL) {
                true
            } else {
                item.type.contains(selectedFilter.displayName, ignoreCase = true)
            }
            
            // Filter by search query
            val searchMatches = if (searchQuery.text.isBlank()) {
                true
            } else {
                item.content.contains(searchQuery.text, ignoreCase = true) ||
                item.type.contains(searchQuery.text, ignoreCase = true)
            }
            
            typeMatches && searchMatches
        }
        
        // Then group by date
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val grouped = filtered.groupBy { item ->
            val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).parse(item.timestamp)
            dateFormat.format(timestamp)
        }
        
        // Convert to sorted list of groups
        grouped.entries.sortedByDescending { it.key }.map { entry ->
            val date = entry.key
            val items = entry.value
            Pair(date, items)
        }
    }

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { 
                        if (!isSearchActive) {
                            Text(
                                "History",
                                fontFamily = PlayfairDisplay,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.primary
                            ) 
                        } else {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Search history...") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true,
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Color.Transparent,
                                    unfocusedBorderColor = Color.Transparent
                                )
                            )
                        }
                    },
                    navigationIcon = {
                        if (!isSearchActive) {
                            IconButton(onClick = { navController.popBackStack() }) {
                                Icon(
                                    Icons.AutoMirrored.Filled.ArrowBack, 
                                    contentDescription = "Back",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else {
                            IconButton(onClick = { 
                                isSearchActive = false
                                searchQuery = TextFieldValue("")
                            }) {
                                Icon(
                                    Icons.Default.Close, 
                                    contentDescription = "Cancel search",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    },
                    actions = {
                        if (!isSearchActive) {
                            IconButton(onClick = { isSearchActive = true }) {
                                Icon(
                                    Icons.Default.Search, 
                                    contentDescription = "Search",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                        
                        IconButton(onClick = {
                            scope.launch {
                                historyManager.clearHistory()
                            }
                        }) {
                            Icon(
                                Icons.Default.DeleteSweep, 
                                contentDescription = "Clear history",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)
                    )
                )
                
                // Filter chips
                AnimatedVisibility(visible = !isSearchActive) {
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        QRCodeType.entries.toTypedArray().take(4).forEachIndexed { index, type ->
                            SegmentedButton(
                                selected = selectedFilter == type,
                                onClick = { selectedFilter = type },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = 4
                                ),
                                colors = SegmentedButtonDefaults.colors(
                                    activeContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    activeBorderColor = MaterialTheme.colorScheme.primary,
                                    activeContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            ) {
                                Icon(
                                    imageVector = type.icon, 
                                    contentDescription = type.displayName
                                )
                            }
                        }
                    }
                }
            }
        },
        bottomBar = {
            BottomNavigationBar(navController = navController, currentRoute = currentRoute)
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        if (filteredAndGroupedItems.isEmpty()) {
            // Empty state
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = if (searchQuery.text.isNotBlank() || selectedFilter != QRCodeType.ALL) 
                        Icons.Default.SearchOff else Icons.Default.History,
                    contentDescription = "No History",
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = if (searchQuery.text.isNotBlank() || selectedFilter != QRCodeType.ALL) 
                        "No matching results" else "No QR Code History",
                    fontFamily = AbrilFatface,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = if (searchQuery.text.isNotBlank() || selectedFilter != QRCodeType.ALL) 
                        "Try a different search term or filter" else 
                        "Generated and scanned QR codes will appear here",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            // History list with date grouping
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                item {
                    Text(
                        text = "Your QR History",
                        fontFamily = AbrilFatface,
                        fontWeight = FontWeight.Normal,
                        fontSize = 24.sp,
                        letterSpacing = 0.25.sp,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                    )
                }
                
                // Grouped items
                filteredAndGroupedItems.forEach { (date, items) ->
                    // Date header
                    item {
                        DateHeader(date)
                    }
                    
                    // Items for this date
                    itemsIndexed(items) { index, item ->
                        HistoryItemCard(
                            item = item,
                            onItemClick = {
                                navController.navigate("qr_detail/${item.id}")
                            },
                            onDeleteClick = {
                                historyManager.removeHistoryItem(item.id)
                            }
                        )
                        
                        // Add divider except after the last item
                        if (index < items.size - 1) {
                            Divider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DateHeader(date: String) {
    Text(
        text = date,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
fun HistoryItemCard(
    item: HistoryItem,
    onItemClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clickable { onItemClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = item.icon,
                contentDescription = null,
                tint = item.iconTint,
                modifier = Modifier.size(32.dp)
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.type,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = item.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Text(
                    text = item.timestamp,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Row {
                IconButton(onClick = onItemClick) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                        contentDescription = "Open",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                IconButton(onClick = onDeleteClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
} 