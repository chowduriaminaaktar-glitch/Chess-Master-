package com.example

import com.example.chess.engine.ChessEngine
import com.example.chess.model.Move
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.chess.model.Square
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ChessEngineTest {

    @Test
    fun testInitialBoardSetup() {
        val board = ChessEngine.createInitialBoard()

        // 32 pieces total
        var pieceCount = 0
        for (c in 0..7) {
            for (r in 0..7) {
                if (board[c][r] != null) pieceCount++
            }
        }
        assertEquals(32, pieceCount)

        // Kings in correct starting positions
        val whiteKing = board[4][0]
        assertNotNull(whiteKing)
        assertEquals(PieceType.KING, whiteKing?.type)
        assertEquals(PieceColor.WHITE, whiteKing?.color)

        val blackKing = board[4][7]
        assertNotNull(blackKing)
        assertEquals(PieceType.KING, blackKing?.type)
        assertEquals(PieceColor.BLACK, blackKing?.color)

        // Initial material is equal
        val (whiteVal, blackVal) = ChessEngine.computeMaterialAdvantage(board)
        assertEquals(whiteVal, blackVal)
    }

    @Test
    fun testInitialLegalMoves() {
        val board = ChessEngine.createInitialBoard()
        // White has 20 opening moves: 16 pawn moves (8 pawns x 2 options) + 4 knight moves (2 knights x 2 options)
        val allWhiteMoves = ChessEngine.getAllLegalMoves(PieceColor.WHITE, board, null)
        assertEquals(20, allWhiteMoves.size)
    }

    @Test
    fun testPawnTwoSquareAdvanceAndEnPassant() {
        val board = ChessEngine.createInitialBoard()

        // 1. Move white pawn e2 -> e4
        val e2 = Square(4, 1)
        val e4 = Square(4, 3)
        val moveE4 = Move(e2, e4, board[4][1]!!)
        ChessEngine.executeMoveOnBoard(board, moveE4)

        // 2. Move white pawn e4 -> e5
        val moveE5 = Move(e4, Square(4, 4), board[4][3]!!)
        ChessEngine.executeMoveOnBoard(board, moveE5)

        // 3. Black pawn d7 -> d5 (two square advance)
        val d7 = Square(3, 6)
        val d5 = Square(3, 4)
        val moveD5 = Move(d7, d5, board[3][6]!!)
        ChessEngine.executeMoveOnBoard(board, moveD5)

        // En passant target is d6: Square(3, 5)
        val enPassantTarget = Square(3, 5)

        // White pawn on e5 can capture en passant to d6
        val legalMovesFromE5 = ChessEngine.getLegalMoves(Square(4, 4), board, PieceColor.WHITE, enPassantTarget)
        val epMove = legalMovesFromE5.find { it.isEnPassant && it.to == enPassantTarget }
        assertNotNull("En passant move should be available", epMove)

        // Execute en passant
        ChessEngine.executeMoveOnBoard(board, epMove!!)
        assertEquals(null, board[3][4]) // Black pawn on d5 should be removed
        assertEquals(PieceType.PAWN, board[3][5]?.type)
        assertEquals(PieceColor.WHITE, board[3][5]?.color)
    }

    @Test
    fun testScholarsMate() {
        val board = ChessEngine.createInitialBoard()

        // 1. e4 e5
        ChessEngine.executeMoveOnBoard(board, Move(Square(4, 1), Square(4, 3), board[4][1]!!))
        ChessEngine.executeMoveOnBoard(board, Move(Square(4, 6), Square(4, 4), board[4][6]!!))

        // 2. Bc4 Nc6
        ChessEngine.executeMoveOnBoard(board, Move(Square(5, 0), Square(2, 3), board[5][0]!!))
        ChessEngine.executeMoveOnBoard(board, Move(Square(1, 7), Square(2, 5), board[1][7]!!))

        // 3. Qh5 Nf6
        ChessEngine.executeMoveOnBoard(board, Move(Square(3, 0), Square(7, 4), board[3][0]!!))
        ChessEngine.executeMoveOnBoard(board, Move(Square(6, 7), Square(5, 5), board[6][7]!!))

        // 4. Qxf7#
        val queen = board[7][4]!!
        val f7Pawn = board[5][6]!!
        val mateMove = Move(Square(7, 4), Square(5, 6), queen, capturedPiece = f7Pawn)
        ChessEngine.executeMoveOnBoard(board, mateMove)

        // Verify Black King is in check
        assertTrue("Black King should be in check", ChessEngine.isKingInCheck(PieceColor.BLACK, board))

        // Verify Black has zero legal moves -> Checkmate
        val blackMoves = ChessEngine.getAllLegalMoves(PieceColor.BLACK, board, null)
        assertEquals(0, blackMoves.size)
    }

    @Test
    fun testCastlingRights() {
        val board = ChessEngine.createInitialBoard()
        // Clear f1 and g1 for white kingside castling
        board[5][0] = null // f1 bishop
        board[6][0] = null // g1 knight

        val whiteKingMoves = ChessEngine.getLegalMoves(Square(4, 0), board, PieceColor.WHITE, null)
        val castleMove = whiteKingMoves.find { it.isCastlingKingside }
        assertNotNull("Kingside castling should be legal", castleMove)

        // Execute castling
        ChessEngine.executeMoveOnBoard(board, castleMove!!)
        assertEquals(PieceType.KING, board[6][0]?.type) // King on g1
        assertEquals(PieceType.ROOK, board[5][0]?.type) // Rook on f1
        assertEquals(null, board[7][0]) // h1 empty
    }
}
