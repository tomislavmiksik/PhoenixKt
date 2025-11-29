# Phoenix Fitness

An Android fitness tracking application built with Kotlin and Jetpack Compose.

## Tech Stack

- **Kotlin** - Programming language
- **Jetpack Compose** - Modern UI toolkit
- **Material Design 3** - UI design with Orange (#FF8427) primary color
- **Hilt** - Dependency injection
- **Room** - Local database
- **Retrofit** - API client
- **Coroutines & Flow** - Async operations

## Architecture

- **MVVM** with Clean Architecture
- **State/Event/Action** pattern for ViewModels
- **Type-safe navigation** with Kotlin Serialization
- **Interface-based DI** for testability

## Build Variants

- `devDebug` - Development build
- `stagingDebug` - Staging build
- `prodDebug` - Production build

```bash
# Build and install dev variant
./gradlew installDevDebug

# Run Detekt code analysis
./gradlew detekt
```

## Project Structure

```
app/src/main/java/dev/tomislavmiksik/phoenix/
├── core/              # Data layer, config, DI
├── ui/                # Screens and navigation
└── MainActivity.kt
```

## License

MIT License - See LICENSE file

© 2025 Tomislav Miksik
