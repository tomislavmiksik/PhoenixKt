package dev.tomislavmiksik.peak.ui.home.components

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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import dev.tomislavmiksik.peak.R
import dev.tomislavmiksik.peak.ui.base.components.PeakBox
import dev.tomislavmiksik.peak.ui.home.CalendarProgressTrackerData

@Composable
fun HomeHeroSection(
    modifier: Modifier = Modifier,
    steps: Long,
    goal: Long = 10_000L,
    calendarData: CalendarProgressTrackerData,
) {
    val progress = (steps.toFloat() / goal).coerceIn(0f, 1f)

    val messageRes = when {
        progress == 0f -> R.string.peak_message_start
        progress < 0.25f -> R.string.peak_message_25
        progress < 0.50f -> R.string.peak_message_50
        progress < 0.75f -> R.string.peak_message_75
        progress < 1f -> R.string.peak_message_99
        else -> R.string.peak_message_100
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(messageRes),
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_lg)))

        PeakBox(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CalendarProgressTracker(
                    data = calendarData,
                )
                Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_sm)))
                Column {
                    DailyPeakMountain(
                        modifier = Modifier.padding(end = dimensionResource(R.dimen.spacing_sm)),
                        steps = steps,
                        goal = goal,
                    )
                    Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))
                    Text(
                        text = stringResource(
                            R.string.peak_steps_progress,
                            formatStepsWithCommas(steps),
                            formatStepsWithCommas(goal)
                        ),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

private fun formatStepsWithCommas(steps: Long): String {
    return "%,d".format(steps)
}
