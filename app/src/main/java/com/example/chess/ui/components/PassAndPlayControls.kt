package com.example.chess.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.TableRestaurant
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.ChessBorderDark
import com.example.ui.theme.ChessCardDark
import com.example.ui.theme.ChessGold
import com.example.ui.theme.ChessGoldLight

@Composable
fun PassAndPlayControls(
    isGameStarted: Boolean,
    canUndo: Boolean,
    isTimerRunning: Boolean,
    hasTimer: Boolean,
    isFaceToFaceMode: Boolean,
    onStartGame: () -> Unit,
    onUndo: () -> Unit,
    onTogglePause: () -> Unit,
    onManualFlip: () -> Unit,
    onToggleFaceToFace: () -> Unit,
    onOfferDraw: () -> Unit,
    onResign: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = ChessCardDark,
        border = androidx.compose.foundation.BorderStroke(1.dp, ChessBorderDark),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .testTag("pass_and_play_controls")
    ) {
        if (!isGameStarted) {
            // Unstarted game: Prominent Start Game Button + Setup helpers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onStartGame,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ChessGold,
                        contentColor = Color(0xFF141210)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("start_game_bottom_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Start Game",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Start Game",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                QuickActionButton(
                    icon = Icons.Default.ScreenRotation,
                    label = "Rotate",
                    testTag = "flip_board_button_pre",
                    onClick = onManualFlip
                )

                QuickActionButton(
                    icon = Icons.Default.TableRestaurant,
                    label = "Across",
                    active = isFaceToFaceMode,
                    testTag = "face_to_face_button_pre",
                    onClick = onToggleFaceToFace
                )
            }
        } else {
            // Active game: Complete Pass & Play Actions
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 1. Undo Move
                QuickActionButton(
                    icon = Icons.AutoMirrored.Filled.Undo,
                    label = "Undo",
                    enabled = canUndo,
                    testTag = "undo_button",
                    onClick = onUndo
                )

                // 2. Pause / Resume Clock (if timer enabled)
                if (hasTimer) {
                    QuickActionButton(
                        icon = if (isTimerRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        label = if (isTimerRunning) "Pause" else "Resume",
                        active = !isTimerRunning,
                        testTag = "pause_clock_button",
                        onClick = onTogglePause
                    )
                }

                // 3. Flip Board View
                QuickActionButton(
                    icon = Icons.Default.ScreenRotation,
                    label = "Rotate",
                    testTag = "flip_board_button",
                    onClick = onManualFlip
                )

                // 4. Face-to-Face Dual Table Mode
                QuickActionButton(
                    icon = Icons.Default.TableRestaurant,
                    label = "Across",
                    active = isFaceToFaceMode,
                    testTag = "face_to_face_button",
                    onClick = onToggleFaceToFace
                )

                // 5. Draw
                QuickActionButton(
                    icon = Icons.Default.Handshake,
                    label = "Draw",
                    testTag = "offer_draw_button",
                    onClick = onOfferDraw
                )

                // 6. Resign
                QuickActionButton(
                    icon = Icons.Default.Flag,
                    label = "Resign",
                    tint = Color(0xFFE57373),
                    testTag = "resign_button",
                    onClick = onResign
                )
            }
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    enabled: Boolean = true,
    active: Boolean = false,
    tint: Color = ChessGold,
    testTag: String,
    onClick: () -> Unit
) {
    val effectiveColor = when {
        !enabled -> Color(0xFF655D54)
        active -> ChessGoldLight
        else -> tint
    }

    val bg = if (active) ChessGold.copy(alpha = 0.25f) else Color.Transparent

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(bg)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag(testTag)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = effectiveColor,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = effectiveColor
        )
    }
}
