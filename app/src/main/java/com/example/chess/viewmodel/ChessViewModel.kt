package com.example.chess.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.chess.audio.SoundHapticHelper
import com.example.chess.data.ChessDatabase
import com.example.chess.data.MatchRecord
import com.example.chess.data.MatchRepository
import com.example.chess.engine.ChessEngine
import com.example.chess.model.BoardThemeType
import com.example.chess.model.GameStatus
import com.example.chess.model.Move
import com.example.chess.model.Piece
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.chess.model.Square
import com.example.chess.model.TimeControl
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class GameSnapshot(
    val board: Array<Array<Piece?>>,
    val turn: PieceColor,
    val enPassantTarget: Square?,
    val capturedWhite: List<Piece>,
    val capturedBlack: List<Piece>,
    val whiteTimeMs: Long,
    val blackTimeMs: Long,
    val moves: List<Move>,
    val lastMove: Move?
)

enum class Screen {
    GAME,
    HISTORY,
    SETTINGS
}

class ChessViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MatchRepository
    private val soundHelper: SoundHapticHelper = SoundHapticHelper(application)

    init {
        val db = ChessDatabase.getDatabase(application)
        repository = MatchRepository(db.matchDao())
    }

    val matchHistory = repository.allMatches.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Current Navigation Screen
    private val _currentScreen = MutableStateFlow(Screen.GAME)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Board & Game State
    private val _board = MutableStateFlow(ChessEngine.createInitialBoard())
    val board: StateFlow<Array<Array<Piece?>>> = _board.asStateFlow()

    private val _turn = MutableStateFlow(PieceColor.WHITE)
    val turn: StateFlow<PieceColor> = _turn.asStateFlow()

    private val _selectedSquare = MutableStateFlow<Square?>(null)
    val selectedSquare: StateFlow<Square?> = _selectedSquare.asStateFlow()

    private val _legalMoves = MutableStateFlow<List<Move>>(emptyList())
    val legalMoves: StateFlow<List<Move>> = _legalMoves.asStateFlow()

    private val _lastMove = MutableStateFlow<Move?>(null)
    val lastMove: StateFlow<Move?> = _lastMove.asStateFlow()

    private val _kingInCheckSquare = MutableStateFlow<Square?>(null)
    val kingInCheckSquare: StateFlow<Square?> = _kingInCheckSquare.asStateFlow()

    private val _capturedWhitePieces = MutableStateFlow<List<Piece>>(emptyList())
    val capturedWhitePieces: StateFlow<List<Piece>> = _capturedWhitePieces.asStateFlow()

    private val _capturedBlackPieces = MutableStateFlow<List<Piece>>(emptyList())
    val capturedBlackPieces: StateFlow<List<Piece>> = _capturedBlackPieces.asStateFlow()

    private val _moveHistory = MutableStateFlow<List<Move>>(emptyList())
    val moveHistory: StateFlow<List<Move>> = _moveHistory.asStateFlow()

    private val _status = MutableStateFlow(GameStatus.ACTIVE)
    val status: StateFlow<GameStatus> = _status.asStateFlow()

    private val _winner = MutableStateFlow<PieceColor?>(null)
    val winner: StateFlow<PieceColor?> = _winner.asStateFlow()

    private var enPassantTarget: Square? = null
    private val undoStack = mutableListOf<GameSnapshot>()
    private var matchStartTimeMs: Long = System.currentTimeMillis()

    // Timer & Clock
    private val _timeControl = MutableStateFlow(TimeControl.RAPID_10)
    val timeControl: StateFlow<TimeControl> = _timeControl.asStateFlow()

    private val _whiteTimeMs = MutableStateFlow(TimeControl.RAPID_10.durationSeconds * 1000)
    val whiteTimeMs: StateFlow<Long> = _whiteTimeMs.asStateFlow()

    private val _blackTimeMs = MutableStateFlow(TimeControl.RAPID_10.durationSeconds * 1000)
    val blackTimeMs: StateFlow<Long> = _blackTimeMs.asStateFlow()

    private val _isTimerRunning = MutableStateFlow(false)
    val isTimerRunning: StateFlow<Boolean> = _isTimerRunning.asStateFlow()

    private val _isGameStarted = MutableStateFlow(false)
    val isGameStarted: StateFlow<Boolean> = _isGameStarted.asStateFlow()

    private var clockJob: Job? = null

    fun startGame() {
        if (_isGameStarted.value) return
        _isGameStarted.value = true
        _status.value = GameStatus.ACTIVE
        matchStartTimeMs = System.currentTimeMillis()
        if (_timeControl.value.durationSeconds > 0) {
            startClock()
        }
        soundHelper.playMoveFeedback()
    }

    // Pass and Play Settings
    private val _isFaceToFaceMode = MutableStateFlow(true) // Black HUD flipped 180° for flat table play
    val isFaceToFaceMode: StateFlow<Boolean> = _isFaceToFaceMode.asStateFlow()

    private val _isBoardFlipped = MutableStateFlow(false)
    val isBoardFlipped: StateFlow<Boolean> = _isBoardFlipped.asStateFlow()

    private val _autoRotateBoard = MutableStateFlow(false)
    val autoRotateBoard: StateFlow<Boolean> = _autoRotateBoard.asStateFlow()

    private val _boardTheme = MutableStateFlow(BoardThemeType.WOOD)
    val boardTheme: StateFlow<BoardThemeType> = _boardTheme.asStateFlow()

    private val _showLegalMoves = MutableStateFlow(true)
    val showLegalMoves: StateFlow<Boolean> = _showLegalMoves.asStateFlow()

    private val _soundEnabled = MutableStateFlow(true)
    val soundEnabled: StateFlow<Boolean> = _soundEnabled.asStateFlow()

    private val _hapticsEnabled = MutableStateFlow(true)
    val hapticsEnabled: StateFlow<Boolean> = _hapticsEnabled.asStateFlow()

    private val _whitePlayerName = MutableStateFlow("White")
    val whitePlayerName: StateFlow<String> = _whitePlayerName.asStateFlow()

    private val _blackPlayerName = MutableStateFlow("Black")
    val blackPlayerName: StateFlow<String> = _blackPlayerName.asStateFlow()

    // Modals & Dialogs State
    private val _pendingPromotionMove = MutableStateFlow<Move?>(null)
    val pendingPromotionMove: StateFlow<Move?> = _pendingPromotionMove.asStateFlow()

    private val _showGameOverDialog = MutableStateFlow(false)
    val showGameOverDialog: StateFlow<Boolean> = _showGameOverDialog.asStateFlow()

    private val _showNewGameModal = MutableStateFlow(false)
    val showNewGameModal: StateFlow<Boolean> = _showNewGameModal.asStateFlow()

    private val _showFullHistoryModal = MutableStateFlow(false)
    val showFullHistoryModal: StateFlow<Boolean> = _showFullHistoryModal.asStateFlow()

    private val _turnHandoverVisible = MutableStateFlow(false)
    val turnHandoverVisible: StateFlow<Boolean> = _turnHandoverVisible.asStateFlow()

    // Navigation
    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun onSquareClicked(square: Square) {
        if (!_isGameStarted.value) {
            startGame()
        }

        if (_status.value != GameStatus.ACTIVE) {
            _showGameOverDialog.value = true
            return
        }

        val currentSelected = _selectedSquare.value
        val currentBoard = _board.value
        val currentTurn = _turn.value

        if (currentSelected == null) {
            // Pick up piece of current player
            val piece = currentBoard[square.col][square.row]
            if (piece != null && piece.color == currentTurn) {
                _selectedSquare.value = square
                _legalMoves.value = ChessEngine.getLegalMoves(square, currentBoard, currentTurn, enPassantTarget)
            }
        } else {
            // Check if clicking another piece of the same player
            val pieceAtTarget = currentBoard[square.col][square.row]
            if (pieceAtTarget != null && pieceAtTarget.color == currentTurn) {
                _selectedSquare.value = square
                _legalMoves.value = ChessEngine.getLegalMoves(square, currentBoard, currentTurn, enPassantTarget)
                return
            }

            // Check if destination is one of the legal moves
            val candidateMoves = _legalMoves.value.filter { it.to == square }
            if (candidateMoves.isNotEmpty()) {
                // If this is a promotion move, pawn can promote to Q, R, B, N
                if (candidateMoves.any { it.promotionType != null }) {
                    // Pick the first candidate move as template, show promotion dialog
                    _pendingPromotionMove.value = candidateMoves.first()
                } else {
                    executeMove(candidateMoves.first())
                }
            } else {
                // Tapped invalid square: deselect
                _selectedSquare.value = null
                _legalMoves.value = emptyList()
            }
        }
    }

    fun onPromotionSelected(type: PieceType) {
        val template = _pendingPromotionMove.value ?: return
        val finalMove = template.copy(promotionType = type)
        _pendingPromotionMove.value = null
        executeMove(finalMove)
    }

    private fun executeMove(move: Move) {
        val currentBoard = _board.value
        val currentTurn = _turn.value
        val opponent = currentTurn.opposite()

        // 1. Save snapshot for Undo
        val snapshot = GameSnapshot(
            board = ChessEngine.cloneBoard(currentBoard),
            turn = currentTurn,
            enPassantTarget = enPassantTarget,
            capturedWhite = _capturedWhitePieces.value.toList(),
            capturedBlack = _capturedBlackPieces.value.toList(),
            whiteTimeMs = _whiteTimeMs.value,
            blackTimeMs = _blackTimeMs.value,
            moves = _moveHistory.value.toList(),
            lastMove = _lastMove.value
        )
        undoStack.add(snapshot)

        // 2. Clone board and execute
        val newBoard = ChessEngine.cloneBoard(currentBoard)
        ChessEngine.executeMoveOnBoard(newBoard, move)

        // 3. Update captured pieces list
        if (move.capturedPiece != null) {
            if (move.capturedPiece.color == PieceColor.WHITE) {
                _capturedWhitePieces.value = _capturedWhitePieces.value + move.capturedPiece
            } else {
                _capturedBlackPieces.value = _capturedBlackPieces.value + move.capturedPiece
            }
        }

        // 4. Generate SAN notation
        val allLegalBefore = ChessEngine.getAllLegalMoves(currentTurn, currentBoard, enPassantTarget)
        val san = ChessEngine.generateSan(move, currentBoard, newBoard, opponent, allLegalBefore)
        move.san = san
        _moveHistory.value = _moveHistory.value + move

        // 5. Update enPassantTarget for next turn
        val isTwoSquarePawn = move.piece.type == PieceType.PAWN && Math.abs(move.to.row - move.from.row) == 2
        enPassantTarget = if (isTwoSquarePawn) {
            Square(move.from.col, (move.from.row + move.to.row) / 2)
        } else {
            null
        }

        // 6. Add Fischer increment to the player who just moved
        val incrementMs = _timeControl.value.incrementSeconds * 1000
        if (incrementMs > 0) {
            if (currentTurn == PieceColor.WHITE) {
                _whiteTimeMs.value += incrementMs
            } else {
                _blackTimeMs.value += incrementMs
            }
        }

        // 7. Update board and clear selection
        _board.value = newBoard
        _lastMove.value = move
        _selectedSquare.value = null
        _legalMoves.value = emptyList()

        // 8. Check King In Check & Game Over for opponent
        val opponentInCheck = ChessEngine.isKingInCheck(opponent, newBoard)
        if (opponentInCheck) {
            _kingInCheckSquare.value = ChessEngine.findKing(opponent, newBoard)
        } else {
            _kingInCheckSquare.value = null
        }

        val opponentMoves = ChessEngine.getAllLegalMoves(opponent, newBoard, enPassantTarget)

        if (opponentMoves.isEmpty()) {
            // Game Over!
            stopClock()
            if (opponentInCheck) {
                _status.value = GameStatus.CHECKMATE
                _winner.value = currentTurn
                soundHelper.playGameOverFeedback()
            } else {
                _status.value = GameStatus.STALEMATE
                _winner.value = null
                soundHelper.playGameOverFeedback()
            }
            saveCurrentMatchToDatabase()
            _showGameOverDialog.value = true
        } else {
            // Normal move sound / haptic feedback
            if (opponentInCheck) {
                soundHelper.playCheckFeedback()
            } else if (move.capturedPiece != null) {
                soundHelper.playCaptureFeedback()
            } else {
                soundHelper.playMoveFeedback()
            }

            // Switch turn
            _turn.value = opponent

            // Auto-rotate board if enabled
            if (_autoRotateBoard.value) {
                _isBoardFlipped.value = (opponent == PieceColor.BLACK)
            }

            // Start timer if not already running
            if (_timeControl.value.durationSeconds > 0 && !_isTimerRunning.value) {
                startClock()
            }
        }
    }

    fun undoMove() {
        if (undoStack.isEmpty()) return
        val lastState = undoStack.removeAt(undoStack.size - 1)

        _board.value = lastState.board
        _turn.value = lastState.turn
        enPassantTarget = lastState.enPassantTarget
        _capturedWhitePieces.value = lastState.capturedWhite
        _capturedBlackPieces.value = lastState.capturedBlack
        _whiteTimeMs.value = lastState.whiteTimeMs
        _blackTimeMs.value = lastState.blackTimeMs
        _moveHistory.value = lastState.moves
        _lastMove.value = lastState.lastMove
        _selectedSquare.value = null
        _legalMoves.value = emptyList()
        _status.value = GameStatus.ACTIVE
        _winner.value = null
        _showGameOverDialog.value = false

        val inCheck = ChessEngine.isKingInCheck(lastState.turn, lastState.board)
        _kingInCheckSquare.value = if (inCheck) ChessEngine.findKing(lastState.turn, lastState.board) else null

        if (_autoRotateBoard.value) {
            _isBoardFlipped.value = (lastState.turn == PieceColor.BLACK)
        }

        soundHelper.playMoveFeedback()
    }

    // Timer management
    private fun startClock() {
        _isTimerRunning.value = true
        clockJob?.cancel()
        clockJob = viewModelScope.launch {
            while (isActive && _isTimerRunning.value && _status.value == GameStatus.ACTIVE) {
                delay(100)
                if (_turn.value == PieceColor.WHITE) {
                    val remaining = _whiteTimeMs.value - 100
                    if (remaining <= 0) {
                        _whiteTimeMs.value = 0
                        handleTimeout(PieceColor.WHITE)
                        break
                    } else {
                        _whiteTimeMs.value = remaining
                    }
                } else {
                    val remaining = _blackTimeMs.value - 100
                    if (remaining <= 0) {
                        _blackTimeMs.value = 0
                        handleTimeout(PieceColor.BLACK)
                        break
                    } else {
                        _blackTimeMs.value = remaining
                    }
                }
            }
        }
    }

    private fun stopClock() {
        _isTimerRunning.value = false
        clockJob?.cancel()
    }

    fun togglePauseTimer() {
        if (_timeControl.value.durationSeconds == 0L || _status.value != GameStatus.ACTIVE) return
        if (_isTimerRunning.value) {
            stopClock()
        } else {
            startClock()
        }
    }

    private fun handleTimeout(loser: PieceColor) {
        stopClock()
        _status.value = GameStatus.TIMEOUT
        _winner.value = loser.opposite()
        soundHelper.playGameOverFeedback()
        saveCurrentMatchToDatabase()
        _showGameOverDialog.value = true
    }

    fun offerDraw() {
        if (_status.value != GameStatus.ACTIVE) return
        stopClock()
        _status.value = GameStatus.DRAW_BY_AGREEMENT
        _winner.value = null
        soundHelper.playGameOverFeedback()
        saveCurrentMatchToDatabase()
        _showGameOverDialog.value = true
    }

    fun resign() {
        if (_status.value != GameStatus.ACTIVE) return
        stopClock()
        val loser = _turn.value
        _status.value = GameStatus.RESIGNATION
        _winner.value = loser.opposite()
        soundHelper.playGameOverFeedback()
        saveCurrentMatchToDatabase()
        _showGameOverDialog.value = true
    }

    fun toggleManualFlip() {
        _isBoardFlipped.value = !_isBoardFlipped.value
    }

    fun toggleFaceToFace() {
        _isFaceToFaceMode.value = !_isFaceToFaceMode.value
    }

    fun setBoardTheme(theme: BoardThemeType) {
        _boardTheme.value = theme
    }

    fun setAutoRotate(enabled: Boolean) {
        _autoRotateBoard.value = enabled
        if (enabled) {
            _isBoardFlipped.value = (_turn.value == PieceColor.BLACK)
        }
    }

    fun setFaceToFace(enabled: Boolean) {
        _isFaceToFaceMode.value = enabled
    }

    fun setShowHints(enabled: Boolean) {
        _showLegalMoves.value = enabled
    }

    fun setSoundEnabled(enabled: Boolean) {
        _soundEnabled.value = enabled
        soundHelper.soundEnabled = enabled
    }

    fun setHapticsEnabled(enabled: Boolean) {
        _hapticsEnabled.value = enabled
        soundHelper.hapticsEnabled = enabled
    }

    fun openNewGameModal() {
        _showNewGameModal.value = true
    }

    fun closeNewGameModal() {
        _showNewGameModal.value = false
    }

    fun openFullHistoryModal() {
        _showFullHistoryModal.value = true
    }

    fun closeFullHistoryModal() {
        _showFullHistoryModal.value = false
    }

    fun closeGameOverDialog() {
        _showGameOverDialog.value = false
    }

    fun startNewGame(
        tc: TimeControl,
        theme: BoardThemeType,
        autoRotate: Boolean,
        faceToFace: Boolean,
        whiteName: String,
        blackName: String
    ) {
        stopClock()
        undoStack.clear()
        _board.value = ChessEngine.createInitialBoard()
        _turn.value = PieceColor.WHITE
        _selectedSquare.value = null
        _legalMoves.value = emptyList()
        _lastMove.value = null
        _kingInCheckSquare.value = null
        _capturedWhitePieces.value = emptyList()
        _capturedBlackPieces.value = emptyList()
        _moveHistory.value = emptyList()
        _status.value = GameStatus.ACTIVE
        _winner.value = null
        enPassantTarget = null
        matchStartTimeMs = System.currentTimeMillis()

        _timeControl.value = tc
        _whiteTimeMs.value = tc.durationSeconds * 1000
        _blackTimeMs.value = tc.durationSeconds * 1000
        _boardTheme.value = theme
        _autoRotateBoard.value = autoRotate
        _isFaceToFaceMode.value = faceToFace
        _isBoardFlipped.value = false
        _whitePlayerName.value = whiteName
        _blackPlayerName.value = blackName

        _showNewGameModal.value = false
        _showGameOverDialog.value = false
        _isGameStarted.value = false
    }

    fun rematch() {
        // Swap colors for rematch
        val previousWhite = _whitePlayerName.value
        val previousBlack = _blackPlayerName.value
        startNewGame(
            tc = _timeControl.value,
            theme = _boardTheme.value,
            autoRotate = _autoRotateBoard.value,
            faceToFace = _isFaceToFaceMode.value,
            whiteName = previousBlack,
            blackName = previousWhite
        )
    }

    fun generatePgn(): String {
        val dateFmt = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
        val dateStr = dateFmt.format(Date(matchStartTimeMs))

        val resultStr = when (_winner.value) {
            PieceColor.WHITE -> "1-0"
            PieceColor.BLACK -> "0-1"
            null -> if (_status.value == GameStatus.ACTIVE) "*" else "1/2-1/2"
        }

        val sb = StringBuilder()
        sb.appendLine("[Event \"Casual Pass & Play\"]")
        sb.appendLine("[Site \"Mobile\"]")
        sb.appendLine("[Date \"$dateStr\"]")
        sb.appendLine("[White \"${_whitePlayerName.value}\"]")
        sb.appendLine("[Black \"${_blackPlayerName.value}\"]")
        sb.appendLine("[Result \"$resultStr\"]")
        sb.appendLine("[TimeControl \"${_timeControl.value.name}\"]")
        sb.appendLine()

        val pairs = _moveHistory.value.chunked(2)
        for ((idx, pair) in pairs.withIndex()) {
            val num = idx + 1
            sb.append("$num. ${pair[0].san} ")
            if (pair.size > 1) {
                sb.append("${pair[1].san} ")
            }
        }
        sb.append(resultStr)
        return sb.toString()
    }

    private fun saveCurrentMatchToDatabase() {
        viewModelScope.launch {
            try {
                val durationSec = ((System.currentTimeMillis() - matchStartTimeMs) / 1000).coerceAtLeast(1)
                val winnerStr = when (_winner.value) {
                    PieceColor.WHITE -> "WHITE"
                    PieceColor.BLACK -> "BLACK"
                    null -> "DRAW"
                }

                val reasonStr = when (_status.value) {
                    GameStatus.CHECKMATE -> "Checkmate"
                    GameStatus.RESIGNATION -> "Resignation"
                    GameStatus.TIMEOUT -> "Timeout"
                    GameStatus.STALEMATE -> "Stalemate"
                    GameStatus.DRAW_BY_AGREEMENT -> "Agreement"
                    else -> "Draw"
                }

                val record = MatchRecord(
                    timestamp = System.currentTimeMillis(),
                    whitePlayerName = _whitePlayerName.value,
                    blackPlayerName = _blackPlayerName.value,
                    winner = winnerStr,
                    resultReason = reasonStr,
                    totalMoves = _moveHistory.value.size,
                    durationSeconds = durationSec,
                    timeControlName = _timeControl.value.name,
                    pgnMoves = generatePgn()
                )
                repository.saveMatch(record)
            } catch (_: Exception) { }
        }
    }

    fun deleteMatch(match: MatchRecord) {
        viewModelScope.launch {
            repository.deleteMatch(match)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }
}
