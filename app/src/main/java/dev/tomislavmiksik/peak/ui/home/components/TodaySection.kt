package dev.tomislavmiksik.peak.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import dev.tomislavmiksik.peak.R
import dev.tomislavmiksik.peak.ui.theme.PeakTheme

@Composable
fun TodaySection(
    sleepMinutes: Long,
    sleepGoalMinutes: Long = 480, // 8 hours default
    heartRate: Long,
    calories: Double,
    caloriesGoal: Double = 2500.0,
    activeMinutes: Long,
    activeGoalMinutes: Long = 60,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.home_today),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm))
        ) {
            StatCard(
                icon = Icons.Default.Bedtime,
                title = stringResource(R.string.home_sleep),
                value = formatSleepDuration(sleepMinutes),
                subtitle = "",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = Icons.Default.Favorite,
                title = stringResource(R.string.home_heart_rate),
                value = "$heartRate bpm",
                subtitle = stringResource(R.string.home_resting),
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm))
        ) {
            StatCard(
                icon = Icons.Default.LocalFireDepartment,
                title = stringResource(R.string.calories_today),
                value = "%,d".format(calories.toInt()),
                subtitle = stringResource(R.string.home_burned),
                modifier = Modifier.weight(1f)
            )
            StatCard(
                icon = Icons.AutoMirrored.Filled.DirectionsRun,
                title = stringResource(R.string.home_active),
                value = "$activeMinutes min",
                subtitle = stringResource(R.string.home_active_today),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

private fun formatSleepDuration(minutes: Long): String {
    val hours = minutes / 60
    val mins = minutes % 60
    return if (hours > 0) {
        "${hours}h ${mins}m"
    } else {
        "${mins}m"
    }
}

//region Previews
@Preview(showBackground = true)
@Composable
private fun TodaySection_preview() {
    PeakTheme {
        TodaySection(
            sleepMinutes = 420,
            heartRate = 72,
            calories = 1850.0,
            activeMinutes = 45
        )
    }
}
//endregion
