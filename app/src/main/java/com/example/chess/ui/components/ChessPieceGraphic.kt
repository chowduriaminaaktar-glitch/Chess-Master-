package com.example.chess.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.chess.model.Piece
import com.example.chess.model.PieceColor
import com.example.chess.model.PieceType
import com.example.ui.theme.BlackPieceBorder
import com.example.ui.theme.BlackPieceFill
import com.example.ui.theme.BlackPieceHighlight
import com.example.ui.theme.WhitePieceBorder
import com.example.ui.theme.WhitePieceFill
import com.example.ui.theme.WhitePieceShadow

@Composable
fun ChessPieceGraphic(
    piece: Piece,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.padding(4.dp)) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val isWhite = piece.color == PieceColor.WHITE

            val fillBrush = if (isWhite) {
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFFFFFFF),
                        WhitePieceFill,
                        WhitePieceShadow
                    )
                )
            } else {
                Brush.verticalGradient(
                    colors = listOf(
                        BlackPieceHighlight,
                        BlackPieceFill,
                        Color(0xFF151413)
                    )
                )
            }

            val strokeColor = if (isWhite) WhitePieceBorder else BlackPieceBorder
            val strokeWidth = size.width * 0.035f

            when (piece.type) {
                PieceType.PAWN -> drawPawn(fillBrush, strokeColor, strokeWidth)
                PieceType.ROOK -> drawRook(fillBrush, strokeColor, strokeWidth)
                PieceType.KNIGHT -> drawKnight(fillBrush, strokeColor, strokeWidth, isWhite)
                PieceType.BISHOP -> drawBishop(fillBrush, strokeColor, strokeWidth)
                PieceType.QUEEN -> drawQueen(fillBrush, strokeColor, strokeWidth)
                PieceType.KING -> drawKing(fillBrush, strokeColor, strokeWidth)
            }
        }
    }
}

// ---------------- PAWN ----------------
private fun DrawScope.drawPawn(fill: Brush, stroke: Color, strokeWidth: Float) {
    val w = size.width
    val h = size.height

    // Head sphere
    val headRadius = w * 0.16f
    val headCenter = Offset(w * 0.5f, h * 0.32f)
    drawCircle(brush = fill, radius = headRadius, center = headCenter)
    drawCircle(color = stroke, radius = headRadius, center = headCenter, style = Stroke(strokeWidth))

    // Body & Base path
    val body = Path().apply {
        moveTo(w * 0.40f, h * 0.43f)
        cubicTo(w * 0.44f, h * 0.44f, w * 0.56f, h * 0.44f, w * 0.60f, h * 0.43f)
        cubicTo(w * 0.57f, h * 0.60f, w * 0.63f, h * 0.72f, w * 0.72f, h * 0.80f)
        lineTo(w * 0.76f, h * 0.88f)
        cubicTo(w * 0.76f, h * 0.90f, w * 0.24f, h * 0.90f, w * 0.24f, h * 0.88f)
        lineTo(w * 0.28f, h * 0.80f)
        cubicTo(w * 0.37f, h * 0.72f, w * 0.43f, h * 0.60f, w * 0.40f, h * 0.43f)
        close()
    }
    drawPath(body, brush = fill, style = Fill)
    drawPath(body, color = stroke, style = Stroke(strokeWidth, join = StrokeJoin.Round, cap = StrokeCap.Round))

    // Base trim collar
    drawLine(
        color = stroke,
        start = Offset(w * 0.32f, h * 0.80f),
        end = Offset(w * 0.68f, h * 0.80f),
        strokeWidth = strokeWidth * 0.9f,
        cap = StrokeCap.Round
    )
}

// ---------------- ROOK ----------------
private fun DrawScope.drawRook(fill: Brush, stroke: Color, strokeWidth: Float) {
    val w = size.width
    val h = size.height

    val path = Path().apply {
        // Base
        moveTo(w * 0.20f, h * 0.88f)
        lineTo(w * 0.80f, h * 0.88f)
        lineTo(w * 0.76f, h * 0.78f)
        lineTo(w * 0.70f, h * 0.76f)
        // Tower column up
        lineTo(w * 0.67f, h * 0.42f)
        lineTo(w * 0.76f, h * 0.38f)
        // Crenels / Battlements
        lineTo(w * 0.76f, h * 0.22f)
        lineTo(w * 0.64f, h * 0.22f)
        lineTo(w * 0.64f, h * 0.30f)
        lineTo(w * 0.56f, h * 0.30f)
        lineTo(w * 0.56f, h * 0.22f)
        lineTo(w * 0.44f, h * 0.22f)
        lineTo(w * 0.44f, h * 0.30f)
        lineTo(w * 0.36f, h * 0.30f)
        lineTo(w * 0.36f, h * 0.22f)
        lineTo(w * 0.24f, h * 0.22f)
        lineTo(w * 0.24f, h * 0.38f)
        lineTo(w * 0.33f, h * 0.42f)
        // Tower column down
        lineTo(w * 0.30f, h * 0.76f)
        lineTo(w * 0.24f, h * 0.78f)
        close()
    }
    drawPath(path, brush = fill, style = Fill)
    drawPath(path, color = stroke, style = Stroke(strokeWidth, join = StrokeJoin.Round, cap = StrokeCap.Round))

    // Horizontal ridge lines
    drawLine(
        color = stroke,
        start = Offset(w * 0.26f, h * 0.38f),
        end = Offset(w * 0.74f, h * 0.38f),
        strokeWidth = strokeWidth * 0.8f,
        cap = StrokeCap.Round
    )
    drawLine(
        color = stroke,
        start = Offset(w * 0.27f, h * 0.77f),
        end = Offset(w * 0.73f, h * 0.77f),
        strokeWidth = strokeWidth * 0.8f,
        cap = StrokeCap.Round
    )
}

// ---------------- KNIGHT ----------------
private fun DrawScope.drawKnight(fill: Brush, stroke: Color, strokeWidth: Float, isWhite: Boolean) {
    val w = size.width
    val h = size.height

    val path = Path().apply {
        // Base
        moveTo(w * 0.22f, h * 0.88f)
        lineTo(w * 0.78f, h * 0.88f)
        lineTo(w * 0.74f, h * 0.80f)
        // Back of head & mane
        cubicTo(w * 0.75f, h * 0.65f, w * 0.77f, h * 0.40f, w * 0.66f, h * 0.22f)
        // Ear tip
        lineTo(w * 0.60f, h * 0.16f)
        lineTo(w * 0.54f, h * 0.24f)
        // Forehead
        lineTo(w * 0.44f, h * 0.28f)
        // Snout / Muzzle
        cubicTo(w * 0.32f, h * 0.33f, w * 0.25f, h * 0.42f, w * 0.25f, h * 0.50f)
        // Mouth & Jaw
        lineTo(w * 0.34f, h * 0.53f)
        cubicTo(w * 0.38f, h * 0.54f, w * 0.42f, h * 0.50f, w * 0.45f, h * 0.52f)
        // Neck front curve
        cubicTo(w * 0.38f, h * 0.62f, w * 0.30f, h * 0.70f, w * 0.26f, h * 0.80f)
        close()
    }
    drawPath(path, brush = fill, style = Fill)
    drawPath(path, color = stroke, style = Stroke(strokeWidth, join = StrokeJoin.Round, cap = StrokeCap.Round))

    // Eye dot
    val eyeCenter = Offset(w * 0.46f, h * 0.35f)
    drawCircle(color = stroke, radius = w * 0.035f, center = eyeCenter)

    // Mane ridges
    val manePath = Path().apply {
        moveTo(w * 0.63f, h * 0.32f)
        lineTo(w * 0.55f, h * 0.38f)
        moveTo(w * 0.68f, h * 0.46f)
        lineTo(w * 0.58f, h * 0.52f)
        moveTo(w * 0.70f, h * 0.60f)
        lineTo(w * 0.60f, h * 0.66f)
    }
    drawPath(manePath, color = stroke, style = Stroke(strokeWidth * 0.8f, cap = StrokeCap.Round))
}

// ---------------- BISHOP ----------------
private fun DrawScope.drawBishop(fill: Brush, stroke: Color, strokeWidth: Float) {
    val w = size.width
    val h = size.height

    // Small tip ball
    drawCircle(
        color = stroke,
        radius = w * 0.045f,
        center = Offset(w * 0.5f, h * 0.17f)
    )

    // Body
    val body = Path().apply {
        moveTo(w * 0.5f, h * 0.22f)
        // Right side of head oval
        cubicTo(w * 0.68f, h * 0.26f, w * 0.68f, h * 0.48f, w * 0.58f, h * 0.56f)
        // Stem
        cubicTo(w * 0.58f, h * 0.68f, w * 0.66f, h * 0.76f, w * 0.74f, h * 0.80f)
        lineTo(w * 0.76f, h * 0.88f)
        lineTo(w * 0.24f, h * 0.88f)
        lineTo(w * 0.26f, h * 0.80f)
        cubicTo(w * 0.34f, h * 0.76f, w * 0.42f, h * 0.68f, w * 0.42f, h * 0.56f)
        cubicTo(w * 0.32f, h * 0.48f, w * 0.32f, h * 0.26f, w * 0.5f, h * 0.22f)
        close()
    }
    drawPath(body, brush = fill, style = Fill)
    drawPath(body, color = stroke, style = Stroke(strokeWidth, join = StrokeJoin.Round, cap = StrokeCap.Round))

    // Bishop miter cross slash
    val slash = Path().apply {
        moveTo(w * 0.42f, h * 0.32f)
        lineTo(w * 0.58f, h * 0.44f)
    }
    drawPath(slash, color = stroke, style = Stroke(strokeWidth * 0.9f, cap = StrokeCap.Round))

    // Collar ring
    drawLine(
        color = stroke,
        start = Offset(w * 0.36f, h * 0.58f),
        end = Offset(w * 0.64f, h * 0.58f),
        strokeWidth = strokeWidth * 0.8f,
        cap = StrokeCap.Round
    )
}

// ---------------- QUEEN ----------------
private fun DrawScope.drawQueen(fill: Brush, stroke: Color, strokeWidth: Float) {
    val w = size.width
    val h = size.height

    // Coronet pearls
    val pearlRadius = w * 0.038f
    val pearlPoints = listOf(
        Offset(w * 0.22f, h * 0.28f),
        Offset(w * 0.36f, h * 0.23f),
        Offset(w * 0.50f, h * 0.19f),
        Offset(w * 0.64f, h * 0.23f),
        Offset(w * 0.78f, h * 0.28f)
    )
    for (pt in pearlPoints) {
        drawCircle(brush = fill, radius = pearlRadius, center = pt)
        drawCircle(color = stroke, radius = pearlRadius, center = pt, style = Stroke(strokeWidth * 0.8f))
    }

    // Crown & Gown body
    val body = Path().apply {
        moveTo(w * 0.22f, h * 0.30f)
        lineTo(w * 0.32f, h * 0.38f)
        lineTo(w * 0.36f, h * 0.25f)
        lineTo(w * 0.44f, h * 0.36f)
        lineTo(w * 0.50f, h * 0.21f)
        lineTo(w * 0.56f, h * 0.36f)
        lineTo(w * 0.64f, h * 0.25f)
        lineTo(w * 0.68f, h * 0.38f)
        lineTo(w * 0.78f, h * 0.30f)
        // Torso waist
        cubicTo(w * 0.72f, h * 0.50f, w * 0.62f, h * 0.58f, w * 0.65f, h * 0.76f)
        lineTo(w * 0.76f, h * 0.88f)
        lineTo(w * 0.24f, h * 0.88f)
        lineTo(w * 0.35f, h * 0.76f)
        cubicTo(w * 0.38f, h * 0.58f, w * 0.28f, h * 0.50f, w * 0.22f, h * 0.30f)
        close()
    }
    drawPath(body, brush = fill, style = Fill)
    drawPath(body, color = stroke, style = Stroke(strokeWidth, join = StrokeJoin.Round, cap = StrokeCap.Round))

    // Regal waistband
    drawLine(
        color = stroke,
        start = Offset(w * 0.34f, h * 0.55f),
        end = Offset(w * 0.66f, h * 0.55f),
        strokeWidth = strokeWidth * 0.85f,
        cap = StrokeCap.Round
    )
}

// ---------------- KING ----------------
private fun DrawScope.drawKing(fill: Brush, stroke: Color, strokeWidth: Float) {
    val w = size.width
    val h = size.height

    // Latin Cross on top
    val cross = Path().apply {
        // Vertical
        moveTo(w * 0.5f, h * 0.10f)
        lineTo(w * 0.5f, h * 0.22f)
        // Horizontal
        moveTo(w * 0.42f, h * 0.14f)
        lineTo(w * 0.58f, h * 0.14f)
    }
    drawPath(cross, color = stroke, style = Stroke(strokeWidth * 1.1f, cap = StrokeCap.Round))

    // Imperial Crown & Body
    val body = Path().apply {
        moveTo(w * 0.44f, h * 0.22f)
        lineTo(w * 0.56f, h * 0.22f)
        // Curved crown lobes
        cubicTo(w * 0.74f, h * 0.23f, w * 0.78f, h * 0.42f, w * 0.66f, h * 0.50f)
        // Neck/Stem
        cubicTo(w * 0.60f, h * 0.58f, w * 0.64f, h * 0.70f, w * 0.74f, h * 0.80f)
        lineTo(w * 0.78f, h * 0.88f)
        lineTo(w * 0.22f, h * 0.88f)
        lineTo(w * 0.26f, h * 0.80f)
        cubicTo(w * 0.36f, h * 0.70f, w * 0.40f, h * 0.58f, w * 0.34f, h * 0.50f)
        cubicTo(w * 0.22f, h * 0.42f, w * 0.26f, h * 0.23f, w * 0.44f, h * 0.22f)
        close()
    }
    drawPath(body, brush = fill, style = Fill)
    drawPath(body, color = stroke, style = Stroke(strokeWidth, join = StrokeJoin.Round, cap = StrokeCap.Round))

    // Crown arch divider
    val arch = Path().apply {
        moveTo(w * 0.35f, h * 0.38f)
        cubicTo(w * 0.44f, h * 0.28f, w * 0.56f, h * 0.28f, w * 0.65f, h * 0.38f)
    }
    drawPath(arch, color = stroke, style = Stroke(strokeWidth * 0.8f, cap = StrokeCap.Round))

    // Collar band
    drawLine(
        color = stroke,
        start = Offset(w * 0.32f, h * 0.54f),
        end = Offset(w * 0.68f, h * 0.54f),
        strokeWidth = strokeWidth * 0.85f,
        cap = StrokeCap.Round
    )
}
