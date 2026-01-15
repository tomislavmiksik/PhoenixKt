package dev.tomislavmiksik.phoenix.ui.dashboard

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.tomislavmiksik.phoenix.core.domain.model.HealthSnapshot
import dev.tomislavmiksik.phoenix.core.domain.repository.HealthConnectRepository
import dev.tomislavmiksik.phoenix.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val healthConnectRepository: HealthConnectRepository,
) : BaseViewModel<DashboardState, DashboardEvent, DashboardAction>(
    initialState = DashboardState(
        dateRange = Pair(
            //TODO: recheck on importing time as provider
            LocalDate.now().withDayOfMonth(1),
            LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())
        )
    )
) {
    init {
        trySendAction(DashboardAction.LoadHealthData)
    }

    override fun handleAction(action: DashboardAction) {
        when (action) {
            is DashboardAction.LoadHealthData -> handleLoadHealthData()
            is DashboardAction.RefreshData -> handleRefreshData()
            is DashboardAction.AddEntryClick -> handleAddEntryClick()
            is DashboardAction.Internal.HealthDataLoaded -> handleHealthDataLoaded(
                action.snapshot,
                action.stepsByDate,
                action.caloriesByDate
            )

            is DashboardAction.Internal.HealthDataError -> handleHealthDataError(action.message)
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
                val caloriesByDate = healthConnectRepository.getActiveCaloriesForDateRange(
                    startDate = state.dateRange.first,
                    endDate = state.dateRange.second
                )
                sendAction(
                    DashboardAction.Internal.HealthDataLoaded(
                        snapshot,
                        stepsByDate,
                        caloriesByDate
                    )
                )
            } catch (e: Exception) {
                sendAction(DashboardAction.Internal.HealthDataError(e.message ?: "Unknown error"))
            }
        }
    }

    private fun handleHealthDataLoaded(
        snapshot: HealthSnapshot,
        stepsByDate: Map<LocalDate, Long>,
        caloriesByDate: Map<LocalDate, Long>,
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
            weight = snapshot.weight,
            // Chart
            stepsByDate = stepsByDate,
            caloriesByDate = caloriesByDate
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
        sendEvent(DashboardEvent.NavigateToAddEntry)
    }
}

data class DashboardState(
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

sealed class DashboardEvent {
    data object NavigateToAddEntry : DashboardEvent()
}

sealed class DashboardAction {
    data object LoadHealthData : DashboardAction()
    data object RefreshData : DashboardAction()
    data object AddEntryClick : DashboardAction()

    sealed class Internal : DashboardAction() {
        data class HealthDataLoaded(
            val snapshot: HealthSnapshot,
            val stepsByDate: Map<LocalDate, Long>,
            val caloriesByDate: Map<LocalDate, Long> = emptyMap(),
        ) : Internal()

        data class HealthDataError(val message: String) : Internal()
    }
}