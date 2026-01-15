package dev.tomislavmiksik.phoenix.ui.main.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import dev.tomislavmiksik.phoenix.R
import dev.tomislavmiksik.phoenix.ui.main.DashboardRoute
import dev.tomislavmiksik.phoenix.ui.main.ProfileRoute

enum class BottomNavDestination(
    val route: Any,
    val icon: ImageVector,
    @StringRes val labelRes: Int
) {
    Dashboard(
        route = DashboardRoute,
        icon = Icons.Default.Home,
        labelRes = R.string.nav_dashboard
    ),
    Profile(
        route = ProfileRoute,
        icon = Icons.Default.Person,
        labelRes = R.string.nav_profile
    )
}

@Composable
fun BottomNavBar(
    currentDestination: BottomNavDestination,
    onDestinationSelected: (BottomNavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.shadow(elevation = 8.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        windowInsets = WindowInsets(0.dp)
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
                    selectedIndicatorColor = Color.Transparent,
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
