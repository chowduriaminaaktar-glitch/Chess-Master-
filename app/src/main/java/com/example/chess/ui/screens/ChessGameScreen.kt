package com.example.chess.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.engine.ChessEngine
import com.example.chess.model.PieceColor
import com.example.chess.ui.components.ChessBoardView
import com.example.chess.ui.components.FullMoveHistoryModal
import com.example.chess.ui.components.GameOverDialog
import com.example.chess.ui.components.MoveHistoryRibbon
import com.example.chess.ui.components.NewGameModal
import com.example.chess.ui.components.PassAndPlayControls
import com.example.chess.ui.components.PlayerHeaderView
import com.example.chess.ui.components.PromotionDialog
import com.example.chess.ui.components.StartGameOverlay
import com.example.chess.viewmodel.ChessViewModel
import com.example.chess.viewmodel.Screen
import com.example.ui.theme.ChessBorderDark
import com.example.ui.theme.ChessDarkBg
import com.example.ui.theme.ChessGold
import com.example.ui.theme.ChessGoldLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChessGameScreen(
    viewModel: ChessViewModel,
    modifier: Modifier = Modifier
) {
    val board by viewModel.board.collectAsState()
    val turn by viewModel.turn.collectAsState()
    val selectedSquare by viewModel.selectedSquare.collectAsState()
    val legalMoves by viewModel.legalMoves.collectAsState()
    val lastMove by viewModel.lastMove.collectAsState()
    val kingInCheckSquare by viewModel.kingInCheckSquare.collectAsState()
    val capturedWhite by viewModel.capturedWhitePieces.collectAsState()
    val capturedBlack by viewModel.capturedBlackPieces.collectAsState()
    val moveHistory by viewModel.moveHistory.collectAsState()
    val status by viewModel.status.collectAsState()
    val winner by viewModel.winner.collectAsState()
    val isGameStarted by viewModel.isGameStarted.collectAsState()

    val timeControl by viewModel.timeControl.collectAsState()
    val whiteTimeMs by viewModel.whiteTimeMs.collectAsState()
    val blackTimeMs by viewModel.blackTimeMs.collectAsState()
    val isTimerRunning by viewModel.isTimerRunning.collectAsState()

    val isFaceToFace by viewModel.isFaceToFaceMode.collectAsState()
    val isBoardFlipped by viewModel.isBoardFlipped.collectAsState()
    val autoRotate by viewModel.autoRotateBoard.collectAsState()
    val boardTheme by viewModel.boardTheme.collectAsState()
    val showLegalMoves by viewModel.showLegalMoves.collectAsState()
    val whiteName by viewModel.whitePlayerName.collectAsState()
    val blackName by viewModel.blackPlayerName.collectAsState()

    val pendingPromotion by viewModel.pendingPromotionMove.collectAsState()
    val showGameOver by viewModel.showGameOverDialog.collectAsState()
    val showNewGame by viewModel.showNewGameModal.collectAsState()
    val showFullHistory by viewModel.showFullHistoryModal.collectAsState()

    // Calculate material advantage
    val (whiteMaterial, blackMaterial) = ChessEngine.computeMaterialAdvantage(board)
    val whiteAdvantage = (whiteMaterial - blackMaterial).coerceAtLeast(0)
    val blackAdvantage = (blackMaterial - whiteMaterial).coerceAtLeast(0)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .clip(CircleShape)
                                .background(ChessGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("♞", fontSize = 18.sp, color = ChessGold)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Chess Dual",
                                fontWeight = FontWeight.Bold,
                                color = ChessGold,
                                fontSize = 17.sp
                            )
                            Text(
                                text = "Pass & Play • ${timeControl.name}",
                                fontSize = 10.sp,
                                color = Color(0xFF8B837A)
                            )
                        }
                    }
                },
                actions = {
                    // New Game Button
                    IconButton(
                        onClick = { viewModel.openNewGameModal() },
                        modifier = Modifier.testTag("action_new_game")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New Game",
                            tint = ChessGold
                        )
                    }

                    // Match History Button
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.HISTORY) },
                        modifier = Modifier.testTag("action_history")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Match Records",
                            tint = Color(0xFFD4C9BC)
                        )
                    }

                    // Settings Button
                    IconButton(
                        onClick = { viewModel.navigateTo(Screen.SETTINGS) },
                        modifier = Modifier.testTag("action_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = Color(0xFFD4C9BC)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = ChessDarkBg)
            )
        },
        containerColor = ChessDarkBg,
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Top Player HUD: Black Player (Rotated 180° when face-to-face across table mode is on!)
            PlayerHeaderView(
                playerName = blackName,
                playerColor = PieceColor.BLACK,
                isCurrentTurn = turn == PieceColor.BLACK,
                timeRemainingMs = blackTimeMs,
                timeControl = timeControl,
                capturedPieces = capturedWhite,
                materialAdvantage = blackAdvantage,
                isFaceToFaceInverted = isFaceToFace
            )

            // 2. Main Chess Board
            Box(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                ChessBoardView(
                    board = board,
                    selectedSquare = selectedSquare,
                    legalMoves = legalMoves,
                    lastMove = lastMove,
                    kingInCheckSquare = kingInCheckSquare,
                    isFlipped = isBoardFlipped,
                    boardTheme = boardTheme,
                    showLegalMoves = showLegalMoves,
                    onSquareClicked = { viewModel.onSquareClicked(it) }
                )

                // Overlay when match has not started yet
                if (!isGameStarted && status == com.example.chess.model.GameStatus.ACTIVE && moveHistory.isEmpty()) {
                    StartGameOverlay(
                        whiteName = whiteName,
                        blackName = blackName,
                        timeControlName = timeControl.name,
                        onStartGame = { viewModel.startGame() },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            // 3. Move History Notation Strip
            MoveHistoryRibbon(
                moves = moveHistory,
                onOpenFullHistory = { viewModel.openFullHistoryModal() },
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
            )

            // 4. Bottom Player HUD: White Player (Always standard perspective)
            PlayerHeaderView(
                playerName = whiteName,
                playerColor = PieceColor.WHITE,
                isCurrentTurn = turn == PieceColor.WHITE,
                timeRemainingMs = whiteTimeMs,
                timeControl = timeControl,
                capturedPieces = capturedBlack,
                materialAdvantage = whiteAdvantage,
                isFaceToFaceInverted = false
            )

            // 5. Pass & Play Quick Controller Bar
            PassAndPlayControls(
                isGameStarted = isGameStarted,
                canUndo = moveHistory.isNotEmpty(),
                isTimerRunning = isTimerRunning,
                hasTimer = timeControl.durationSeconds > 0,
                isFaceToFaceMode = isFaceToFace,
                onStartGame = { viewModel.startGame() },
                onUndo = { viewModel.undoMove() },
                onTogglePause = { viewModel.togglePauseTimer() },
                onManualFlip = { viewModel.toggleManualFlip() },
                onToggleFaceToFace = { viewModel.toggleFaceToFace() },
                onOfferDraw = { viewModel.offerDraw() },
                onResign = { viewModel.resign() },
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }
    }

    // Modal Dialogs
    if (pendingPromotion != null) {
        PromotionDialog(
            color = turn,
            onSelectPieceType = { viewModel.onPromotionSelected(it) }
        )
    }

    if (showGameOver) {
        GameOverDialog(
            status = status,
            winner = winner,
            whiteName = whiteName,
            blackName = blackName,
            totalMoves = moveHistory.size,
            pgnString = viewModel.generatePgn(),
            onRematch = { viewModel.rematch() },
            onNewGame = { viewModel.openNewGameModal() },
            onDismiss = { viewModel.closeGameOverDialog() }
        )
    }

    if (showNewGame) {
        NewGameModal(
            currentTimeControl = timeControl,
            currentTheme = boardTheme,
            currentAutoFlip = autoRotate,
            currentFaceToFace = isFaceToFace,
            whiteNameDefault = whiteName,
            blackNameDefault = blackName,
            onStartGame = { tc, theme, autoFlipVal, faceVal, wName, bName ->
                viewModel.startNewGame(tc, theme, autoFlipVal, faceVal, wName, bName)
            },
            onDismiss = { viewModel.closeNewGameModal() }
        )
    }

    if (showFullHistory) {
        FullMoveHistoryModal(
            moves = moveHistory,
            pgnString = viewModel.generatePgn(),
            onDismiss = { viewModel.closeFullHistoryModal() }
        )
    }
}
