# Phoenix Fitness Tracker - Project Structure

## Architecture
This project follows **Clean Architecture** principles with three main layers:

```
app/src/main/java/dev/tomislavmiksik/phoenix/
├── data/              # Data Layer
│   ├── local/         # Room Database, DAOs, Entities
│   ├── remote/        # Retrofit APIs, DTOs
│   └── repository/    # Repository implementations
│
├── domain/            # Domain Layer
│   ├── model/         # Domain models (business logic entities)
│   ├── repository/    # Repository interfaces
│   └── usecase/       # Use cases (business logic)
│
├── presentation/      # Presentation Layer
│   ├── screens/       # Compose screens (UI)
│   ├── components/    # Reusable UI components
│   ├── navigation/    # Navigation setup
│   └── viewmodel/     # ViewModels
│
├── di/                # Dependency Injection
│   ├── AppModule.kt
│   ├── NetworkModule.kt
│   └── DatabaseModule.kt
│
└── ui/                # UI Theme
    └── theme/         # Colors, Typography, Theme
```

## Tech Stack
- **UI**: Jetpack Compose + Material 3
- **Navigation**: Navigation Compose
- **DI**: Hilt
- **Database**: Room
- **Networking**: Retrofit + OkHttp + Kotlinx Serialization
- **Images**: Coil
- **Async**: Coroutines + Flow
- **Architecture**: MVVM + Clean Architecture

## Recommended Package Structure for Features

For each feature (e.g., Workouts, Progress, Exercises), create:

### Data Layer (`data/`)
- `local/` - Room entities and DAOs
- `remote/` - API interface and DTOs
- `repository/` - Repository implementation

### Domain Layer (`domain/`)
- `model/` - Domain models
- `repository/` - Repository interface
- `usecase/` - Use cases (GetWorkouts, SaveWorkout, etc.)

### Presentation Layer (`presentation/`)
- `screens/` - Composable screens
- `viewmodel/` - ViewModels
- `components/` - Feature-specific components

## Example Feature Structure

```
workout/
├── data/
│   ├── local/
│   │   ├── WorkoutEntity.kt
│   │   └── WorkoutDao.kt
│   ├── remote/
│   │   ├── WorkoutApi.kt
│   │   └── WorkoutDto.kt
│   └── repository/
│       └── WorkoutRepositoryImpl.kt
├── domain/
│   ├── model/
│   │   └── Workout.kt
│   ├── repository/
│   │   └── WorkoutRepository.kt
│   └── usecase/
│       ├── GetWorkoutsUseCase.kt
│       └── SaveWorkoutUseCase.kt
└── presentation/
    ├── screens/
    │   ├── WorkoutListScreen.kt
    │   └── WorkoutDetailScreen.kt
    ├── viewmodel/
    │   └── WorkoutViewModel.kt
    └── components/
        └── WorkoutCard.kt
```

## Next Steps

1. **Define your features** (Workouts, Progress, Exercises, Profile, etc.)
2. **Create domain models** for your core business entities
3. **Set up Room database** with entities and DAOs
4. **Create API interfaces** for backend communication
5. **Implement repositories** following the repository pattern
6. **Build use cases** for business logic
7. **Create ViewModels** to manage UI state
8. **Design Compose screens** for your UI

## Development Tips

- Use **StateFlow** in ViewModels for UI state
- Implement **Resource/Result** wrapper for API responses
- Use **Sealed classes** for UI states (Loading, Success, Error)
- Leverage **Hilt** for dependency injection throughout
- Keep domain models pure (no Android dependencies)
- Use **mappers** to convert between DTOs, Entities, and Domain models
