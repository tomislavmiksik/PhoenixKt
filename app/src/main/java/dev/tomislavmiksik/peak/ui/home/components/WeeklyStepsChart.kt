package dev.tomislavmiksik.peak.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.tomislavmiksik.peak.R
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WeeklyStepsChart(
    stepsByDate: Map<LocalDate, Long>,
    modifier: Modifier = Modifier,
) {
    val today = LocalDate.now()
    val startOfWeek = today.with(DayOfWeek.MONDAY)
    val weekDays = (0..6).map { startOfWeek.plusDays(it.toLong()) }

    val weekSteps = weekDays.map { date ->
        date to (stepsByDate[date] ?: 0L)
    }

    val maxSteps = weekSteps.maxOfOrNull { it.second }?.coerceAtLeast(1L) ?: 10000L
    val totalSteps = weekSteps.sumOf { it.second }
    val daysWithData = weekSteps.count { it.second > 0 }
    val avgSteps = if (daysWithData > 0) totalSteps / daysWithData else 0L

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .padding(dimensionResource(R.dimen.spacing_lg))
    ) {
        Text(
            text = stringResource(R.string.home_this_week),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimensionResource(R.dimen.chart_height)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            weekSteps.forEach { (date, steps) ->
                DayBar(
                    dayLabel = date.dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.getDefault()),
                    steps = steps,
                    maxSteps = maxSteps,
                    isToday = date == today,
                    isFuture = date.isAfter(today),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(R.string.home_total, formatSteps(totalSteps)),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = stringResource(R.string.home_avg, formatSteps(avgSteps)),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun DayBar(
    dayLabel: String,
    steps: Long,
    maxSteps: Long,
    isToday: Boolean,
    isFuture: Boolean,
    modifier: Modifier = Modifier,
) {
    val barHeight = if (steps > 0) {
        (steps.toFloat() / maxSteps * 80).dp
    } else {
        4.dp
    }

    val barColor = when {
        isFuture -> MaterialTheme.colorScheme.surface
        isToday -> MaterialTheme.colorScheme.primary
        steps > 0 -> MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
        else -> MaterialTheme.colorScheme.surface
    }

    Column(
        modifier = modifier.padding(horizontal = dimensionResource(R.dimen.spacing_xxs)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (steps > 0 && !isFuture) {
            Text(
                text = formatStepsShort(steps),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_xs)))
        Box(
            modifier = Modifier
                .width(dimensionResource(R.dimen.chart_bar_width))
                .height(barHeight)
                .clip(MaterialTheme.shapes.small)
                .background(barColor)
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))
        Text(
            text = dayLabel,
            style = MaterialTheme.typography.labelSmall,
            color = if (isToday) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
    }
}

private fun formatSteps(steps: Long): String {
    return when {
        steps >= 1000 -> "%,d".format(steps)
        else -> steps.toString()
    }
}

private fun formatStepsShort(steps: Long): String {
    return when {
        steps >= 1000 -> "%.1fk".format(steps / 1000.0)
        else -> steps.toString()
    }
}
