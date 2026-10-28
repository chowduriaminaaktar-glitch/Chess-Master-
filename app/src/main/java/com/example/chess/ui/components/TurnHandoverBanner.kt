package com.example.chess.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.model.PieceColor
import com.example.ui.theme.BlackPieceFill
import com.example.ui.theme.ChessGold
import com.example.ui.theme.WhitePieceFill

@Composable
fun TurnHandoverBanner(
    visible: Boolean,
    turn: PieceColor,
    playerName: String,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -it / 2 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -it / 2 }),
        modifier = modifier
    ) {
        val isWhite = turn == PieceColor.WHITE
        val badgeBg = if (isWhite) WhitePieceFill else BlackPieceFill
        val badgeIconColor = if (isWhite) Color(0xFF141210) else Color(0xFFFAF7EE)

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(30.dp))
                .background(Color(0xE61E1A16))
                .border(1.5.dp, ChessGold, RoundedCornerShape(30.dp))
                .clickable(onClick = onDismiss)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("turn_handover_banner")
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(badgeBg),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isWhite) "♔" else "♚",
                        fontSize = 14.sp,
                        color = badgeIconColor
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Pass to $playerName ($turn) • Tap to ready",
                    color = ChessGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
