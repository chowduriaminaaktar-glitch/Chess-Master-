package com.example.chess.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.model.Move
import com.example.ui.theme.ChessBorderDark
import com.example.ui.theme.ChessGold

@Composable
fun MoveHistoryRibbon(
    moves: List<Move>,
    onOpenFullHistory: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    // Auto-scroll to latest move when moves change
    LaunchedEffect(moves.size) {
        if (moves.isNotEmpty()) {
            scrollState.animateScrollTo(scrollState.maxValue)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF191613))
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag("move_history_ribbon")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.List,
                contentDescription = "Full Move History",
                tint = ChessGold.copy(alpha = 0.7f),
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .clickable(onClick = onOpenFullHistory)
                    .padding(4.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            if (moves.isEmpty()) {
                Text(
                    text = "New match — White to move (tap pieces to see moves)",
                    color = Color(0xFF8B837A),
                    fontSize = 12.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            } else {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(scrollState),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pair moves by fullmove number
                    val pairs = moves.chunked(2)
                    for ((index, pair) in pairs.withIndex()) {
                        val moveNumber = index + 1
                        val whiteMove = pair.getOrNull(0)
                        val blackMove = pair.getOrNull(1)

                        Text(
                            text = "$moveNumber.",
                            color = Color(0xFF7A7269),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(start = if (index > 0) 8.dp else 0.dp, end = 4.dp)
                        )

                        if (whiteMove != null) {
                            Text(
                                text = whiteMove.san,
                                color = Color(0xFFFAF7EE),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }

                        if (blackMove != null) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = blackMove.san,
                                color = ChessGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}
