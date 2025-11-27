# Navigation Guide

This guide explains the navigation architecture in PhoenixKt, which follows the Bitwarden pattern for type-safe, state-based navigation.

## Architecture Overview

The navigation system uses a **hybrid approach**:

1. **State-Based Navigation** at the root level (Auth vs Main flows)
2. **Event-Based Navigation** within each flow (screen-to-screen)
3. **Type-Safe Routes** using Kotlin Serialization

## Navigation Layers

```
MainActivity
    └── RootNavScreen (State-based)
        ├── RootNavViewModel (manages Auth/Main state)
        └── NavHost
            ├── Auth Graph (Event-based)
            │   └── Login Screen
            └── Main Graph (Event-based)
                └── Home Screen
```

## 1. State-Based Navigation (Root Level)

State-based navigation is managed by `RootNavViewModel` and `RootNavScreen`. The navigation state is determined by data layer state (e.g., user authentication).

### RootNavState

Three possible states:
- `Loading` - Initial state while determining auth status
- `Auth` - User not authenticated → show auth flow
- `Main` - User authenticated → show main app flow

### RootNavViewModel

```kotlin
@HiltViewModel
class RootNavViewModel @Inject constructor() : ViewModel() {
    val rootNavState: StateFlow<RootNavState>

    fun onLoginSuccess() // Transition to Main
    fun onLogout()       // Transition to Auth
}
```

**Benefits:**
- Automatic navigation based on data layer changes
- Single source of truth for app-level navigation
- No need to manually navigate on login/logout

## 2. Event-Based Navigation (Screen Level)

Individual screens use event-based navigation for user interactions.

### Pattern

```kotlin
// ViewModel emits events
sealed class LoginEvent {
    data object NavigateToHome : LoginEvent()
}

// Screen handles events
EventsEffect(viewModel = viewModel) { event ->
    when (event) {
        LoginEvent.NavigateToHome -> onNavigateToHome()
    }
}
```

## 3. Type-Safe Navigation

All routes use Kotlin Serialization for type safety.

### Basic Route (No Parameters)

```kotlin
@Serializable
data object LoginRoute  // Modern: data object instead of object

// Add to graph
fun NavGraphBuilder.loginDestination(
    onNavigateToHome: () -> Unit,
) {
    composable<LoginRoute> {
        LoginScreen(onNavigateToHome = onNavigateToHome)
    }
}

// Navigate
fun NavController.navigateToLogin(
    navOptions: NavOptions? = null,
) {
    this.navigate(route = LoginRoute, navOptions = navOptions)
}
```

### Route With Parameters

```kotlin
@Serializable
data class WorkoutDetailRoute(
    val workoutId: String,
    val isEditMode: Boolean = false,
)

// Add to graph
fun NavGraphBuilder.workoutDetailDestination(
    onNavigateBack: () -> Unit,
) {
    composable<WorkoutDetailRoute> { backStackEntry ->
        // Type-safe parameter extraction
        val route = backStackEntry.toRoute<WorkoutDetailRoute>()

        WorkoutDetailScreen(
            workoutId = route.workoutId,
            isEditMode = route.isEditMode,
            onNavigateBack = onNavigateBack,
        )
    }
}

// Navigate
fun NavController.navigateToWorkoutDetail(
    workoutId: String,
    isEditMode: Boolean = false,
    navOptions: NavOptions? = null,
) {
    this.navigate(
        route = WorkoutDetailRoute(
            workoutId = workoutId,
            isEditMode = isEditMode,
        ),
        navOptions = navOptions,
    )
}
```

### Accessing Parameters in ViewModel

```kotlin
@Serializable
data class WorkoutDetailRoute(
    val workoutId: String,
)

// Args wrapper for ViewModel
data class WorkoutDetailArgs(
    val workoutId: String,
)

fun SavedStateHandle.toWorkoutDetailArgs(): WorkoutDetailArgs {
    val route = this.toRoute<WorkoutDetailRoute>()
    return WorkoutDetailArgs(workoutId = route.workoutId)
}

// ViewModel
@HiltViewModel
class WorkoutDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : BaseViewModel<...>(...) {

    private val args = savedStateHandle.toWorkoutDetailArgs()

    init {
        loadWorkout(args.workoutId)
    }
}
```

## 4. Navigation Graphs

Navigation graphs group related screens.

### Auth Graph

```kotlin
@Serializable
data object AuthGraph  // Modern: @Serializable data object for graphs

fun NavGraphBuilder.authGraph(
    onLoginSuccess: () -> Unit,
    navController: NavController,
) {
    navigation<AuthGraph>(
        startDestination = LoginRoute,
    ) {
        loginDestination(onNavigateToHome = onLoginSuccess)
        // Add more auth screens
    }
}
```

### Main Graph

```kotlin
@Serializable
data object MainGraph  // Modern: @Serializable data object for graphs

fun NavGraphBuilder.mainGraph(
    onLogout: () -> Unit,
    navController: NavController,
) {
    navigation<MainGraph>(
        startDestination = HomeRoute,
    ) {
        homeDestination(onNavigateToLogin = onLogout)
        // Add more main screens
    }
}
```

## 5. Complete Screen Implementation

Here's a complete example of adding a new screen:

### Step 1: Create the Route and Navigation

**`WorkoutListNavigation.kt`**
```kotlin
@Serializable
data object WorkoutListRoute  // Modern: data object

fun NavGraphBuilder.workoutListDestination(
    onNavigateToWorkoutDetail: (String) -> Unit,
    onNavigateBack: () -> Unit,
) {
    composable<WorkoutListRoute> {
        WorkoutListScreen(
            onNavigateToWorkoutDetail = onNavigateToWorkoutDetail,
            onNavigateBack = onNavigateBack,
        )
    }
}

fun NavController.navigateToWorkoutList(
    navOptions: NavOptions? = null,
) {
    this.navigate(route = WorkoutListRoute, navOptions = navOptions)
}
```

### Step 2: Create the ViewModel

**`WorkoutListViewModel.kt`**
```kotlin
@HiltViewModel
class WorkoutListViewModel @Inject constructor(
    // Inject repositories
) : BaseViewModel<WorkoutListState, WorkoutListEvent, WorkoutListAction>(
    initialState = WorkoutListState(workouts = emptyList())
) {
    override fun handleAction(action: WorkoutListAction) {
        when (action) {
            is WorkoutListAction.WorkoutClick -> handleWorkoutClick(action)
        }
    }

    private fun handleWorkoutClick(action: WorkoutListAction.WorkoutClick) {
        sendEvent(WorkoutListEvent.NavigateToWorkoutDetail(action.workoutId))
    }
}

@Parcelize
data class WorkoutListState(
    val workouts: List<Workout>,
) : Parcelable

sealed class WorkoutListEvent {
    data class NavigateToWorkoutDetail(val workoutId: String) : WorkoutListEvent()
}

sealed class WorkoutListAction {
    data class WorkoutClick(val workoutId: String) : WorkoutListAction()
}
```

### Step 3: Create the Screen

**`WorkoutListScreen.kt`**
```kotlin
@Composable
fun WorkoutListScreen(
    onNavigateToWorkoutDetail: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: WorkoutListViewModel = hiltViewModel(),
) {
    val state by viewModel.stateFlow.collectAsStateWithLifecycle()

    EventsEffect(viewModel = viewModel) { event ->
        when (event) {
            is WorkoutListEvent.NavigateToWorkoutDetail -> {
                onNavigateToWorkoutDetail(event.workoutId)
            }
        }
    }

    WorkoutListContent(
        state = state,
        onWorkoutClick = { id ->
            viewModel.trySendAction(WorkoutListAction.WorkoutClick(id))
        },
        onBackClick = onNavigateBack,
    )
}
```

### Step 4: Add to Navigation Graph

**`MainNavigation.kt`**
```kotlin
fun NavGraphBuilder.mainGraph(
    onLogout: () -> Unit,
    navController: NavController,
) {
    navigation<MainGraph>(
        startDestination = HomeRoute,
    ) {
        homeDestination(
            onNavigateToLogin = onLogout,
            onNavigateToWorkoutList = {
                navController.navigateToWorkoutList()
            },
        )

        workoutListDestination(
            onNavigateToWorkoutDetail = { workoutId ->
                navController.navigateToWorkoutDetail(workoutId)
            },
            onNavigateBack = {
                navController.navigateUp()
            },
        )

        workoutDetailDestination(
            onNavigateBack = {
                navController.navigateUp()
            },
        )
    }
}
```

## 6. Navigation Utilities

### EventsEffect

Utility for consuming ViewModel events in Compose:

```kotlin
EventsEffect(viewModel = viewModel) { event ->
    when (event) {
        is MyEvent.Navigate -> onNavigate()
    }
}
```

### Common Navigation Options

```kotlin
// Navigate and clear back stack
navController.navigate(route) {
    popUpTo(0) { inclusive = true }
    launchSingleTop = true
}

// Navigate and pop previous screen
navController.navigate(route) {
    popUpTo(navController.previousBackStackEntry?.destination?.route ?: return@navigate) {
        inclusive = true
    }
}

// Navigate up (back button)
navController.navigateUp()

// Pop back stack to specific destination
navController.popBackStack(route, inclusive = false)
```

## 7. Best Practices

### DO ✅
- Use `@Serializable data object` for routes without parameters (modern)
- Use `@Serializable data class` for routes with parameters
- Use `@Serializable data object` for navigation graphs
- Create `...Navigation.kt` files with destination and navigate extension functions
- Use `EventsEffect` to handle navigation events from ViewModels
- Keep navigation callbacks in parent graphs/screens
- Use state-based navigation for app-level flows (auth, onboarding)
- Use event-based navigation for screen-level flows

### DON'T ❌
- Use plain `object` (use `data object` instead - more modern)
- Inject `NavController` into ViewModels
- Navigate directly in ViewModels (use events instead)
- Use string-based routes (use Kotlin Serialization)
- Pass complex objects in navigation arguments (use IDs and fetch in destination)
- Create circular navigation dependencies

## 8. Current Navigation Structure

```
PhoenixKt
├── Auth Flow (State: RootNavState.Auth)
│   └── Login Screen
│       → onLoginSuccess() → Triggers RootNavState.Main
│
└── Main Flow (State: RootNavState.Main)
    └── Home Screen
        → onLogout() → Triggers RootNavState.Auth
```

## 9. Adding New Flows

To add a new top-level flow (e.g., Onboarding):

1. Add state to `RootNavState`:
```kotlin
sealed class RootNavState {
    data object Onboarding : RootNavState()
    data object Auth : RootNavState()
    data object Main : RootNavState()
}
```

2. Create the graph:
```kotlin
fun NavGraphBuilder.onboardingGraph(
    onComplete: () -> Unit,
    navController: NavController,
) {
    navigation<OnboardingGraph>(startDestination = WelcomeRoute) {
        // Add onboarding screens
    }
}
```

3. Add to `RootNavScreen`:
```kotlin
when (rootNavState) {
    RootNavState.Onboarding -> {
        navController.navigate(ONBOARDING_GRAPH_ROUTE) { ... }
    }
    // ...
}
```

## Key Files

- `ui/platform/feature/rootnav/RootNavScreen.kt` - Root navigation container
- `ui/platform/feature/rootnav/RootNavViewModel.kt` - State-based navigation logic
- `ui/platform/base/util/EventsEffect.kt` - Event handling utility
- `ui/auth/AuthNavigation.kt` - Auth flow graph
- `ui/main/MainNavigation.kt` - Main app flow graph
- `ui/login/LoginNavigation.kt` - Login screen navigation
- `ui/home/HomeNavigation.kt` - Home screen navigation

## Resources

- [Jetpack Navigation Compose](https://developer.android.com/jetpack/compose/navigation)
- [Type-safe Navigation](https://developer.android.com/guide/navigation/navigation-type-safety)
- [Bitwarden Android Architecture](https://github.com/bitwarden/android/blob/main/docs/ARCHITECTURE.md)
