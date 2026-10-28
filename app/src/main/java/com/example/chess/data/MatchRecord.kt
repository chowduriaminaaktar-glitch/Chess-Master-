package com.example.chess.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "match_records")
data class MatchRecord(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val whitePlayerName: String = "White",
    val blackPlayerName: String = "Black",
    val winner: String, // "WHITE", "BLACK", "DRAW"
    val resultReason: String, // "Checkmate", "Resignation", "Timeout", "Stalemate", "Agreement"
    val totalMoves: Int,
    val durationSeconds: Long,
    val timeControlName: String,
    val pgnMoves: String
)
