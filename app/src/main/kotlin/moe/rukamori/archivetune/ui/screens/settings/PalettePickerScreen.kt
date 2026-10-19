/*
 * YumaPlayer (2026) | Modified work by MuwMix
 * ArchiveTune (2026) | Original work by © Rukamori
 * GPL-3.0 License | Contributors: see git history
 */

package moe.rukamori.archivetune.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import moe.rukamori.archivetune.LocalPlayerAwareWindowInsets
import moe.rukamori.archivetune.R
import moe.rukamori.archivetune.constants.CustomThemeColorKey
import moe.rukamori.archivetune.ui.component.IconButton
import moe.rukamori.archivetune.ui.theme.ThemePreviews
import moe.rukamori.archivetune.ui.theme.TestThemeWrapper
import moe.rukamori.archivetune.ui.utils.backToMain
import moe.rukamori.archivetune.utils.rememberPreference
import androidx.navigation.compose.rememberNavController

// Accent colors used when artwork-based colors are unavailable.
val TargetAccentColors = listOf(
    Color(0xFF7C5CFF), // Noctune violet
    Color(0xFF4A90D9), // Blue
    Color(0xFF1DB954), // Green
    Color(0xFF9B59B6), // Purple
    Color(0xFFED5564), // Coral
    Color(0xFFE67E22), // Orange
    Color(0xFF1ABC9C), // Teal
    Color(0xFF708090), // Slate
    Color(0xFFFF69B4)  // Pink
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PalettePickerScreen(navController: NavController) {
    val (customThemeColor, onCustomThemeColorChange) = rememberPreference(
        CustomThemeColorKey,
        defaultValue = "#7C5CFF"
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.color_palette)) },
                navigationIcon = {
                    IconButton(
                        onClick = navController::navigateUp,
                        onLongClick = navController::backToMain,
                    ) {
                        Icon(painterResource(R.drawable.arrow_back), contentDescription = null)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Choose the app accent color used when artwork-based colors are unavailable.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(TargetAccentColors) { color ->
                    val hexString = String.format("#%06X", 0xFFFFFF and color.toArgb())
                    val isSelected = customThemeColor == hexString || (customThemeColor == "default" && hexString == "#7C5CFF")

                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable {
                                onCustomThemeColorChange(hexString)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                painter = painterResource(R.drawable.check),
                                contentDescription = null,
                                tint = if (color.red * 0.2126 + color.green * 0.7152 + color.blue * 0.0722 > 0.5) Color.Black else Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Lets users create a personal Noctune accent color. Palette choices remain separate
 * from layout options, so changing a color never changes the app's layout.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeCreatorScreen(navController: NavController) {
    val (savedColor, onSavedColorChange) = rememberPreference(
        CustomThemeColorKey,
        defaultValue = "#7C5CFF"
    )
    var hexInput by remember(savedColor) {
        mutableStateOf(savedColor.removePrefix("#").uppercase().takeIf { it.matches(Regex("[0-9A-F]{6}")) } ?: "7C5CFF")
    }
    val normalizedHex = "#$hexInput"
    val isValid = hexInput.matches(Regex("[0-9A-Fa-f]{6}"))
    val previewColor = remember(normalizedHex, isValid) {
        if (isValid) Color(android.graphics.Color.parseColor(normalizedHex)) else Color(0xFF7C5CFF)
    }

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = { Text("Create a theme") },
                navigationIcon = {
                    IconButton(
                        onClick = navController::navigateUp,
                        onLongClick = navController::backToMain,
                    ) {
                        Icon(painterResource(R.drawable.arrow_back), contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Create a Noctune accent palette",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Choose a color that fits your setup. This changes the app's color palette, not its layout.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(112.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(previewColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Noctune",
                    style = MaterialTheme.typography.headlineMedium,
                    color = if (previewColor.luminance() > 0.5f) Color.Black else Color.White
                )
            }
            OutlinedTextField(
                value = hexInput,
                onValueChange = { input ->
                    hexInput = input.filter { it.isDigit() || it.lowercaseChar() in 'a'..'f' }
                        .uppercase()
                        .take(6)
                },
                label = { Text("Hex color (without #)") },
                prefix = { Text("#") },
                supportingText = {
                    Text(if (isValid) "Preview updates as you type." else "Enter exactly 6 hexadecimal characters.")
                },
                isError = !isValid,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = "Quick colors",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                TargetAccentColors.take(5).forEach { color ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (color == previewColor) 3.dp else 1.dp,
                                color = if (color == previewColor) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable {
                                hexInput = String.format("%06X", 0xFFFFFF and color.toArgb())
                            }
                    )
                }
            }
            Button(
                onClick = {
                    onSavedColorChange(normalizedHex)
                    navController.navigateUp()
                },
                enabled = isValid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save theme")
            }
            OutlinedButton(
                onClick = {
                    hexInput = "7C5CFF"
                    onSavedColorChange("#7C5CFF")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Reset to Noctune violet")
            }
        }
    }
}
@ThemePreviews
@Composable
private fun PalettePickerScreenPreview() {
    TestThemeWrapper {
        PalettePickerScreen(navController = rememberNavController())
    }
}