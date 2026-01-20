@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults.Indicator
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.tomislavmiksik.peak.R
import dev.tomislavmiksik.peak.ui.base.EventsEffect
import dev.tomislavmiksik.peak.ui.home.components.HomeHeroSection
import dev.tomislavmiksik.peak.ui.home.components.RecentActivitySection
import dev.tomislavmiksik.peak.ui.home.components.TodaySection
import dev.tomislavmiksik.peak.ui.home.components.WeeklyStepsChart

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
            ) { CircularProgressIndicator() }
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
            Scaffold(
                containerColor = Color.White,
                topBar = {
                    TopAppBar(
                        windowInsets = WindowInsets(top = 0.dp, bottom = 0.dp),
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.background
                        ),
                        title = {
                            Text(
                                text = stringResource(R.string.home_title),
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.primary
                            )
                        },
                        actions = {
                            HomeHeader()
                        },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
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
                            Indicator(
                                modifier = Modifier.align(Alignment.TopCenter),
                                isRefreshing = state.isRefreshing,
                                state = pullToRefreshState,
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
                                    modifier = Modifier.fillMaxWidth()
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
                                WeeklyStepsChart(
                                    stepsByDate = state.stepsByDate,
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

@Composable
private fun HomeHeader(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopEnd
    ) {
        IconButton(
            onClick = { /* TODO: Navigate to profile screen */ }
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = stringResource(R.string.profile_button)
            )
        }
    }
}
