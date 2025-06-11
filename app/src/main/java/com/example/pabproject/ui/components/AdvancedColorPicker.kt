package com.example.pabproject.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pabproject.ui.theme.PlayfairDisplay
import com.example.pabproject.ui.theme.Nunito
import com.example.pabproject.utils.CustomColorManager
import kotlin.math.*

// Predefined quick colors
val QuickColors = listOf(
    Color(0xFF000000), Color(0xFF2E86AB), Color(0xFF27AE60), Color(0xFFE74C3C),
    Color(0xFFF39C12), Color(0xFF9B59B6), Color(0xFF1DA1F2), Color(0xFF34495E),
    Color(0xFF8E44AD), Color(0xFFE67E22), Color(0xFF16A085), Color(0xFFC0392B)
)

enum class ColorPickerMode {
    QUICK, HSV, RGB
}

@Composable
fun AdvancedColorPicker(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    selectedBackgroundColor: Color = Color.White,
    onBackgroundColorSelected: (Color) -> Unit = {},
    customColorManager: CustomColorManager = remember { CustomColorManager() },
    modifier: Modifier = Modifier
) {
    var pickerMode by remember { mutableStateOf(ColorPickerMode.QUICK) }
    var isUpdatingFromExternal by remember { mutableStateOf(false) }
    var colorTarget by remember { mutableStateOf("qr") } // "qr" or "background"
    
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with mode controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (colorTarget == "qr") "QR Code Color" else "Background Color",
                    fontFamily = PlayfairDisplay,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Row {
                    IconButton(
                        onClick = { 
                            pickerMode = when(pickerMode) {
                                ColorPickerMode.QUICK -> ColorPickerMode.HSV
                                ColorPickerMode.HSV -> ColorPickerMode.RGB
                                ColorPickerMode.RGB -> ColorPickerMode.QUICK
                            }
                        }
                    ) {
                        Icon(
                            if (pickerMode == ColorPickerMode.QUICK) Icons.Default.Palette else Icons.Default.Tune,
                            contentDescription = "Switch Mode",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            
            // Color target selection
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    onClick = { colorTarget = "qr" },
                    label = { Text("QR Color", fontFamily = Nunito, fontSize = 12.sp) },
                    selected = colorTarget == "qr",
                    modifier = Modifier.weight(1f)
                )
                FilterChip(
                    onClick = { colorTarget = "background" },
                    label = { Text("Background", fontFamily = Nunito, fontSize = 12.sp) },
                    selected = colorTarget == "background",
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Color preview with hex input
            ColorPreviewSection(
                selectedColor = if (colorTarget == "qr") selectedColor else selectedBackgroundColor,
                onColorSelected = { color ->
                    isUpdatingFromExternal = true
                    customColorManager.addCustomColor(color)
                    if (colorTarget == "qr") {
                        onColorSelected(color)
                    } else {
                        onBackgroundColorSelected(color)
                    }
                    isUpdatingFromExternal = false
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Mode-specific picker
            when (pickerMode) {
                ColorPickerMode.QUICK -> QuickColorSection(
                    selectedColor = if (colorTarget == "qr") selectedColor else selectedBackgroundColor,
                    customColorManager = customColorManager,
                    onColorSelected = { color ->
                        customColorManager.addCustomColor(color)
                        if (colorTarget == "qr") {
                            onColorSelected(color)
                        } else {
                            onBackgroundColorSelected(color)
                        }
                    }
                )
                ColorPickerMode.HSV -> HSVColorPickerSection(
                    selectedColor = if (colorTarget == "qr") selectedColor else selectedBackgroundColor,
                    onColorSelected = { color ->
                        customColorManager.addCustomColor(color)
                        if (colorTarget == "qr") {
                            onColorSelected(color)
                        } else {
                            onBackgroundColorSelected(color)
                        }
                    },
                    isUpdatingFromExternal = isUpdatingFromExternal
                )
                ColorPickerMode.RGB -> RGBColorPickerSection(
                    selectedColor = if (colorTarget == "qr") selectedColor else selectedBackgroundColor,
                    onColorSelected = { color ->
                        customColorManager.addCustomColor(color)
                        if (colorTarget == "qr") {
                            onColorSelected(color)
                        } else {
                            onBackgroundColorSelected(color)
                        }
                    },
                    isUpdatingFromExternal = isUpdatingFromExternal
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Mode indicator
            Text(
                text = when(pickerMode) {
                    ColorPickerMode.QUICK -> "Quick colors - Tap tune icon for advanced options"
                    ColorPickerMode.HSV -> "HSV Color Picker - Hue, Saturation, Value"
                    ColorPickerMode.RGB -> "RGB Color Picker - Red, Green, Blue"
                },
                fontFamily = Nunito,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun ColorPreviewSection(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit
) {
    var hexInput by remember { mutableStateOf(selectedColor.toHexString()) }
    
    LaunchedEffect(selectedColor) {
        hexInput = selectedColor.toHexString()
    }
    
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Color preview circle
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .background(selectedColor)
                .border(2.dp, MaterialTheme.colorScheme.outline, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = "Selected",
                tint = if (selectedColor.luminance() < 0.5f) Color.White else Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
        
        // Hex input field
        OutlinedTextField(
            value = hexInput,
            onValueChange = { newHex ->
                hexInput = newHex.uppercase()
                if (newHex.length == 7 && newHex.startsWith("#")) {
                    try {
                        val color = Color(android.graphics.Color.parseColor(newHex))
                        onColorSelected(color)
                    } catch (e: Exception) {
                        // Invalid hex color, ignore
                    }
                }
            },
            label = { Text("Hex Code", fontFamily = Nunito, fontSize = 12.sp) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
            modifier = Modifier.width(120.dp),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = Nunito,
                fontSize = 14.sp
            ),
            singleLine = true
        )
    }
}

@Composable
private fun QuickColorSection(
    selectedColor: Color,
    customColorManager: CustomColorManager,
    onColorSelected: (Color) -> Unit
) {
    val recentColors = customColorManager.getRecentColors()
    
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Recent custom colors (if any)
        if (recentColors.isNotEmpty()) {
            Text(
                text = "Recent Colors",
                fontFamily = Nunito,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(recentColors) { color ->
                    ColorPickerItem(
                        color = color,
                        isSelected = selectedColor.isSimilarTo(color),
                        onColorSelected = { onColorSelected(color) },
                        size = 35.dp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
        }
        
        // Predefined quick colors
        Text(
            text = "Template Colors",
            fontFamily = Nunito,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            items(QuickColors) { color ->
                ColorPickerItem(
                    color = color,
                    isSelected = selectedColor.isSimilarTo(color),
                    onColorSelected = { onColorSelected(color) }
                )
            }
        }
    }
}

@Composable
private fun HSVColorPickerSection(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    isUpdatingFromExternal: Boolean
) {
    var hue by remember { mutableStateOf(0f) }
    var saturation by remember { mutableStateOf(1f) }
    var value by remember { mutableStateOf(1f) }
    var isInternalUpdate by remember { mutableStateOf(false) }
    
    // Update HSV when color changes externally
    LaunchedEffect(selectedColor) {
        if (!isInternalUpdate && !isUpdatingFromExternal) {
            val hsv = selectedColor.toHsv()
            hue = hsv[0].takeIf { !it.isNaN() } ?: 0f
            saturation = hsv[1].coerceIn(0f, 1f)
            value = hsv[2].coerceIn(0.1f, 1f) // Minimum 10% untuk visibilitas
        }
    }
    
    // Update color when HSV changes
    LaunchedEffect(hue, saturation, value) {
        if (!isUpdatingFromExternal) {
            isInternalUpdate = true
            val color = Color.hsv(hue, saturation, value)
            onColorSelected(color)
            isInternalUpdate = false
        }
    }
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Color wheel - independent dari value/brightness
        IndependentColorWheel(
            modifier = Modifier.size(180.dp),
            selectedHue = hue,
            selectedSaturation = saturation,
            onColorChanged = { newHue, newSat ->
                hue = newHue
                saturation = newSat
            }
        )
        
        // Value/Brightness slider
        ValueSlider(
            value = value,
            hue = hue,
            saturation = saturation,
            onValueChanged = { newValue ->
                value = newValue.coerceAtLeast(0.1f) // Minimum 10%
            }
        )
        
        // HSV value display
        HSVValueDisplay(hue, saturation, value)
    }
}

@Composable
private fun RGBColorPickerSection(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    isUpdatingFromExternal: Boolean
) {
    var red by remember { mutableStateOf((selectedColor.red * 255).toInt()) }
    var green by remember { mutableStateOf((selectedColor.green * 255).toInt()) }
    var blue by remember { mutableStateOf((selectedColor.blue * 255).toInt()) }
    var isInternalUpdate by remember { mutableStateOf(false) }
    
    // Update RGB when color changes externally
    LaunchedEffect(selectedColor) {
        if (!isInternalUpdate && !isUpdatingFromExternal) {
            red = (selectedColor.red * 255).toInt()
            green = (selectedColor.green * 255).toInt()
            blue = (selectedColor.blue * 255).toInt()
        }
    }
    
    // Update color when RGB changes
    LaunchedEffect(red, green, blue) {
        if (!isUpdatingFromExternal) {
            isInternalUpdate = true
            val color = Color(red, green, blue)
            onColorSelected(color)
            isInternalUpdate = false
        }
    }
    
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        ColorSlider(
            label = "R",
            value = red,
            color = Color.Red,
            onValueChange = { red = it }
        )
        
        ColorSlider(
            label = "G",
            value = green,
            color = Color.Green,
            onValueChange = { green = it }
        )
        
        ColorSlider(
            label = "B",
            value = blue,
            color = Color.Blue,
            onValueChange = { blue = it }
        )
        
        // RGB value display
        RGBValueDisplay(red, green, blue)
    }
}

@Composable
private fun IndependentColorWheel(
    modifier: Modifier = Modifier,
    selectedHue: Float,
    selectedSaturation: Float,
    onColorChanged: (hue: Float, saturation: Float) -> Unit
) {
    Canvas(
        modifier = modifier.pointerInput(Unit) {
            detectDragGestures { change, _ ->
                val center = Offset(size.width / 2f, size.height / 2f)
                val offset = change.position - center
                val radius = size.width / 2f
                val distance = sqrt(offset.x * offset.x + offset.y * offset.y)
                
                if (distance <= radius) {
                    val angle = atan2(offset.y, offset.x)
                    val hue = ((angle * 180f / PI + 360f) % 360f).toFloat()
                    val saturation = (distance / radius).coerceIn(0f, 1f)
                    
                    onColorChanged(hue, saturation)
                }
            }
        }
    ) {
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.width / 2f
        
        // Draw full brightness color wheel (always vibrant)
        drawCircle(
            brush = Brush.sweepGradient(
                colors = listOf(
                    Color.hsv(0f, 1f, 1f),    // Red
                    Color.hsv(60f, 1f, 1f),   // Yellow
                    Color.hsv(120f, 1f, 1f),  // Green
                    Color.hsv(180f, 1f, 1f),  // Cyan
                    Color.hsv(240f, 1f, 1f),  // Blue
                    Color.hsv(300f, 1f, 1f),  // Magenta
                    Color.hsv(360f, 1f, 1f)   // Red again
                ),
                center = center
            ),
            radius = radius,
            center = center
        )
        
        // Draw saturation gradient (white center to transparent edge)
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.White, Color.Transparent),
                center = center,
                radius = radius
            ),
            radius = radius,
            center = center
        )
        
        // Draw selection indicator
        val angleRad = selectedHue * PI / 180f
        val selectionRadius = selectedSaturation * radius
        val selectionX = center.x + cos(angleRad).toFloat() * selectionRadius
        val selectionY = center.y + sin(angleRad).toFloat() * selectionRadius
        
        // White outer ring
        drawCircle(
            color = Color.White,
            radius = 10f,
            center = Offset(selectionX, selectionY)
        )
        // Black inner ring
        drawCircle(
            color = Color.Black,
            radius = 8f,
            center = Offset(selectionX, selectionY)
        )
        // Current color center
        drawCircle(
            color = Color.hsv(selectedHue, selectedSaturation, 1f),
            radius = 6f,
            center = Offset(selectionX, selectionY)
        )
    }
}

@Composable
private fun ValueSlider(
    value: Float,
    hue: Float,
    saturation: Float,
    onValueChanged: (Float) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Brightness",
            fontFamily = Nunito,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(80.dp)
        )
        
        Slider(
            value = value,
            onValueChange = onValueChanged,
            valueRange = 0.1f..1f, // Minimum 10% untuk visibilitas
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = Color.hsv(hue, saturation, value),
                activeTrackColor = Color.hsv(hue, saturation, 0.7f),
                inactiveTrackColor = Color.Gray.copy(alpha = 0.3f)
            )
        )
        
        Text(
            text = "${(value * 100).toInt()}%",
            fontFamily = Nunito,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun ColorSlider(
    label: String,
    value: Int,
    color: Color,
    onValueChange: (Int) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = label,
            fontFamily = Nunito,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = color,
            modifier = Modifier.width(20.dp)
        )
        
        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 0f..255f,
            modifier = Modifier.weight(1f),
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color.copy(alpha = 0.7f),
                inactiveTrackColor = color.copy(alpha = 0.3f)
            )
        )
        
        Text(
            text = value.toString(),
            fontFamily = Nunito,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(30.dp),
            textAlign = TextAlign.End
        )
    }
}

@Composable
private fun HSVValueDisplay(hue: Float, saturation: Float, value: Float) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ValueChip("H", "${hue.toInt()}°")
        ValueChip("S", "${(saturation * 100).toInt()}%")
        ValueChip("V", "${(value * 100).toInt()}%")
    }
}

@Composable
private fun RGBValueDisplay(red: Int, green: Int, blue: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        ValueChip("R", red.toString(), Color.Red)
        ValueChip("G", green.toString(), Color.Green)
        ValueChip("B", blue.toString(), Color.Blue)
    }
}

@Composable
private fun ValueChip(label: String, value: String, color: Color = MaterialTheme.colorScheme.primary) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                fontFamily = Nunito,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = color
            )
            Text(
                text = value,
                fontFamily = Nunito,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ColorPickerItem(
    color: Color,
    isSelected: Boolean,
    onColorSelected: () -> Unit,
    size: androidx.compose.ui.unit.Dp = 40.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (isSelected) 3.dp else 1.dp,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                shape = CircleShape
            )
            .clickable { onColorSelected() },
        contentAlignment = Alignment.Center
    ) {
        if (isSelected) {
            Icon(
                Icons.Default.Check,
                contentDescription = "Selected",
                tint = if (color.luminance() < 0.5f) Color.White else Color.Black,
                modifier = Modifier.size((size.value * 0.5f).dp)
            )
        }
    }
}

// Extension functions
private fun Color.toHexString(): String {
    val red = (this.red * 255).toInt()
    val green = (this.green * 255).toInt()
    val blue = (this.blue * 255).toInt()
    return String.format("#%02X%02X%02X", red, green, blue)
}

private fun Color.toHsv(): FloatArray {
    val rgb = intArrayOf(
        (this.red * 255).toInt(),
        (this.green * 255).toInt(),
        (this.blue * 255).toInt()
    )
    val hsv = FloatArray(3)
    android.graphics.Color.RGBToHSV(rgb[0], rgb[1], rgb[2], hsv)
    return hsv
}

private fun Color.luminance(): Float {
    return 0.299f * red + 0.587f * green + 0.114f * blue
}

private fun Color.isSimilarTo(other: Color, tolerance: Float = 0.1f): Boolean {
    return abs(this.red - other.red) < tolerance &&
           abs(this.green - other.green) < tolerance &&
           abs(this.blue - other.blue) < tolerance
} 