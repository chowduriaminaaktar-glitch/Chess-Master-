package com.example.chess.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Handshake
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chess.data.MatchRecord
import com.example.ui.theme.ChessBorderDark
import com.example.ui.theme.ChessCardDark
import com.example.ui.theme.ChessDarkBg
import com.example.ui.theme.ChessGold
import com.example.ui.theme.ChessGoldLight
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchHistoryScreen(
    matches: List<MatchRecord>,
    onBack: () -> Unit,
    onDeleteMatch: (MatchRecord) -> Unit,
    onClearAll: () -> Unit
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    val totalMatches = matches.size
    val whiteWins = matches.count { it.winner == "WHITE" }
    val blackWins = matches.count { it.winner == "BLACK" }
    val draws = matches.count { it.winner == "DRAW" }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Match Records",
                        fontWeight = FontWeight.Bold,
                        color = ChessGold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("history_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = ChessGold
                        )
                    }
                },
                actions = {
                    if (matches.isNotEmpty()) {
                        IconButton(
                            onClick = onClearAll,
                            modifier = Modifier.testTag("clear_history_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear All",
                                tint = Color(0xFFE57373)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ChessDarkBg
                )
            )
        },
        containerColor = ChessDarkBg
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            // Stats summary card
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = ChessCardDark,
                border = androidx.compose.foundation.BorderStroke(1.dp, ChessBorderDark),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .testTag("history_stats_card")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatItem(label = "Matches", value = "$totalMatches", color = ChessGoldLight)
                    StatItem(label = "White Wins", value = "$whiteWins", color = Color(0xFFFAF7EE))
                    StatItem(label = "Black Wins", value = "$blackWins", color = ChessGold)
                    StatItem(label = "Draws", value = "$draws", color = Color(0xFFAFA79E))
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            if (matches.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = Color(0xFF554C42),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No matches saved yet",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF8B837A)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Completed pass-and-play games will appear here",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF6E665E)
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(matches, key = { it.id }) { match ->
                        MatchCardItem(
                            match = match,
                            onCopyPgn = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("Chess PGN", match.pgnMoves))
                                Toast.makeText(context, "PGN copied to clipboard", Toast.LENGTH_SHORT).show()
                            },
                            onDelete = { onDeleteMatch(match) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF8B837A)
        )
    }
}

@Composable
private fun MatchCardItem(
    match: MatchRecord,
    onCopyPgn: () -> Unit,
    onDelete: () -> Unit
) {
    val isDraw = match.winner == "DRAW"
    val winnerName = when (match.winner) {
        "WHITE" -> match.whitePlayerName
        "BLACK" -> match.blackPlayerName
        else -> "Draw"
    }

    val dateFormat = SimpleDateFormat("MMM d, yyyy • HH:mm", Locale.getDefault())
    val dateStr = dateFormat.format(Date(match.timestamp))

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = ChessCardDark),
        border = androidx.compose.foundation.BorderStroke(1.dp, ChessBorderDark),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("match_card_${match.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(if (isDraw) Color(0xFF2C3238) else ChessGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isDraw) Icons.Default.Handshake else Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = if (isDraw) Color(0xFFC7D0D8) else ChessGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = if (isDraw) "Match Drawn" else "$winnerName Won",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isDraw) Color(0xFFEDE8E3) else ChessGold
                        )
                        Text(
                            text = "${match.resultReason} • ${match.totalMoves} moves",
                            fontSize = 12.sp,
                            color = Color(0xFF8B837A)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onCopyPgn,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy PGN",
                            tint = Color(0xFFAFA79E),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFF8B837A),
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Match details line
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${match.whitePlayerName} (W) vs ${match.blackPlayerName} (B)",
                    fontSize = 12.sp,
                    color = Color(0xFFD4C9BC)
                )

                Text(
                    text = match.timeControlName,
                    fontSize = 11.sp,
                    color = ChessGoldLight.copy(alpha = 0.8f)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = dateStr,
                fontSize = 10.sp,
                color = Color(0xFF6E665E)
            )
        }
    }
}
