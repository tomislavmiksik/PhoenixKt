package dev.tomislavmiksik.phoenix.ui.platform.base.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import dev.tomislavmiksik.phoenix.ui.base.BaseViewModel
import kotlinx.coroutines.flow.collectLatest

/**
 * Composable function that collects events from a [BaseViewModel] and handles them.
 * Events are only collected when the lifecycle is at least STARTED.
 *
 * This follows the Bitwarden pattern for handling one-shot events from ViewModels.
 *
 * @param viewModel The ViewModel to collect events from
 * @param handler The lambda to handle each event
 */
@Composable
fun <S, E, A> EventsEffect(
    viewModel: BaseViewModel<S, E, A>,
    handler: (E) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(viewModel, lifecycleOwner) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.eventFlow.collectLatest { event ->
                handler(event)
            }
        }
    }
}
