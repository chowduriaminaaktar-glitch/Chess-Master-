package com.example.chess.engine

import com.example.chess.model.GameStatus
import com.example.chess.model.Move
import com.example.chess.model.Piece
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.chess.model.Square

object ChessEngine {

    fun createInitialBoard(): Array<Array<Piece?>> {
        val board = Array(8) { Array<Piece?>(8) { null } }

        // White pieces (row 0: rank 1, row 1: rank 2)
        val whiteBackRank = listOf(
            PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
            PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
        )
        for (col in 0..7) {
            board[col][0] = Piece(whiteBackRank[col], PieceColor.WHITE)
            board[col][1] = Piece(PieceType.PAWN, PieceColor.WHITE)
        }

        // Black pieces (row 7: rank 8, row 6: rank 7)
        val blackBackRank = listOf(
            PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN,
            PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK
        )
        for (col in 0..7) {
            board[col][7] = Piece(blackBackRank[col], PieceColor.BLACK)
            board[col][6] = Piece(PieceType.PAWN, PieceColor.BLACK)
        }

        return board
    }

    fun cloneBoard(board: Array<Array<Piece?>>): Array<Array<Piece?>> {
        val copy = Array(8) { Array<Piece?>(8) { null } }
        for (c in 0..7) {
            for (r in 0..7) {
                copy[c][r] = board[c][r]?.copy()
            }
        }
        return copy
    }

    fun findKing(color: PieceColor, board: Array<Array<Piece?>>): Square? {
        for (c in 0..7) {
            for (r in 0..7) {
                val piece = board[c][r]
                if (piece != null && piece.color == color && piece.type == PieceType.KING) {
                    return Square(c, r)
                }
            }
        }
        return null
    }

    fun isSquareAttacked(square: Square, byColor: PieceColor, board: Array<Array<Piece?>>): Boolean {
        val targetCol = square.col
        val targetRow = square.row

        // 1. Attacked by Pawns
        val pawnPawnRow = if (byColor == PieceColor.WHITE) targetRow - 1 else targetRow + 1
        if (pawnPawnRow in 0..7) {
            if (targetCol - 1 in 0..7) {
                val p = board[targetCol - 1][pawnPawnRow]
                if (p != null && p.color == byColor && p.type == PieceType.PAWN) return true
            }
            if (targetCol + 1 in 0..7) {
                val p = board[targetCol + 1][pawnPawnRow]
                if (p != null && p.color == byColor && p.type == PieceType.PAWN) return true
            }
        }

        // 2. Attacked by Knights
        val knightOffsets = listOf(
            Pair(1, 2), Pair(2, 1), Pair(2, -1), Pair(1, -2),
            Pair(-1, -2), Pair(-2, -1), Pair(-2, 1), Pair(-1, 2)
        )
        for ((dc, dr) in knightOffsets) {
            val c = targetCol + dc
            val r = targetRow + dr
            if (c in 0..7 && r in 0..7) {
                val p = board[c][r]
                if (p != null && p.color == byColor && p.type == PieceType.KNIGHT) return true
            }
        }

        // 3. Attacked along Orthogonals (Rook, Queen)
        val orthogonalDirs = listOf(Pair(0, 1), Pair(0, -1), Pair(1, 0), Pair(-1, 0))
        for ((dc, dr) in orthogonalDirs) {
            var c = targetCol + dc
            var r = targetRow + dr
            while (c in 0..7 && r in 0..7) {
                val p = board[c][r]
                if (p != null) {
                    if (p.color == byColor && (p.type == PieceType.ROOK || p.type == PieceType.QUEEN)) {
                        return true
                    }
                    break // Blocked
                }
                c += dc
                r += dr
            }
        }

        // 4. Attacked along Diagonals (Bishop, Queen)
        val diagonalDirs = listOf(Pair(1, 1), Pair(1, -1), Pair(-1, 1), Pair(-1, -1))
        for ((dc, dr) in diagonalDirs) {
            var c = targetCol + dc
            var r = targetRow + dr
            while (c in 0..7 && r in 0..7) {
                val p = board[c][r]
                if (p != null) {
                    if (p.color == byColor && (p.type == PieceType.BISHOP || p.type == PieceType.QUEEN)) {
                        return true
                    }
                    break // Blocked
                }
                c += dc
                r += dr
            }
        }

        // 5. Attacked by King
        for (dc in -1..1) {
            for (dr in -1..1) {
                if (dc == 0 && dr == 0) continue
                val c = targetCol + dc
                val r = targetRow + dr
                if (c in 0..7 && r in 0..7) {
                    val p = board[c][r]
                    if (p != null && p.color == byColor && p.type == PieceType.KING) return true
                }
            }
        }

        return false
    }

    fun isKingInCheck(color: PieceColor, board: Array<Array<Piece?>>): Boolean {
        val kingSq = findKing(color, board) ?: return false
        return isSquareAttacked(kingSq, color.opposite(), board)
    }

    fun getPseudoLegalMoves(
        from: Square,
        board: Array<Array<Piece?>>,
        enPassantTarget: Square?
    ): List<Move> {
        val piece = board[from.col][from.row] ?: return emptyList()
        val moves = mutableListOf<Move>()
        val color = piece.color
        val opponent = color.opposite()

        when (piece.type) {
            PieceType.PAWN -> {
                val dir = if (color == PieceColor.WHITE) 1 else -1
                val startRow = if (color == PieceColor.WHITE) 1 else 6
                val promoRow = if (color == PieceColor.WHITE) 7 else 0

                // 1 step forward
                val fRow = from.row + dir
                if (fRow in 0..7 && board[from.col][fRow] == null) {
                    val to = Square(from.col, fRow)
                    if (fRow == promoRow) {
                        for (promo in listOf(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT)) {
                            moves.add(Move(from, to, piece, promotionType = promo))
                        }
                    } else {
                        moves.add(Move(from, to, piece))
                    }

                    // 2 steps forward from start rank
                    val fRow2 = from.row + 2 * dir
                    if (from.row == startRow && board[from.col][fRow2] == null) {
                        moves.add(Move(from, Square(from.col, fRow2), piece))
                    }
                }

                // Diagonals for capture
                for (dCol in listOf(-1, 1)) {
                    val c = from.col + dCol
                    val r = from.row + dir
                    if (c in 0..7 && r in 0..7) {
                        val targetPiece = board[c][r]
                        val to = Square(c, r)
                        if (targetPiece != null && targetPiece.color == opponent) {
                            if (r == promoRow) {
                                for (promo in listOf(PieceType.QUEEN, PieceType.ROOK, PieceType.BISHOP, PieceType.KNIGHT)) {
                                    moves.add(Move(from, to, piece, capturedPiece = targetPiece, promotionType = promo))
                                }
                            } else {
                                moves.add(Move(from, to, piece, capturedPiece = targetPiece))
                            }
                        } else if (enPassantTarget != null && enPassantTarget.col == c && enPassantTarget.row == r) {
                            // En passant capture
                            val capturedPawn = board[c][from.row]
                            if (capturedPawn != null && capturedPawn.color == opponent && capturedPawn.type == PieceType.PAWN) {
                                moves.add(Move(from, to, piece, capturedPiece = capturedPawn, isEnPassant = true))
                            }
                        }
                    }
                }
            }

            PieceType.KNIGHT -> {
                val knightOffsets = listOf(
                    Pair(1, 2), Pair(2, 1), Pair(2, -1), Pair(1, -2),
                    Pair(-1, -2), Pair(-2, -1), Pair(-2, 1), Pair(-1, 2)
                )
                for ((dc, dr) in knightOffsets) {
                    val c = from.col + dc
                    val r = from.row + dr
                    if (c in 0..7 && r in 0..7) {
                        val target = board[c][r]
                        if (target == null) {
                            moves.add(Move(from, Square(c, r), piece))
                        } else if (target.color == opponent) {
                            moves.add(Move(from, Square(c, r), piece, capturedPiece = target))
                        }
                    }
                }
            }

            PieceType.BISHOP -> {
                addRayMoves(from, piece, board, listOf(Pair(1, 1), Pair(1, -1), Pair(-1, 1), Pair(-1, -1)), moves)
            }

            PieceType.ROOK -> {
                addRayMoves(from, piece, board, listOf(Pair(0, 1), Pair(0, -1), Pair(1, 0), Pair(-1, 0)), moves)
            }

            PieceType.QUEEN -> {
                addRayMoves(
                    from, piece, board,
                    listOf(
                        Pair(0, 1), Pair(0, -1), Pair(1, 0), Pair(-1, 0),
                        Pair(1, 1), Pair(1, -1), Pair(-1, 1), Pair(-1, -1)
                    ),
                    moves
                )
            }

            PieceType.KING -> {
                for (dc in -1..1) {
                    for (dr in -1..1) {
                        if (dc == 0 && dr == 0) continue
                        val c = from.col + dc
                        val r = from.row + dr
                        if (c in 0..7 && r in 0..7) {
                            val target = board[c][r]
                            if (target == null) {
                                moves.add(Move(from, Square(c, r), piece))
                            } else if (target.color == opponent) {
                                moves.add(Move(from, Square(c, r), piece, capturedPiece = target))
                            }
                        }
                    }
                }

                // Castling
                if (!piece.hasMoved) {
                    val row = if (color == PieceColor.WHITE) 0 else 7
                    if (from.row == row && from.col == 4) {
                        // Kingside (col 7 rook)
                        val ksRook = board[7][row]
                        if (ksRook != null && ksRook.type == PieceType.ROOK && ksRook.color == color && !ksRook.hasMoved) {
                            if (board[5][row] == null && board[6][row] == null) {
                                moves.add(Move(from, Square(6, row), piece, isCastlingKingside = true))
                            }
                        }

                        // Queenside (col 0 rook)
                        val qsRook = board[0][row]
                        if (qsRook != null && qsRook.type == PieceType.ROOK && qsRook.color == color && !qsRook.hasMoved) {
                            if (board[1][row] == null && board[2][row] == null && board[3][row] == null) {
                                moves.add(Move(from, Square(2, row), piece, isCastlingQueenside = true))
                            }
                        }
                    }
                }
            }
        }

        return moves
    }

    private fun addRayMoves(
        from: Square,
        piece: Piece,
        board: Array<Array<Piece?>>,
        directions: List<Pair<Int, Int>>,
        outMoves: MutableList<Move>
    ) {
        val opponent = piece.color.opposite()
        for ((dc, dr) in directions) {
            var c = from.col + dc
            var r = from.row + dr
            while (c in 0..7 && r in 0..7) {
                val target = board[c][r]
                if (target == null) {
                    outMoves.add(Move(from, Square(c, r), piece))
                } else {
                    if (target.color == opponent) {
                        outMoves.add(Move(from, Square(c, r), piece, capturedPiece = target))
                    }
                    break
                }
                c += dc
                r += dr
            }
        }
    }

    fun getLegalMoves(
        from: Square,
        board: Array<Array<Piece?>>,
        turn: PieceColor,
        enPassantTarget: Square?
    ): List<Move> {
        val piece = board[from.col][from.row] ?: return emptyList()
        if (piece.color != turn) return emptyList()

        val pseudoMoves = getPseudoLegalMoves(from, board, enPassantTarget)
        val legalMoves = mutableListOf<Move>()

        for (move in pseudoMoves) {
            // Check castling legality: King cannot castle out of, through, or into check
            if (move.isCastlingKingside) {
                if (isKingInCheck(turn, board)) continue
                if (isSquareAttacked(Square(5, move.from.row), turn.opposite(), board)) continue
                if (isSquareAttacked(Square(6, move.from.row), turn.opposite(), board)) continue
            } else if (move.isCastlingQueenside) {
                if (isKingInCheck(turn, board)) continue
                if (isSquareAttacked(Square(3, move.from.row), turn.opposite(), board)) continue
                if (isSquareAttacked(Square(2, move.from.row), turn.opposite(), board)) continue
            }

            // Simulate move
            val simBoard = cloneBoard(board)
            executeMoveOnBoard(simBoard, move)

            // Verify own king is not in check
            if (!isKingInCheck(turn, simBoard)) {
                legalMoves.add(move)
            }
        }

        return legalMoves
    }

    fun getAllLegalMoves(
        color: PieceColor,
        board: Array<Array<Piece?>>,
        enPassantTarget: Square?
    ): List<Move> {
        val allMoves = mutableListOf<Move>()
        for (c in 0..7) {
            for (r in 0..7) {
                val piece = board[c][r]
                if (piece != null && piece.color == color) {
                    allMoves.addAll(getLegalMoves(Square(c, r), board, color, enPassantTarget))
                }
            }
        }
        return allMoves
    }

    fun executeMoveOnBoard(board: Array<Array<Piece?>>, move: Move) {
        val movedPiece = move.piece.copy(
            type = move.promotionType ?: move.piece.type,
            hasMoved = true
        )

        board[move.from.col][move.from.row] = null
        board[move.to.col][move.to.row] = movedPiece

        // Handle Castling Rook move
        if (move.isCastlingKingside) {
            val rook = board[7][move.to.row]?.copy(hasMoved = true)
            board[7][move.to.row] = null
            board[5][move.to.row] = rook
        } else if (move.isCastlingQueenside) {
            val rook = board[0][move.to.row]?.copy(hasMoved = true)
            board[0][move.to.row] = null
            board[3][move.to.row] = rook
        }

        // Handle En Passant pawn removal
        if (move.isEnPassant) {
            board[move.to.col][move.from.row] = null
        }
    }

    fun generateSan(
        move: Move,
        boardBefore: Array<Array<Piece?>>,
        boardAfter: Array<Array<Piece?>>,
        opponentColor: PieceColor,
        allLegalMovesFromBefore: List<Move>
    ): String {
        if (move.isCastlingKingside) {
            return appendCheckOrMate("O-O", opponentColor, boardAfter)
        }
        if (move.isCastlingQueenside) {
            return appendCheckOrMate("O-O-O", opponentColor, boardAfter)
        }

        val sb = StringBuilder()

        if (move.piece.type == PieceType.PAWN) {
            if (move.capturedPiece != null || move.isEnPassant) {
                sb.append(move.from.file)
                sb.append('x')
            }
            sb.append(move.to.notation)
            if (move.promotionType != null) {
                sb.append('=').append(move.promotionType.notation)
            }
        } else {
            sb.append(move.piece.type.notation)

            // Disambiguation if another piece of the same type could have moved to this square
            val ambiguousMoves = allLegalMovesFromBefore.filter {
                it.to == move.to &&
                it.from != move.from &&
                it.piece.type == move.piece.type &&
                it.piece.color == move.piece.color
            }

            if (ambiguousMoves.isNotEmpty()) {
                val sameFile = ambiguousMoves.any { it.from.col == move.from.col }
                val sameRank = ambiguousMoves.any { it.from.row == move.from.row }

                if (!sameFile) {
                    sb.append(move.from.file)
                } else if (!sameRank) {
                    sb.append(move.from.rank)
                } else {
                    sb.append(move.from.notation)
                }
            }

            if (move.capturedPiece != null) {
                sb.append('x')
            }
            sb.append(move.to.notation)
        }

        return appendCheckOrMate(sb.toString(), opponentColor, boardAfter)
    }

    private fun appendCheckOrMate(
        baseSan: String,
        opponentColor: PieceColor,
        boardAfter: Array<Array<Piece?>>
    ): String {
        val inCheck = isKingInCheck(opponentColor, boardAfter)
        if (!inCheck) return baseSan

        // Check if opponent has any legal moves in the position after
        val opponentMoves = getAllLegalMoves(opponentColor, boardAfter, null)
        return if (opponentMoves.isEmpty()) "$baseSan#" else "$baseSan+"
    }

    fun computeMaterialAdvantage(board: Array<Array<Piece?>>): Pair<Int, Int> {
        var whiteVal = 0
        var blackVal = 0
        for (c in 0..7) {
            for (r in 0..7) {
                val p = board[c][r] ?: continue
                if (p.color == PieceColor.WHITE) {
                    whiteVal += p.type.value
                } else {
                    blackVal += p.type.value
                }
            }
        }
        return Pair(whiteVal, blackVal)
    }
}
