package dev.tomislavmiksik.phoenix

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.health.connect.client.PermissionController
import dagger.hilt.android.AndroidEntryPoint
import dev.tomislavmiksik.phoenix.ui.platform.rootnav.RootNavScreen
import dev.tomislavmiksik.phoenix.ui.theme.PhoenixTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var pendingPermissionCallback: ((Set<String>) -> Unit)? = null

    private val permissionsLauncher = registerForActivityResult(
        PermissionController.createRequestPermissionResultContract()
    ) { granted ->
        pendingPermissionCallback?.invoke(granted)
        pendingPermissionCallback = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.WHITE,
                android.graphics.Color.WHITE
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.WHITE,
                android.graphics.Color.WHITE
            )
        )
        setContent {
            PhoenixTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        RootNavScreen(
                            onLaunchPermissionRequest = { permissions, onResult ->
                                pendingPermissionCallback = onResult
                                permissionsLauncher.launch(permissions)
                            }
                        )
                    }
                }
            }
        }
    }
}
