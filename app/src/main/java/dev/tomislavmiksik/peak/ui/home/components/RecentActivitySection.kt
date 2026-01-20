package dev.tomislavmiksik.peak.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Pool
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import dev.tomislavmiksik.peak.R

data class RecentActivity(
    val type: ActivityType,
    val title: String,
    val timeLabel: String,
    val details: String,
)

enum class ActivityType(val icon: ImageVector, val color: Color) {
    Running(Icons.Default.DirectionsRun, Color(0xFF4CAF50)),
    Workout(Icons.Default.FitnessCenter, Color(0xFFFF5722)),
    Cycling(Icons.Default.DirectionsBike, Color(0xFF2196F3)),
    Swimming(Icons.Default.Pool, Color(0xFF00BCD4)),
    Yoga(Icons.Default.SelfImprovement, Color(0xFF9C27B0)),
}

@Composable
fun RecentActivitySection(
    activities: List<RecentActivity>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.home_recent_activity),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_md)))

        if (activities.isEmpty()) {
            Text(
                text = "No recent activities",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_sm))) {
                activities.take(3).forEach { activity ->
                    ActivityCard(activity = activity)
                }
            }
        }
    }
}

@Composable
private fun ActivityCard(
    activity: RecentActivity,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium
            )
            .padding(dimensionResource(R.dimen.padding_card)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = activity.type.icon,
            contentDescription = activity.title,
            tint = activity.type.color,
            modifier = Modifier.size(dimensionResource(R.dimen.icon_md))
        )
        Spacer(modifier = Modifier.width(dimensionResource(R.dimen.spacing_md)))
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = activity.timeLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = activity.details,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
