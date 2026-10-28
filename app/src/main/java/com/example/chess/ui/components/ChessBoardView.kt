package com.example.chess.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.model.BoardThemeType
import com.example.chess.model.Move
import com.example.chess.model.Piece
import com.example.chess.model.Square
import com.example.ui.theme.ChessCrimson
import com.example.ui.theme.ChessGold

@Composable
fun ChessBoardView(
    board: Array<Array<Piece?>>,
    selectedSquare: Square?,
    legalMoves: List<Move>,
    lastMove: Move?,
    kingInCheckSquare: Square?,
    isFlipped: Boolean,
    boardTheme: BoardThemeType,
    showLegalMoves: Boolean,
    onSquareClicked: (Square) -> Unit,
    modifier: Modifier = Modifier
) {
    val palette = remember(boardTheme) { getBoardPalette(boardTheme) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "boardRotation"
    )

    // Outer luxury frame
    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .shadow(elevation = 12.dp, shape = RoundedCornerShape(12.dp))
            .clip(RoundedCornerShape(12.dp))
            .background(palette.frameBg)
            .border(3.dp, palette.frameBorder, RoundedCornerShape(12.dp))
            .padding(10.dp)
            .testTag("chess_board")
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val boardSize = maxWidth
            val squareSize = boardSize / 8

            // Rotatable Board Canvas + Grid
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(rotationAngle)
                    .clip(RoundedCornerShape(4.dp))
            ) {
                // 8x8 Board Columns and Rows
                // In normal perspective (White on bottom):
                // Display row 0 (top of screen) is rank 8 (row = 7)
                // Display row 7 (bottom of screen) is rank 1 (row = 0)
                Column(modifier = Modifier.fillMaxSize()) {
                    for (displayRow in 0..7) {
                        val row = 7 - displayRow
                        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                            for (col in 0..7) {
                                val currentSquare = Square(col, row)
                                val piece = board[col][row]
                                val isSelected = selectedSquare == currentSquare
                                val isLastMoveSquare = lastMove != null &&
                                    (lastMove.from == currentSquare || lastMove.to == currentSquare)
                                val isCheckedKing = kingInCheckSquare == currentSquare
                                val targetMove = legalMoves.find { it.to == currentSquare }
                                val isLegalTarget = showLegalMoves && targetMove != null
                                val isCaptureTarget = isLegalTarget && (piece != null || targetMove?.isEnPassant == true)

                                val isLightSquare = (col + row) % 2 != 0
                                val baseColor = if (isLightSquare) palette.lightSquare else palette.darkSquare

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .background(baseColor)
                                        .testTag("square_${currentSquare.notation}")
                                        .clickable(
                                            interactionSource = remember { MutableInteractionSource() },
                                            indication = null
                                        ) {
                                            onSquareClicked(currentSquare)
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    // 1. Last Move tint
                                    if (isLastMoveSquare) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(ChessGold.copy(alpha = 0.32f))
                                        )
                                    }

                                    // 2. Selected Square highlight
                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(ChessGold.copy(alpha = 0.50f))
                                                .border(2.dp, ChessGold)
                                        )
                                    }

                                    // 3. Checked King pulse
                                    if (isCheckedKing) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(ChessCrimson.copy(alpha = 0.65f))
                                        )
                                    }

                                    // 4. Coordinates in square corners
                                    // Show File letter on bottom rank (row 0)
                                    if (row == 0) {
                                        Text(
                                            text = currentSquare.file.toString(),
                                            color = (if (isLightSquare) palette.darkSquare else palette.lightSquare).copy(alpha = 0.8f),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .align(Alignment.BottomEnd)
                                                .padding(end = 2.dp, bottom = 1.dp)
                                                .rotate(-rotationAngle)
                                        )
                                    }
                                    // Show Rank number on left file (col 0)
                                    if (col == 0) {
                                        Text(
                                            text = currentSquare.rank.toString(),
                                            color = (if (isLightSquare) palette.darkSquare else palette.lightSquare).copy(alpha = 0.8f),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier
                                                .align(Alignment.TopStart)
                                                .padding(start = 2.dp, top = 1.dp)
                                                .rotate(-rotationAngle)
                                        )
                                    }

                                    // 5. Piece Graphic
                                    if (piece != null) {
                                        ChessPieceGraphic(
                                            piece = piece,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .rotate(-rotationAngle)
                                        )
                                    }

                                    // 6. Legal Move Indicators
                                    if (isLegalTarget) {
                                        if (isCaptureTarget) {
                                            // Capture target bracket ring
                                            Canvas(modifier = Modifier.fillMaxSize().padding(4.dp)) {
                                                drawCircle(
                                                    color = ChessCrimson.copy(alpha = 0.85f),
                                                    radius = size.minDimension * 0.44f,
                                                    style = Stroke(width = size.minDimension * 0.08f)
                                                )
                                            }
                                        } else {
                                            // Open square dot indicator
                                            Canvas(modifier = Modifier.fillMaxSize()) {
                                                drawCircle(
                                                    color = Color(0x66000000),
                                                    radius = size.minDimension * 0.16f
                                                )
                                                drawCircle(
                                                    color = ChessGold.copy(alpha = 0.90f),
                                                    radius = size.minDimension * 0.13f
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
