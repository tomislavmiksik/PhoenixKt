package dev.tomislavmiksik.peak.ui.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.tooling.preview.Preview
import dev.tomislavmiksik.peak.R
import dev.tomislavmiksik.peak.ui.theme.PeakTheme

@Composable
fun StatCard(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    title: String,
    value: String,
    subtitle: String,
) {
    val cornerRadius = dimensionResource(R.dimen.corner_radius_lg)
    val elevation = dimensionResource(R.dimen.elevation_md)
    val borderWidth = dimensionResource(R.dimen.border_width_sm)

    Column(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = RoundedCornerShape(cornerRadius)
            )
            .border(
                width = borderWidth,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f),
                shape = RoundedCornerShape(cornerRadius)
            )
            .background(MaterialTheme.colorScheme.background)
            .padding(dimensionResource(R.dimen.padding_card))
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = MaterialTheme.shapes.small
                )
                .padding(dimensionResource(R.dimen.padding_component))
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(dimensionResource(R.dimen.icon_sm))
            )
        }
        Column(
            modifier = Modifier.padding(horizontal = dimensionResource(R.dimen.spacing_sm))
        ) {
            Spacer(modifier = Modifier.height(dimensionResource(R.dimen.spacing_sm)))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

//region Previews
@Preview(showBackground = true)
@Composable
private fun StatCard_preview() {
    PeakTheme {
        StatCard(
            icon = Icons.Default.Favorite,
            title = "Heart Rate",
            value = "72",
            subtitle = "bpm resting"
        )
    }
}
//endregion
