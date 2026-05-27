package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Neon Palette Constants
object NeonTheme {
    val Background = Color(0xFF0C1324)
    val SurfaceDark = Color(0xFF070D1F)
    val CardBackground = Color(0xCC191F31)
    val OutlineCyan = Color(0xFF00DBE9)
    val OutlineLime = Color(0xFFABD600)
    val OutlineMagenta = Color(0xFFFFACE8)
    val TextPrimary = Color(0xFFDCE1FB)
    val TextVariant = Color(0xFFB9CACB)
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    borderColor: Color = Color(0x3300DBE9),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = NeonTheme.CardBackground),
        border = BorderStroke(1.dp, borderColor)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            content = content
        )
    }
}

@Composable
fun GlowButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    color: Color = NeonTheme.OutlineCyan,
    enabled: Boolean = true,
    fontSize: androidx.compose.ui.unit.TextUnit = 14.sp
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(52.dp)
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(8.dp)),
        colors = ButtonDefaults.buttonColors(
            containerColor = color.copy(alpha = 0.15f),
            contentColor = color
        ),
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
    ) {
        Text(
            text = text.uppercase(),
            fontSize = fontSize,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.5.sp
        )
    }
}

@Composable
fun NeonGauge(
    value: Float,
    maxValue: Float,
    title: String,
    unit: String,
    modifier: Modifier = Modifier,
    color: Color = NeonTheme.OutlineCyan
) {
    val progress = (value / maxValue).coerceIn(0f, 1f)
    val sweep = progress * 270f

    Box(
        modifier = modifier.size(160.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(140.dp)) {
            val strokeWidth = 12.dp.toPx()
            val activeSweep = sweep.coerceIn(0f, 270f)

            // Neon glow layers (outer → inner → core)
            val glowParams = listOf(
                30.dp.toPx() to 0.04f,
                24.dp.toPx() to 0.08f,
                18.dp.toPx() to 0.18f,
                14.dp.toPx() to 0.35f
            )
            for ((width, alpha) in glowParams) {
                drawArc(
                    color = color.copy(alpha = alpha),
                    startAngle = -225f,
                    sweepAngle = activeSweep,
                    useCenter = false,
                    style = Stroke(width = width, cap = StrokeCap.Round)
                )
            }

            // Background arc
            drawArc(
                color = Color(0xFF151B2D),
                startAngle = -225f,
                sweepAngle = 270f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Value arc
            drawArc(
                color = color,
                startAngle = -225f,
                sweepAngle = activeSweep,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )
        }
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = String.format("%.1f", value),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = NeonTheme.TextVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = unit,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = color,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun SimulatedTelemetryChart(
    points: List<Float>,
    modifier: Modifier = Modifier,
    color: Color = NeonTheme.OutlineCyan,
    showPlaceholder: Boolean = false,
    placeholderLabel: String = "Aguardando dados..."
) {
    if (showPlaceholder || points.isEmpty()) {
        Box(
            modifier = modifier,
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "───", fontSize = 24.sp, color = Color(0xFF2E3447), letterSpacing = 4.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = placeholderLabel, fontSize = 11.sp, color = NeonTheme.TextVariant)
            }
        }
        return
    }

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val pad = 4.dp.toPx()
        val chartW = w - pad * 2
        val chartH = h - pad * 2

        val maxVal = points.maxOrNull() ?: 100f
        val minVal = points.minOrNull() ?: 0f
        val range = (maxVal - minVal).coerceAtLeast(1f)
        val n = points.size

        // --- Grid lines (horizontal only, subtle) ---
        for (i in 0..3) {
            val y = pad + (chartH / 3) * i
            drawLine(
                color = Color(0x0F849495),
                start = Offset(pad, y),
                end = Offset(w - pad, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        if (n < 2) return@Canvas

        val xStep = chartW / (n - 1).coerceAtLeast(1)

        // Helper to map data point to canvas coordinate
        fun yCoord(value: Float): Float {
            return pad + chartH - ((value - minVal) / range * chartH)
        }

        // --- Area path (closed to bottom) ---
        val areaPath = Path()
        val firstX = pad
        val firstY = yCoord(points[0])
        val lastX = pad + (n - 1) * xStep

        areaPath.moveTo(firstX, pad + chartH)
        areaPath.lineTo(firstX, firstY)

        for (i in 1 until n) {
            val x = pad + i * xStep
            val y = yCoord(points[i])
            val prevX = pad + (i - 1) * xStep
            val prevY = yCoord(points[i - 1])
            val cx = (prevX + x) / 2f
            areaPath.cubicTo(cx, prevY, cx, y, x, y)
        }
        areaPath.lineTo(lastX, pad + chartH)
        areaPath.close()

        drawPath(
            path = areaPath,
            brush = Brush.verticalGradient(
                colors = listOf(color.copy(alpha = 0.20f), color.copy(alpha = 0.0f)),
                startY = pad,
                endY = pad + chartH
            )
        )

        // --- Line (glow + main) ---
        val linePath = Path()
        linePath.moveTo(firstX, firstY)
        for (i in 1 until n) {
            val x = pad + i * xStep
            val y = yCoord(points[i])
            val prevX = pad + (i - 1) * xStep
            val prevY = yCoord(points[i - 1])
            val cx = (prevX + x) / 2f
            linePath.cubicTo(cx, prevY, cx, y, x, y)
        }

        // Glow
        drawPath(
            path = linePath,
            color = color.copy(alpha = 0.25f),
            style = Stroke(width = 6.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
        // Main line
        drawPath(
            path = linePath,
            color = color,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
        )
    }
}

@Composable
fun CustomizableToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val color = if (checked) NeonTheme.OutlineLime else Color(0xFF2E3447)
    val alignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
    val glowColor = if (checked) NeonTheme.OutlineLime.copy(alpha = 0.4f) else Color.Transparent

    Box(
        modifier = modifier
            .width(56.dp)
            .height(28.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF070D1F))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .drawBehind {
                drawCircle(
                    color = glowColor,
                    radius = size.height * 0.7f,
                    center = if (checked) Offset(size.width - size.height / 2, size.height / 2) else Offset(size.height / 2, size.height / 2)
                )
            }
            .clickable {
                onCheckedChange(!checked)
            }
            .padding(4.dp),
        contentAlignment = alignment
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (checked) NeonTheme.OutlineLime else Color(0xFF849495))
        )
    }
}
