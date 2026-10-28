package com.example.chess.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.model.BoardThemeType
import com.example.chess.ui.components.getBoardPalette
import com.example.ui.theme.ChessBorderDark
import com.example.ui.theme.ChessCardDark
import com.example.ui.theme.ChessDarkBg
import com.example.ui.theme.ChessGold
import com.example.ui.theme.ChessGoldLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    currentTheme: BoardThemeType,
    autoRotate: Boolean,
    faceToFace: Boolean,
    showHints: Boolean,
    soundEnabled: Boolean,
    hapticsEnabled: Boolean,
    onThemeChanged: (BoardThemeType) -> Unit,
    onAutoRotateChanged: (Boolean) -> Unit,
    onFaceToFaceChanged: (Boolean) -> Unit,
    onShowHintsChanged: (Boolean) -> Unit,
    onSoundChanged: (Boolean) -> Unit,
    onHapticsChanged: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    BackHandler { onBack() }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Settings & Preferences",
                        fontWeight = FontWeight.Bold,
                        color = ChessGold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("settings_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ChessGold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ChessDarkBg)
            )
        },
        containerColor = ChessDarkBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(10.dp))

            // 1. Board Appearance
            SectionHeader(icon = Icons.Default.Palette, title = "Board Theme")

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ChessCardDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, ChessBorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    for (theme in BoardThemeType.values()) {
                        val isSelected = currentTheme == theme
                        val palette = getBoardPalette(theme)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ChessGold.copy(alpha = 0.18f) else Color.Transparent)
                                .clickable { onThemeChanged(theme) }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                // Board sample icon
                                Row(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .border(1.dp, palette.frameBorder, RoundedCornerShape(4.dp))
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Box(modifier = Modifier.weight(1f).fillMaxWidth().background(palette.lightSquare))
                                        Box(modifier = Modifier.weight(1f).fillMaxWidth().background(palette.darkSquare))
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Box(modifier = Modifier.weight(1f).fillMaxWidth().background(palette.darkSquare))
                                        Box(modifier = Modifier.weight(1f).fillMaxWidth().background(palette.lightSquare))
                                    }
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = theme.displayName,
                                    fontSize = 14.sp,
                                    color = if (isSelected) ChessGoldLight else Color(0xFFEDE8E3),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }

                            if (isSelected) {
                                Text(
                                    text = "Active",
                                    color = ChessGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 2. Single Mobile Pass & Play System
            SectionHeader(icon = Icons.Default.PhoneAndroid, title = "Pass & Play System (Single Mobile)")

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ChessCardDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, ChessBorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    SettingToggleRow(
                        title = "Across-Table (Face-to-Face) HUD",
                        description = "Inverts Black's clock & player HUD 180° for flat table play",
                        checked = faceToFace,
                        onCheckedChange = onFaceToFaceChanged
                    )

                    HorizontalDivider(color = ChessBorderDark.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 10.dp))

                    SettingToggleRow(
                        title = "Auto-Rotate Board on Turn",
                        description = "Automatically spins board 180° so the current player is on bottom",
                        checked = autoRotate,
                        onCheckedChange = onAutoRotateChanged
                    )

                    HorizontalDivider(color = ChessBorderDark.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 10.dp))

                    SettingToggleRow(
                        title = "Highlight Legal Moves",
                        description = "Shows candidate target dots and capture targets when tapping pieces",
                        checked = showHints,
                        onCheckedChange = onShowHintsChanged
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 3. Audio & Haptics
            SectionHeader(icon = Icons.Default.VolumeUp, title = "Sound & Vibration")

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ChessCardDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, ChessBorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    SettingToggleRow(
                        title = "Sound Effects",
                        description = "Realistic wooden knock on moves, captures, checks, and victory",
                        checked = soundEnabled,
                        onCheckedChange = onSoundChanged
                    )

                    HorizontalDivider(color = ChessBorderDark.copy(alpha = 0.5f), modifier = Modifier.padding(vertical = 10.dp))

                    SettingToggleRow(
                        title = "Tactile Haptic Feedback",
                        description = "Subtle vibration pulse on piece moves and captures",
                        checked = hapticsEnabled,
                        onCheckedChange = onHapticsChanged
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 4. Quick Rules & Tips
            SectionHeader(icon = Icons.Default.Info, title = "Two-Player Pass & Play Rules")

            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = ChessCardDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, ChessBorderDark),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "• Castling: Tap the King and move it two squares towards a Rook that hasn't moved.",
                        fontSize = 12.sp,
                        color = Color(0xFFD4C9BC)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• En Passant: If an opponent pawn advances 2 squares past yours, capture it diagonally on your next turn.",
                        fontSize = 12.sp,
                        color = Color(0xFFD4C9BC)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Pawn Promotion: Reaching the opposite end displays a selection menu for Queen, Rook, Bishop, or Knight.",
                        fontSize = 12.sp,
                        color = Color(0xFFD4C9BC)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• Table Mode: Place your mobile flat on a table between both players. Enable 'Across' to invert Player 2's clock!",
                        fontSize = 12.sp,
                        color = ChessGoldLight
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
private fun SectionHeader(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(bottom = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = ChessGold,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = ChessGoldLight
        )
    }
}

@Composable
private fun SettingToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFEDE8E3)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = description,
                fontSize = 11.sp,
                color = Color(0xFF8B837A)
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = ChessGold,
                checkedTrackColor = ChessGold.copy(alpha = 0.4f),
                uncheckedThumbColor = Color(0xFF7E766D),
                uncheckedTrackColor = Color(0xFF2E2721)
            )
        )
    }
}
