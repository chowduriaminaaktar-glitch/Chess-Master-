package com.example.chess.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.model.BoardThemeType
import com.example.chess.model.TimeControl
import com.example.ui.theme.ChessBorderDark
import com.example.ui.theme.ChessCardDark
import com.example.ui.theme.ChessGold
import com.example.ui.theme.ChessGoldLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewGameModal(
    currentTimeControl: TimeControl,
    currentTheme: BoardThemeType,
    currentAutoFlip: Boolean,
    currentFaceToFace: Boolean,
    whiteNameDefault: String,
    blackNameDefault: String,
    onStartGame: (TimeControl, BoardThemeType, Boolean, Boolean, String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedTimeControl by remember { mutableStateOf(currentTimeControl) }
    var selectedTheme by remember { mutableStateOf(currentTheme) }
    var autoFlip by remember { mutableStateOf(currentAutoFlip) }
    var faceToFace by remember { mutableStateOf(currentFaceToFace) }
    var whiteName by remember { mutableStateOf(whiteNameDefault) }
    var blackName by remember { mutableStateOf(blackNameDefault) }

    val scrollState = rememberScrollState()

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = ChessCardDark,
            border = androidx.compose.foundation.BorderStroke(1.dp, ChessBorderDark),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("new_game_modal")
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(scrollState)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "New Match Setup",
                        style = MaterialTheme.typography.titleLarge,
                        color = ChessGold,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFFAFA79E))
                    }
                }

                HorizontalDivider(color = ChessBorderDark, thickness = 1.dp)

                Spacer(modifier = Modifier.height(14.dp))

                // Player Names
                Text(
                    text = "Player Names (Pass & Play)",
                    color = ChessGoldLight,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = whiteName,
                        onValueChange = { whiteName = it },
                        label = { Text("White (1st)") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("white_name_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ChessGold,
                            unfocusedBorderColor = ChessBorderDark,
                            focusedLabelColor = ChessGold,
                            unfocusedLabelColor = Color(0xFF8B837A)
                        )
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedTextField(
                        value = blackName,
                        onValueChange = { blackName = it },
                        label = { Text("Black (2nd)") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("black_name_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ChessGold,
                            unfocusedBorderColor = ChessBorderDark,
                            focusedLabelColor = ChessGold,
                            unfocusedLabelColor = Color(0xFF8B837A)
                        )
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Time Control Presets
                Text(
                    text = "Time Control",
                    color = ChessGoldLight,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (tc in TimeControl.ALL) {
                        val isSelected = selectedTimeControl.name == tc.name
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ChessGold.copy(alpha = 0.2f) else Color(0xFF1E1A16))
                                .border(
                                    1.dp,
                                    if (isSelected) ChessGold else ChessBorderDark,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedTimeControl = tc }
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = tc.name,
                                color = if (isSelected) ChessGoldLight else Color(0xFFEDE8E3),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                            if (isSelected) {
                                Text(
                                    text = "Selected",
                                    color = ChessGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Board Theme Selection
                Text(
                    text = "Board Theme",
                    color = ChessGoldLight,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (theme in BoardThemeType.values()) {
                        val isSelected = selectedTheme == theme
                        val palette = getBoardPalette(theme)

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ChessGold.copy(alpha = 0.2f) else Color(0xFF1E1A16))
                                .border(
                                    1.5.dp,
                                    if (isSelected) ChessGold else ChessBorderDark,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { selectedTheme = theme }
                                .padding(6.dp)
                        ) {
                            // Mini board sample
                            Row(modifier = Modifier.size(28.dp).clip(RoundedCornerShape(4.dp))) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Box(modifier = Modifier.weight(1f).fillMaxWidth().background(palette.lightSquare))
                                    Box(modifier = Modifier.weight(1f).fillMaxWidth().background(palette.darkSquare))
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Box(modifier = Modifier.weight(1f).fillMaxWidth().background(palette.darkSquare))
                                    Box(modifier = Modifier.weight(1f).fillMaxWidth().background(palette.lightSquare))
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = when (theme) {
                                    BoardThemeType.WOOD -> "Wood"
                                    BoardThemeType.TOURNAMENT -> "Tourn"
                                    BoardThemeType.OBSIDIAN -> "Slate"
                                    BoardThemeType.GLASS_ICE -> "Ice"
                                },
                                fontSize = 11.sp,
                                color = if (isSelected) ChessGoldLight else Color(0xFFAFA79E),
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Pass & Play Comfort Toggles
                Text(
                    text = "Pass & Play System",
                    color = ChessGoldLight,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Across-Table Mode Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Across-Table (Face-to-Face)", color = Color(0xFFEDE8E3), fontSize = 14.sp)
                        Text("Inverts Player 2's clock & HUD 180° for flat table play", color = Color(0xFF8B837A), fontSize = 11.sp)
                    }
                    Switch(
                        checked = faceToFace,
                        onCheckedChange = { faceToFace = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = ChessGold, checkedTrackColor = ChessGold.copy(alpha = 0.4f))
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Auto-Rotate Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Auto-Rotate Board on Turn", color = Color(0xFFEDE8E3), fontSize = 14.sp)
                        Text("Flips perspective to the player currently moving", color = Color(0xFF8B837A), fontSize = 11.sp)
                    }
                    Switch(
                        checked = autoFlip,
                        onCheckedChange = { autoFlip = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = ChessGold, checkedTrackColor = ChessGold.copy(alpha = 0.4f))
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Start Match Button
                Button(
                    onClick = {
                        onStartGame(
                            selectedTimeControl,
                            selectedTheme,
                            autoFlip,
                            faceToFace,
                            whiteName.ifBlank { "White" },
                            blackName.ifBlank { "Black" }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ChessGold, contentColor = Color(0xFF141210)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("start_match_button")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Start Match", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
