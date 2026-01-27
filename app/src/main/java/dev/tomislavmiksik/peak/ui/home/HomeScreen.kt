@file:OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)

package dev.tomislavmiksik.peak.ui.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.LoadingIndicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.tomislavmiksik.peak.R
import dev.tomislavmiksik.peak.ui.base.EventsEffect
import dev.tomislavmiksik.peak.ui.home.components.ActivityType
import dev.tomislavmiksik.peak.ui.home.components.HomeHeroSection
import dev.tomislavmiksik.peak.ui.home.components.RecentActivity
import dev.tomislavmiksik.peak.ui.home.components.RecentActivitySection
import dev.tomislavmiksik.peak.ui.home.components.TodaySection
import dev.tomislavmiksik.peak.ui.theme.PeakTheme
import java.time.LocalDate

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToAddEntry: () -> Unit = {},
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel) { event ->
        when (event) {
            is HomeEvent.NavigateToAddEntry -> onNavigateToAddEntry()
        }
    }

    HomeContent(
        state = state,
        onRefresh = { viewModel.trySendAction(HomeAction.RefreshData) },
        modifier = modifier
    )
}

@Composable
private fun HomeContent(
    state: HomeState,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        state.isLoading -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { LoadingIndicator() }
        }

        state.error != null -> {
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.error,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }

        else -> {
            val pullToRefreshState = rememberPullToRefreshState()
            val scrollBehavior =
                TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
            val progress = (state.steps.toFloat() / 10000).coerceIn(0f, 1f)
            val messageRes = when {
                progress == 0f -> R.string.peak_message_start
                progress < 0.25f -> R.string.peak_message_25
                progress < 0.50f -> R.string.peak_message_50
                progress < 0.75f -> R.string.peak_message_75
                progress < 1f -> R.string.peak_message_99
                else -> R.string.peak_message_100
            }
            Scaffold(
                contentWindowInsets = TopAppBarDefaults.windowInsets,
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = stringResource(messageRes),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        actions = {
                            IconButton(
                                onClick = { /* TODO: Navigate to profile screen */ }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = stringResource(R.string.profile_button)
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors().copy(
                            scrolledContainerColor = MaterialTheme.colorScheme.background
                        ),
                        scrollBehavior = scrollBehavior,
                        windowInsets = WindowInsets(top = 0.dp, bottom = 0.dp),
                    )
                },
                modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            ) { it ->
                Surface(
                    modifier = Modifier
                        .padding(top = it.calculateTopPadding())
                ) {
                    PullToRefreshBox(
                        isRefreshing = state.isRefreshing,
                        onRefresh = onRefresh,
                        modifier = modifier.fillMaxSize(),
                        state = pullToRefreshState,
                        indicator = {
                            LoadingIndicator(
                                state = pullToRefreshState,
                                modifier = Modifier.align(Alignment.TopCenter),
                                isRefreshing = state.isRefreshing,
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = dimensionResource(R.dimen.padding_screen)),
                            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.spacing_xl))
                        ) {
                            item {
                                HomeHeroSection(
                                    steps = state.steps,
                                    goal = 10_000L,
                                    calendarData = state.calendarProgressTrackerData,
                                    modifier = Modifier
                                        .padding(top = dimensionResource(R.dimen.spacing_lg))
                                        .fillMaxWidth()
                                )
                            }

                            item {
                                TodaySection(
                                    sleepMinutes = state.sleepDurationMinutes,
                                    heartRate = state.heartRate,
                                    calories = state.activeCalories,
                                    activeMinutes = state.exerciseDurationMinutes,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            item {
                                RecentActivitySection(
                                    activities = state.recentActivities,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = dimensionResource(R.dimen.padding_bottom_nav))
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

//region Previews
@Preview(showBackground = true)
@Composable
private fun HomeContent_preview() {
    val today = LocalDate.now()
    val sampleSteps = (1..today.dayOfMonth).associate { day ->
        today.withDayOfMonth(day) to (5000L + (day * 500L))
    }
    val sampleCalories = (1..today.dayOfMonth).associate { day ->
        today.withDayOfMonth(day) to (1500L + (day * 50L))
    }
    PeakTheme {
        HomeContent(
            state = HomeState(
                isLoading = false,
                steps = 7523,
                sleepDurationMinutes = 420,
                heartRate = 72,
                activeCalories = 1850.0,
                exerciseDurationMinutes = 45,
                stepsByDate = sampleSteps,
                caloriesByDate = sampleCalories,
                dateRange = today.minusDays(30) to today,
                recentActivities = listOf(
                    RecentActivity(
                        type = ActivityType.Running,
                        title = "Morning Run",
                        timeLabel = "Today, 7:30 AM",
                        details = "5.2 km • 32 min"
                    ),
                    RecentActivity(
                        type = ActivityType.Workout,
                        title = "Strength Training",
                        timeLabel = "Yesterday",
                        details = "45 min • 320 kcal"
                    )
                )
            ),
            onRefresh = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContent_loading_preview() {
    PeakTheme {
        HomeContent(
            state = HomeState(
                isLoading = true,
                dateRange = LocalDate.now().minusDays(30) to LocalDate.now()
            ),
            onRefresh = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun HomeContent_error_preview() {
    PeakTheme {
        HomeContent(
            state = HomeState(
                isLoading = false,
                error = "Failed to load health data",
                dateRange = LocalDate.now().minusDays(30) to LocalDate.now()
            ),
            onRefresh = {}
        )
    }
}
//endregion
