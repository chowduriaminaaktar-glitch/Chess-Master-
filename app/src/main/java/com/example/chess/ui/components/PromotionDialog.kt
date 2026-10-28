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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.model.Piece
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.ui.theme.ChessBorderDark
import com.example.ui.theme.ChessCardDark
import com.example.ui.theme.ChessGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PromotionDialog(
    color: PieceColor,
    onSelectPieceType: (PieceType) -> Unit
) {
    BasicAlertDialog(
        onDismissRequest = { /* Force selection to keep game in valid state */ }
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = ChessCardDark,
            border = androidx.compose.foundation.BorderStroke(1.5.dp, ChessGold),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("promotion_dialog")
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Promote Pawn",
                    style = MaterialTheme.typography.titleLarge,
                    color = ChessGold,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Choose a piece for ${if (color == PieceColor.WHITE) "White" else "Black"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(20.dp))

                val choices = listOf(
                    PieceType.QUEEN to "Queen",
                    PieceType.ROOK to "Rook",
                    PieceType.BISHOP to "Bishop",
                    PieceType.KNIGHT to "Knight"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    for ((type, name) in choices) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, ChessBorderDark, RoundedCornerShape(10.dp))
                                .background(androidx.compose.ui.graphics.Color(0xFF1E1A16))
                                .clickable { onSelectPieceType(type) }
                                .padding(horizontal = 8.dp, vertical = 10.dp)
                                .testTag("promote_to_${type.name.lowercase()}")
                        ) {
                            Box(modifier = Modifier.size(52.dp)) {
                                ChessPieceGraphic(
                                    piece = Piece(type, color),
                                    modifier = Modifier.size(52.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = name,
                                fontSize = 11.sp,
                                color = ChessGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
