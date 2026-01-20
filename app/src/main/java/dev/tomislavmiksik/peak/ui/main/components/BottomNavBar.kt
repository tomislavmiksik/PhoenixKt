package dev.tomislavmiksik.peak.ui.main.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.DirectionsRun
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import dev.tomislavmiksik.peak.R
import dev.tomislavmiksik.peak.ui.main.ActivityRoute
import dev.tomislavmiksik.peak.ui.main.HomeRoute
import dev.tomislavmiksik.peak.ui.main.ProgressRoute

@Composable
fun BottomNavBar(
    currentDestination: BottomNavDestination,
    onDestinationSelected: (BottomNavDestination) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier.shadow(elevation = dimensionResource(R.dimen.elevation_md)),
        containerColor = MaterialTheme.colorScheme.background,
        windowInsets = WindowInsets(0)
    ) {
        BottomNavDestination.entries.forEach { destination ->
            val label = stringResource(destination.labelRes)
            NavigationBarItem(
                selected = currentDestination == destination,
                alwaysShowLabel = true,
                onClick = { onDestinationSelected(destination) },
                colors = NavigationBarItemDefaults.colors().copy(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    selectedIndicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                ),
                icon = {
                    Icon(
                        imageVector = destination.icon,
                        contentDescription = label
                    )
                },
                label = { Text(label) }
            )
        }
    }
}

enum class BottomNavDestination(
    val route: Any,
    val icon: ImageVector,
    @StringRes val labelRes: Int,
) {
    Home(
        route = HomeRoute,
        icon = Icons.Default.Home,
        labelRes = R.string.nav_home
    ),
    Activity(
        route = ActivityRoute,
        icon = Icons.AutoMirrored.Filled.DirectionsRun,
        labelRes = R.string.nav_activity
    ),
    Progress(
        route = ProgressRoute,
        icon = Icons.AutoMirrored.Filled.TrendingUp,
        labelRes = R.string.nav_progress
    )
}
