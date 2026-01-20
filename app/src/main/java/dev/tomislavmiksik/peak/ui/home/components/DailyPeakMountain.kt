package dev.tomislavmiksik.peak.ui.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import dev.tomislavmiksik.peak.ui.theme.ForestGreen

@Composable
fun DailyPeakMountain(
    modifier: Modifier = Modifier,
    steps: Long,
    goal: Long = 10_000L,
) {
    val progress = (steps.toFloat() / goal).coerceIn(0f, 1f)

    Canvas(
        modifier = modifier.aspectRatio(0.9f)
    ) {
        val w = size.width
        val h = size.height

        val mountainPath = Path().apply {
            // Start bottom left
            moveTo(0f, h)

            // Up to small left peak
            lineTo(w * 0.15f, h * 0.55f)

            // Down to valley
            lineTo(w * 0.25f, h * 0.75f)

            // Up to main peak
            lineTo(w * 0.55f, h * 0.08f)

            // Small ridge/shoulder
            lineTo(w * 0.65f, h * 0.15f)

            // Down to right base
            lineTo(w, h * 0.85f)

            // Close along bottom
            lineTo(w, h)
            close()
        }

        // Unfilled mountain
        drawPath(mountainPath, color = ForestGreen.copy(alpha = 0.2f))

        // Fill from bottom
        if (progress > 0f) {
            val fillTop = h - (h * progress)

            clipPath(mountainPath) {
                drawRect(
                    color = ForestGreen,
                    topLeft = Offset(0f, fillTop),
                    size = Size(w, h - fillTop)
                )
            }
        }
    }
}
