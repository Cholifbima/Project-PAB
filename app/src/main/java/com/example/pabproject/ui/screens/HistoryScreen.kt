package com.example.pabproject.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.graphicsLayer
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
    val historyItems = historyManager.historyItems
    val scope = rememberCoroutineScope()
    
    var searchQuery by remember { mutableStateOf(TextFieldValue("")) }
    var selectedFilter by remember { mutableStateOf(QRCodeType.ALL) }
    var isSearchActive by remember { mutableStateOf(false) }
    
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
                                val snackbarResult = historyManager.clearHistory()
                                // You could add snackbar functionality here
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
                        QRCodeType.values().take(4).forEachIndexed { index, type ->
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
            BottomNavigationBar(navController, currentRoute)
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
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(RoundedCornerShape(50.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (searchQuery.text.isNotBlank() || selectedFilter != QRCodeType.ALL) 
                            Icons.Default.SearchOff else Icons.Default.History,
                        contentDescription = "No History",
                        modifier = Modifier.size(60.dp),
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                    )
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Text(
                    text = if (searchQuery.text.isNotBlank() || selectedFilter != QRCodeType.ALL) 
                        "No matching results" else "No QR Code History",
                    fontFamily = AbrilFatface,
                    fontWeight = FontWeight.Normal,
                    fontSize = 24.sp,
                    letterSpacing = 0.25.sp,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = if (searchQuery.text.isNotBlank() || selectedFilter != QRCodeType.ALL) 
                        "Try a different search term or filter" else 
                        "Generated and scanned QR codes will appear here",
                    fontFamily = Nunito,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    letterSpacing = 0.25.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center
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
                        val dismissState = rememberSwipeToDismissBoxState()
                        val currentItem by rememberUpdatedState(item)
                        
                        if (dismissState.currentValue != SwipeToDismissBoxValue.Settled) {
                            LaunchedEffect(Unit) {
                                // Remove item when swiped
                                historyManager.removeHistoryItem(currentItem.id)
                                dismissState.reset()
                            }
                        }
                        
                        SwipeToDismissBox(
                            state = dismissState,
                            backgroundContent = {
                                val color = MaterialTheme.colorScheme.error
                                val scale by animateFloatAsState(
                                    if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) 1f else 0.75f
                                )
                                
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 20.dp, vertical = 6.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(color),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete",
                                        modifier = Modifier
                                            .scale(scale)
                                            .padding(end = 16.dp),
                                        tint = Color.White
                                    )
                                }
                            },
                            content = {
                                HistoryItemCard(
                                    item = item,
                                    onClick = {
                                        navController.navigate("qr_detail/${item.id}")
                                    }
                                )
                            },
                            enableDismissFromStartToEnd = false,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
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
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val yesterday = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(
        Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }.time
    )
    
    val displayDate = when(date) {
        today -> "Today"
        yesterday -> "Yesterday"
        else -> {
            // Format the date in a more readable format
            try {
                val parsedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(date)
                SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()).format(parsedDate)
            } catch (e: Exception) {
                date
            }
        }
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Divider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant
        )
        
        Text(
            text = displayDate,
            fontFamily = Nunito,
            fontWeight = FontWeight.SemiBold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        
        Divider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}

@Composable
fun HistoryItemCard(
    item: HistoryItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                    text = SimpleDateFormat("HH:mm", Locale.getDefault())
                        .format(SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        .parse(item.timestamp) ?: Date()),
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