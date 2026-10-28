package com.example.chess.data

import kotlinx.coroutines.flow.Flow

class MatchRepository(private val matchDao: MatchDao) {
    val allMatches: Flow<List<MatchRecord>> = matchDao.getAllMatches()

    suspend fun saveMatch(match: MatchRecord): Long = matchDao.insertMatch(match)

    suspend fun deleteMatch(match: MatchRecord) = matchDao.deleteMatch(match)

    suspend fun clearHistory() = matchDao.clearAllMatches()
}
