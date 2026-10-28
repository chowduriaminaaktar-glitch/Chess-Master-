package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.chess.ui.screens.ChessGameScreen
import com.example.chess.ui.screens.MatchHistoryScreen
import com.example.chess.ui.screens.SettingsScreen
import com.example.chess.viewmodel.ChessViewModel
import com.example.chess.viewmodel.Screen
import com.example.ui.theme.ChessDarkBg
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: ChessViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = ChessDarkBg
                ) {
                    val currentScreen by viewModel.currentScreen.collectAsState()
                    val matchHistory by viewModel.matchHistory.collectAsState()
                    val boardTheme by viewModel.boardTheme.collectAsState()
                    val autoRotate by viewModel.autoRotateBoard.collectAsState()
                    val faceToFace by viewModel.isFaceToFaceMode.collectAsState()
                    val showHints by viewModel.showLegalMoves.collectAsState()
                    val soundEnabled by viewModel.soundEnabled.collectAsState()
                    val hapticsEnabled by viewModel.hapticsEnabled.collectAsState()

                    when (currentScreen) {
                        Screen.GAME -> {
                            ChessGameScreen(viewModel = viewModel)
                        }
                        Screen.HISTORY -> {
                            MatchHistoryScreen(
                                matches = matchHistory,
                                onBack = { viewModel.navigateTo(Screen.GAME) },
                                onDeleteMatch = { viewModel.deleteMatch(it) },
                                onClearAll = { viewModel.clearAllHistory() }
                            )
                        }
                        Screen.SETTINGS -> {
                            SettingsScreen(
                                currentTheme = boardTheme,
                                autoRotate = autoRotate,
                                faceToFace = faceToFace,
                                showHints = showHints,
                                soundEnabled = soundEnabled,
                                hapticsEnabled = hapticsEnabled,
                                onThemeChanged = { viewModel.setBoardTheme(it) },
                                onAutoRotateChanged = { viewModel.setAutoRotate(it) },
                                onFaceToFaceChanged = { viewModel.setFaceToFace(it) },
                                onShowHintsChanged = { viewModel.setShowHints(it) },
                                onSoundChanged = { viewModel.setSoundEnabled(it) },
                                onHapticsChanged = { viewModel.setHapticsEnabled(it) },
                                onBack = { viewModel.navigateTo(Screen.GAME) }
                            )
                        }
                    }
                }
            }
        }
    }
}
