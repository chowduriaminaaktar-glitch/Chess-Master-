package com.example.chess.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.model.Piece
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.chess.model.TimeControl
import com.example.ui.theme.BlackPieceFill
import com.example.ui.theme.ChessBorderDark
import com.example.ui.theme.ChessCardDark
import com.example.ui.theme.ChessCrimson
import com.example.ui.theme.ChessGold
import com.example.ui.theme.ChessGoldLight
import com.example.ui.theme.ChessGreenAccent
import com.example.ui.theme.WhitePieceFill

@Composable
fun PlayerHeaderView(
    playerName: String,
    playerColor: PieceColor,
    isCurrentTurn: Boolean,
    timeRemainingMs: Long,
    timeControl: TimeControl,
    capturedPieces: List<Piece>,
    materialAdvantage: Int, // e.g. +3
    isFaceToFaceInverted: Boolean,
    modifier: Modifier = Modifier
) {
    val rotation = if (isFaceToFaceInverted) 180f else 0f

    val isWhite = playerColor == PieceColor.WHITE
    val badgeBg = if (isWhite) WhitePieceFill else BlackPieceFill
    val badgeBorder = if (isWhite) Color(0xFF8A6C4A) else Color(0xFFD4AF37)
    val badgeIconColor = if (isWhite) Color(0xFF2A231C) else Color(0xFFEDE8E3)

    val activeBorderColor by animateColorAsState(
        targetValue = if (isCurrentTurn) ChessGold else ChessBorderDark,
        animationSpec = tween(300),
        label = "activeBorder"
    )

    val hasClock = timeControl.durationSeconds > 0
    val isLowTime = hasClock && timeRemainingMs < 30_000

    Box(
        modifier = modifier
            .fillMaxWidth()
            .rotate(rotation)
            .padding(horizontal = 8.dp, vertical = 4.dp)
            .testTag(if (isWhite) "player_header_white" else "player_header_black")
    ) {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = if (isCurrentTurn) ChessCardDark else Color(0xFF1A1714),
            border = androidx.compose.foundation.BorderStroke(
                width = if (isCurrentTurn) 2.dp else 1.dp,
                color = activeBorderColor
            ),
            shadowElevation = if (isCurrentTurn) 6.dp else 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Player Avatar + Name + Captured pieces
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    // Piece color badge
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(badgeBg)
                            .border(1.5.dp, badgeBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (isWhite) "♔" else "♚",
                            fontSize = 20.sp,
                            color = badgeIconColor
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = playerName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = if (isCurrentTurn) FontWeight.Bold else FontWeight.Medium,
                                color = if (isCurrentTurn) ChessGold else Color(0xFFE2DDD7),
                                fontSize = 15.sp
                            )

                            if (materialAdvantage > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(ChessGreenAccent.copy(alpha = 0.2f))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "+$materialAdvantage",
                                        color = ChessGreenAccent,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Captured pieces row
                        if (capturedPieces.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val grouped = capturedPieces.groupBy { it.type }
                                val order = listOf(
                                    PieceType.QUEEN,
                                    PieceType.ROOK,
                                    PieceType.BISHOP,
                                    PieceType.KNIGHT,
                                    PieceType.PAWN
                                )
                                for (type in order) {
                                    val count = grouped[type]?.size ?: 0
                                    if (count > 0) {
                                        val symbol = when (type) {
                                            PieceType.PAWN -> "♙"
                                            PieceType.KNIGHT -> "♘"
                                            PieceType.BISHOP -> "♗"
                                            PieceType.ROOK -> "♖"
                                            PieceType.QUEEN -> "♕"
                                            PieceType.KING -> "♔"
                                        }
                                        Text(
                                            text = if (count > 1) "$symbol$count " else "$symbol ",
                                            fontSize = 13.sp,
                                            color = Color(0xFFB5ABA0)
                                        )
                                    }
                                }
                            }
                        } else {
                            Text(
                                text = if (isCurrentTurn) "Thinking..." else "Waiting",
                                fontSize = 11.sp,
                                color = if (isCurrentTurn) ChessGold.copy(alpha = 0.8f) else Color(0xFF7E766D)
                            )
                        }
                    }
                }

                // Right: Chess Clock or Turn Tag
                if (hasClock) {
                    val minutes = (timeRemainingMs / 1000) / 60
                    val seconds = (timeRemainingMs / 1000) % 60
                    val tenths = (timeRemainingMs % 1000) / 100
                    val timeStr = if (minutes < 1 && isLowTime) {
                        String.format("%02d.%d", seconds, tenths)
                    } else {
                        String.format("%02d:%02d", minutes, seconds)
                    }

                    val clockBg = when {
                        isLowTime && isCurrentTurn -> ChessCrimson.copy(alpha = 0.25f)
                        isCurrentTurn -> ChessGold.copy(alpha = 0.18f)
                        else -> Color(0xFF221E1A)
                    }

                    val clockText = when {
                        isLowTime && isCurrentTurn -> ChessCrimson
                        isCurrentTurn -> ChessGoldLight
                        else -> Color(0xFFAFA79E)
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(clockBg)
                            .border(
                                1.dp,
                                if (isCurrentTurn) clockText.copy(alpha = 0.6f) else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (isCurrentTurn) {
                                Icon(
                                    imageVector = Icons.Default.HourglassBottom,
                                    contentDescription = "Active Clock",
                                    tint = clockText,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = timeStr,
                                color = clockText,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                        }
                    }
                } else {
                    // Casual mode without clock: show turn indicator badge
                    if (isCurrentTurn) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(ChessGold.copy(alpha = 0.2f))
                                .border(1.dp, ChessGold.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "TURN",
                                color = ChessGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                letterSpacing = 1.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
