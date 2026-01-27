package dev.tomislavmiksik.peak.ui.home

import android.util.Log
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.tomislavmiksik.peak.core.data.local.entity.HealthSnapshot
import dev.tomislavmiksik.peak.core.domain.repository.HealthConnectRepository
import dev.tomislavmiksik.peak.ui.base.BaseViewModel
import dev.tomislavmiksik.peak.ui.home.components.RecentActivity
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val healthConnectRepository: HealthConnectRepository,
) : BaseViewModel<HomeState, HomeEvent, HomeAction>(
    initialState = HomeState(
        dateRange = Pair(
            //TODO: recheck on importing time as provider
            LocalDate.now().withDayOfMonth(1),
            LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())
        )
    )
) {
    init {
        trySendAction(HomeAction.LoadHealthData)
    }

    override fun handleAction(action: HomeAction) {
        when (action) {
            is HomeAction.LoadHealthData -> handleLoadHealthData()
            is HomeAction.RefreshData -> handleRefreshData()
            is HomeAction.AddEntryClick -> handleAddEntryClick()
            is HomeAction.Internal.HealthDataLoaded -> handleHealthDataLoaded(
                action.snapshot,
                action.stepsByDate,
            )

            is HomeAction.Internal.HealthDataError -> handleHealthDataError(action.message)
        }
    }

    private fun handleLoadHealthData() {
        mutableStateFlow.value = state.copy(isLoading = true, error = null)
        fetchAllHealthData()
    }

    private fun handleRefreshData() {
        mutableStateFlow.value = state.copy(isRefreshing = true, error = null)
        fetchAllHealthData()
    }

    private fun fetchAllHealthData() {
        viewModelScope.launch {
            try {
                val snapshot = healthConnectRepository.getTodaySnapshot()
                val stepsByDate = healthConnectRepository.getStepsForDateRange(
                    startDate = state.dateRange.first,
                    endDate = state.dateRange.second
                )

                sendAction(
                    HomeAction.Internal.HealthDataLoaded(
                        snapshot,
                        stepsByDate,
                    )
                )
            } catch (e: Exception) {
                Log.d("ERR", "fetchAllHealthData: ${e.message}", e)
                sendAction(HomeAction.Internal.HealthDataError(e.message ?: "Unknown error"))
            }
        }
    }

    private fun handleHealthDataLoaded(
        snapshot: HealthSnapshot,
        stepsByDate: Map<LocalDate, Long>,
    ) {
        mutableStateFlow.value = state.copy(
            isLoading = false,
            isRefreshing = false,
            // Activity
            steps = snapshot.steps,
            distanceMeters = snapshot.distanceMeters,
            activeCalories = snapshot.activeCalories,
            totalCalories = snapshot.totalCalories,
            floorsClimbed = snapshot.floorsClimbed,
            exerciseCount = snapshot.exerciseCount,
            exerciseDurationMinutes = snapshot.exerciseDurationMinutes,
            // Sleep
            sleepDurationMinutes = snapshot.sleepDurationMinutes,
            sleepStartTime = snapshot.sleepStartTime,
            sleepEndTime = snapshot.sleepEndTime,
            // Vitals
            heartRate = snapshot.heartRate,
            // Chart
            stepsByDate = stepsByDate,
        )
    }

    private fun handleHealthDataError(message: String) {
        mutableStateFlow.value = state.copy(
            isLoading = false,
            isRefreshing = false,
            error = message
        )
    }

    private fun handleAddEntryClick() {
        sendEvent(HomeEvent.NavigateToAddEntry)
    }
}

data class HomeState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val error: String? = null,

    // Activity
    val steps: Long = 0,
    val distanceMeters: Double = 0.0,
    val activeCalories: Double = 0.0,
    val totalCalories: Double = 0.0,
    val floorsClimbed: Long = 0,
    val exerciseCount: Int = 0,
    val exerciseDurationMinutes: Long = 0,

    // Sleep
    val sleepDurationMinutes: Long = 0,
    val sleepStartTime: String? = null,
    val sleepEndTime: String? = null,

    // Vitals
    val heartRate: Long = 0,
    val weight: Double = 0.0,

    // Chart data
    val stepsByDate: Map<LocalDate, Long> = emptyMap(),
    val caloriesByDate: Map<LocalDate, Long> = emptyMap(),
    val dateRange: Pair<LocalDate, LocalDate>,

    // Recent activities
    val recentActivities: List<RecentActivity> = emptyList(),
) {
    val calendarProgressTrackerData: CalendarProgressTrackerData
        get() {
            val averageStepsPerDay = if (stepsByDate.isNotEmpty()) {
                stepsByDate.values.sum() / stepsByDate.size
            } else {
                0L
            }
            val averageCaloriesSpentPerDay = if (caloriesByDate.isNotEmpty()) {
                caloriesByDate.values.sum() / caloriesByDate.size
            } else {
                0L
            }
            return CalendarProgressTrackerData(
                stepsByDate = stepsByDate,
                averageStepsPerDay = averageStepsPerDay,
                caloriesSpentByDate = caloriesByDate,
                averageCaloriesSpentPerDay = averageCaloriesSpentPerDay,
            )
        }
}

data class CalendarProgressTrackerData(
    val stepsByDate: Map<LocalDate, Long>,
    val averageStepsPerDay: Long,
    val caloriesSpentByDate: Map<LocalDate, Long>,
    val averageCaloriesSpentPerDay: Long,
)

sealed class HomeEvent {
    data object NavigateToAddEntry : HomeEvent()
}

sealed class HomeAction {
    data object LoadHealthData : HomeAction()
    data object RefreshData : HomeAction()
    data object AddEntryClick : HomeAction()

    sealed class Internal : HomeAction() {
        data class HealthDataLoaded(
            val snapshot: HealthSnapshot,
            val stepsByDate: Map<LocalDate, Long> = emptyMap(),
        ) : Internal()

        data class HealthDataError(val message: String) : Internal()
    }
}
