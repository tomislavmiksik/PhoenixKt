package dev.tomislavmiksik.peak.ui.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import dev.tomislavmiksik.peak.R
import dev.tomislavmiksik.peak.core.util.extensions.toMonthYearStringShort
import dev.tomislavmiksik.peak.ui.home.CalendarProgressTrackerData
import dev.tomislavmiksik.peak.ui.theme.PeakTheme
import java.time.DayOfWeek
import java.time.LocalDate

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CalendarProgressTracker(
    data: CalendarProgressTrackerData,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(dimensionResource(R.dimen.spacing_sm)),
        horizontalAlignment = Alignment.Start,
    ) {
        Text(
            text = data.stepsByDate.keys.firstOrNull()?.toMonthYearStringShort() ?: "",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(
            modifier = modifier
                .height(dimensionResource(R.dimen.spacing_sm))
        )
        Row(
            modifier = modifier.padding(bottom = dimensionResource(R.dimen.spacing_lg)),
            horizontalArrangement = Arrangement.spacedBy(
                dimensionResource(R.dimen.spacing_sm),
            )
        ) {
            FlowRow(
                modifier = modifier,
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_xxs)),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_xxs)),
                maxItemsInEachRow = 7
            ) {
                val stepsByDate = data.stepsByDate
                DayOfWeek.entries.forEach {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.small
                            )
                            .size(dimensionResource(R.dimen.calendar_day_size))
                            .clip(shape = MaterialTheme.shapes.small)
                    ) {
                        Text(
                            text = it.name.first().toString(),
                            style = MaterialTheme.typography.bodySmall.copy(
                                textAlign = TextAlign.Center
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                repeat(LocalDate.now().withDayOfMonth(1).dayOfWeek.value - 1) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .background(
                                color = Color.Transparent,
                                shape = MaterialTheme.shapes.small
                            )
                            .size(dimensionResource(R.dimen.calendar_day_size))
                            .clip(shape = MaterialTheme.shapes.small)
                    ) {}
                }
                stepsByDate.forEach {
                    CalendarDay(dateData = it)
                }
            }
        }
    }
}

@Composable
fun stepCountToColor(steps: Long, goal: Int = 10000): Color {
    val primary = MaterialTheme.colorScheme.primary
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant

    val ratio = (steps.toFloat() / goal).coerceIn(0f, 1f)
    return when {
        ratio >= 1.0f -> primary
        ratio >= 0.5f -> primary.copy(alpha = 0.6f)
        ratio > 0f -> primary.copy(alpha = 0.3f)
        else -> onSurfaceVariant.copy(alpha = 0.2f)
    }
}


@Composable
fun CalendarDay(dateData: Map.Entry<LocalDate, Long>) {
    val isToday = dateData.key == LocalDate.now()
    val calendarDaySize = dimensionResource(R.dimen.calendar_day_size)
    val borderWidthToday = dimensionResource(R.dimen.border_width_md)
    val borderWidthDefault = dimensionResource(R.dimen.border_width_sm)
    val cornerRadiusSm = dimensionResource(R.dimen.corner_radius_sm)

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .background(
                color = stepCountToColor(dateData.value),
                shape = MaterialTheme.shapes.small
            )
            .size(calendarDaySize)
            .clip(shape = MaterialTheme.shapes.small)
            .border(
                width = if (isToday) borderWidthToday else borderWidthDefault,
                color = if (isToday)
                    MaterialTheme.colorScheme.inverseSurface
                else
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                shape = MaterialTheme.shapes.small,
            )
    ) {
        if (isToday) {
            val currentDayColor = MaterialTheme.colorScheme.inverseSurface
            Canvas(
                modifier = Modifier.fillMaxSize()
            ) {
                val r = cornerRadiusSm.toPx()
                val triangleHeight = size.height * 0.45f


                val path = Path().apply {
                    moveTo(r, size.height)
                    lineTo(size.width / 2, size.height - triangleHeight)
                    lineTo(size.width - r, size.height)
                    arcTo(
                        rect = Rect(
                            left = size.width - r * 2,
                            top = size.height - r * 2,
                            right = size.width,
                            bottom = size.height
                        ),
                        startAngleDegrees = 0f,
                        sweepAngleDegrees = 90f,
                        forceMoveTo = false
                    )
                    lineTo(r, size.height)
                    arcTo(
                        rect = Rect(
                            left = 0f,
                            top = size.height - r * 2,
                            right = r * 2,
                            bottom = size.height
                        ),
                        startAngleDegrees = 90f,
                        sweepAngleDegrees = 90f,
                        forceMoveTo = false
                    )
                    close()
                }
                drawPath(path, color = currentDayColor)
            }
        }
    }
}

//region Previews
@Preview(showBackground = true)
@Composable
private fun CalendarProgressTracker_preview() {
    val today = LocalDate.now()
    val sampleSteps = (1..today.dayOfMonth).associate { day ->
        today.withDayOfMonth(day) to (5000L + (day * 500L))
    }
    val sampleCalories = (1..today.dayOfMonth).associate { day ->
        today.withDayOfMonth(day) to (1500L + (day * 50L))
    }
    PeakTheme {
        CalendarProgressTracker(
            data = CalendarProgressTrackerData(
                stepsByDate = sampleSteps,
                averageStepsPerDay = 8500L,
                caloriesSpentByDate = sampleCalories,
                averageCaloriesSpentPerDay = 1800L
            )
        )
    }
}
//endregion
