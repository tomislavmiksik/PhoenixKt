package dev.tomislavmiksik.phoenix.ui.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.tomislavmiksik.phoenix.R
import dev.tomislavmiksik.phoenix.core.util.extensions.toMonthYearStringShort
import dev.tomislavmiksik.phoenix.ui.dashboard.CalendarProgressTrackerData
import java.time.DayOfWeek
import java.time.LocalDate

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CalendarProgressTracker(
    data: CalendarProgressTrackerData,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    ) {
        Text(
            text = LocalDate.now().toMonthYearStringShort(),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(
                start = 16.dp,
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.padding(
                bottom = 16.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FlowRow(
                modifier = modifier
                    .fillMaxWidth(0.6f)
                    .padding(
                        start = 16.dp,
                    ),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
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
                            .size(28.dp)
                            .clip(
                                shape = MaterialTheme.shapes.small
                            )
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
                for (i in 1 until LocalDate.now().withDayOfMonth(1).dayOfWeek.value) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.background,
                                shape = MaterialTheme.shapes.small
                            )
                            .size(28.dp)
                            .clip(
                                shape = MaterialTheme.shapes.small
                            )
                    ) {}
                }
                stepsByDate.forEach {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .background(
                                color = stepCountToColor(it.value),
                                shape = MaterialTheme.shapes.small
                            )
                            .size(28.dp)
                            .clip(
                                shape = MaterialTheme.shapes.small
                            )
                    ) {
                        //TODO: decide whether to show day numbers or not
//                        Text(
//                            text = it.key.dayOfMonth.toString(),
//                            style = MaterialTheme.typography.bodySmall.copy(
//                                textAlign = TextAlign.Center,
//                            ),
//                            color = MaterialTheme.colorScheme.onSurface,
//                        )
                    }
                }
            }
            Column {
                Text(
                    text = stringResource(R.string.dashboard_average_steps_per_day),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = data.averageStepsPerDay.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
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