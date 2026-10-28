package com.example.chess.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.model.GameStatus
import com.example.chess.model.PieceColor
import com.example.ui.theme.ChessBorderDark
import com.example.ui.theme.ChessCardDark
import com.example.ui.theme.ChessCrimson
import com.example.ui.theme.ChessGold
import com.example.ui.theme.ChessGreenAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameOverDialog(
    status: GameStatus,
    winner: PieceColor?,
    whiteName: String,
    blackName: String,
    totalMoves: Int,
    pgnString: String,
    onRematch: () -> Unit,
    onNewGame: () -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    val title: String
    val subtitle: String
    val isDraw = winner == null

    when (status) {
        GameStatus.CHECKMATE -> {
            val winnerName = if (winner == PieceColor.WHITE) whiteName else blackName
            title = "$winnerName Wins!"
            subtitle = "Victory by Checkmate"
        }
        GameStatus.RESIGNATION -> {
            val winnerName = if (winner == PieceColor.WHITE) whiteName else blackName
            title = "$winnerName Wins!"
            subtitle = "Opponent resigned"
        }
        GameStatus.TIMEOUT -> {
            val winnerName = if (winner == PieceColor.WHITE) whiteName else blackName
            title = "$winnerName Wins!"
            subtitle = "Won on time"
        }
        GameStatus.STALEMATE -> {
            title = "Stalemate!"
            subtitle = "Draw - No legal moves available"
        }
        GameStatus.DRAW_BY_AGREEMENT -> {
            title = "Draw Agreed"
            subtitle = "Match ended in mutual agreement"
        }
        else -> {
            title = "Game Over"
            subtitle = ""
        }
    }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(18.dp),
            color = ChessCardDark,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, ChessGold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("game_over_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Trophy / Handshake Icon badge
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(if (isDraw) Color(0xFF2C3238) else ChessGold.copy(alpha = 0.2f))
                        .border(
                            1.5.dp,
                            if (isDraw) Color(0xFF8B9DAF) else ChessGold,
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isDraw) Icons.Default.Handshake else Icons.Default.EmojiEvents,
                        contentDescription = null,
                        tint = if (isDraw) Color(0xFFC7D0D8) else ChessGold,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isDraw) Color(0xFFEDE8E3) else ChessGold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFAFA79E),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Total Moves: $totalMoves",
                    fontSize = 12.sp,
                    color = Color(0xFF8B837A)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Actions: Rematch, New Game, Copy PGN
                Button(
                    onClick = onRematch,
                    colors = ButtonDefaults.buttonColors(containerColor = ChessGold, contentColor = Color(0xFF141210)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("rematch_button")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Rematch (Swap Sides)", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onNewGame,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = ChessGold),
                    border = androidx.compose.foundation.BorderStroke(1.dp, ChessBorderDark),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("new_game_button")
                ) {
                    Text("New Match Setup", fontWeight = FontWeight.SemiBold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Chess PGN", pgnString))
                            Toast.makeText(context, "PGN copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF383129)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFC7BCB0)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("copy_pgn_button")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Copy PGN", fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    OutlinedButton(
                        onClick = onDismiss,
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF383129)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFAFA79E)),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("review_board_button")
                    ) {
                        Text("Review Board", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
