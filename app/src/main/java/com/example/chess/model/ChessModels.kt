package com.example.chess.model

enum class PieceType(val symbol: String, val notation: String, val value: Int) {
    PAWN("P", "", 1),
    KNIGHT("N", "N", 3),
    BISHOP("B", "B", 3),
    ROOK("R", "R", 5),
    QUEEN("Q", "Q", 9),
    KING("K", "K", 0)
}

enum class PieceColor {
    WHITE,
    BLACK;

    fun opposite(): PieceColor = if (this == WHITE) BLACK else WHITE
}

data class Piece(
    val type: PieceType,
    val color: PieceColor,
    val hasMoved: Boolean = false,
    val id: String = "${color}_${type}_${System.nanoTime()}"
)

data class Square(
    val col: Int, // 0 to 7 (a to h)
    val row: Int  // 0 to 7 (1 to 8 from white's perspective)
) {
    val file: Char get() = ('a'.code + col).toChar()
    val rank: Int get() = row + 1
    val notation: String get() = "$file$rank"

    fun isValid(): Boolean = col in 0..7 && row in 0..7

    companion object {
        fun fromNotation(name: String): Square? {
            if (name.length != 2) return null
            val c = name[0] - 'a'
            val r = name[1] - '1'
            return if (c in 0..7 && r in 0..7) Square(c, r) else null
        }
    }
}

data class Move(
    val from: Square,
    val to: Square,
    val piece: Piece,
    val capturedPiece: Piece? = null,
    val isCastlingKingside: Boolean = false,
    val isCastlingQueenside: Boolean = false,
    val isEnPassant: Boolean = false,
    val promotionType: PieceType? = null,
    var san: String = ""
)

enum class GameStatus {
    ACTIVE,
    CHECKMATE,
    STALEMATE,
    RESIGNATION,
    TIMEOUT,
    DRAW_BY_AGREEMENT
}

data class TimeControl(
    val name: String,
    val durationSeconds: Long,
    val incrementSeconds: Long = 0
) {
    companion object {
        val NO_TIMER = TimeControl("Casual (No Timer)", 0, 0)
        val BULLET_1 = TimeControl("Bullet 1 min", 60, 0)
        val BLITZ_3 = TimeControl("Blitz 3 min", 180, 0)
        val BLITZ_3_2 = TimeControl("Blitz 3 | 2", 180, 2)
        val BLITZ_5 = TimeControl("Blitz 5 min", 300, 0)
        val RAPID_10 = TimeControl("Rapid 10 min", 600, 0)
        val RAPID_15_10 = TimeControl("Rapid 15 | 10", 900, 10)

        val ALL = listOf(NO_TIMER, BULLET_1, BLITZ_3, BLITZ_3_2, BLITZ_5, RAPID_10, RAPID_15_10)
    }
}

enum class BoardThemeType(val displayName: String) {
    WOOD("Royal Walnut & Maple"),
    TOURNAMENT("Tournament Green"),
    OBSIDIAN("Midnight Slate"),
    GLASS_ICE("Glass & Frost")
}
