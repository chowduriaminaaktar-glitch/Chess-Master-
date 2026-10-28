package com.example.chess.ui.components

import androidx.compose.ui.graphics.Color
import com.example.chess.model.BoardThemeType
import com.example.ui.theme.IceDarkSquare
import com.example.ui.theme.IceFrame
import com.example.ui.theme.IceFrameBorder
import com.example.ui.theme.IceLightSquare
import com.example.ui.theme.SlateDarkSquare
import com.example.ui.theme.SlateFrame
import com.example.ui.theme.SlateFrameBorder
import com.example.ui.theme.SlateLightSquare
import com.example.ui.theme.TournDarkSquare
import com.example.ui.theme.TournFrame
import com.example.ui.theme.TournFrameBorder
import com.example.ui.theme.TournLightSquare
import com.example.ui.theme.WoodDarkSquare
import com.example.ui.theme.WoodFrame
import com.example.ui.theme.WoodFrameBorder
import com.example.ui.theme.WoodLightSquare

data class BoardPalette(
    val lightSquare: Color,
    val darkSquare: Color,
    val frameBg: Color,
    val frameBorder: Color,
    val coordinateColor: Color
)

fun getBoardPalette(theme: BoardThemeType): BoardPalette {
    return when (theme) {
        BoardThemeType.WOOD -> BoardPalette(
            lightSquare = WoodLightSquare,
            darkSquare = WoodDarkSquare,
            frameBg = WoodFrame,
            frameBorder = WoodFrameBorder,
            coordinateColor = Color(0xFFCBB196)
        )
        BoardThemeType.TOURNAMENT -> BoardPalette(
            lightSquare = TournLightSquare,
            darkSquare = TournDarkSquare,
            frameBg = TournFrame,
            frameBorder = TournFrameBorder,
            coordinateColor = Color(0xFFB5C9A9)
        )
        BoardThemeType.OBSIDIAN -> BoardPalette(
            lightSquare = SlateLightSquare,
            darkSquare = SlateDarkSquare,
            frameBg = SlateFrame,
            frameBorder = SlateFrameBorder,
            coordinateColor = Color(0xFF90A4AE)
        )
        BoardThemeType.GLASS_ICE -> BoardPalette(
            lightSquare = IceLightSquare,
            darkSquare = IceDarkSquare,
            frameBg = IceFrame,
            frameBorder = IceFrameBorder,
            coordinateColor = Color(0xFFA5C4D4)
        )
    }
}
