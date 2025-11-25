# BuildConfig Setup Guide

This project uses **product flavors** with **application.properties** files to manage environment-specific configurations.

## Overview

The app has three build flavors:
- **dev** - Development environment
- **staging** - Staging/QA environment
- **prod** - Production environment

Each flavor has its own `application.properties` file that contains environment-specific values like API URLs, keys, and feature flags.

## How It Works

### 1. Product Flavors (`app/build.gradle.kts`)

Three flavors are defined with different app IDs and names:

```kotlin
productFlavors {
    create("dev") {
        applicationIdSuffix = ".dev"
        versionNameSuffix = "-dev"
    }
    create("staging") {
        applicationIdSuffix = ".staging"
        versionNameSuffix = "-staging"
    }
    create("prod") {
        // No suffix for production
    }
}
```

### 2. Properties Files

Each flavor has a properties file at:
- `app/src/dev/application.properties`
- `app/src/staging/application.properties`
- `app/src/prod/application.properties`

### 3. BuildConfig Generation

During build, Gradle reads the appropriate properties file and generates BuildConfig fields:

```kotlin
buildConfigField("String", "API_BASE_URL", "...")
buildConfigField("String", "API_KEY", "...")
buildConfigField("boolean", "DEBUG_MODE", "...")
buildConfigField("int", "NETWORK_TIMEOUT", "...")
```

### 4. Accessing Configuration

Use the `AppConfig` object to access configuration values:

```kotlin
import dev.tomislavmiksik.phoenix.core.config.AppConfig

// Access configuration
val baseUrl = AppConfig.apiBaseUrl
val apiKey = AppConfig.apiKey
val isDebug = AppConfig.isDebugMode
val timeout = AppConfig.networkTimeout
```

## Build Variants

You can build different variants by combining flavors with build types:

- `devDebug` - Development debug build
- `devRelease` - Development release build
- `stagingDebug` - Staging debug build
- `stagingRelease` - Staging release build
- `prodDebug` - Production debug build
- `prodRelease` - Production release build

### Building from Command Line

```bash
# Build dev debug variant
./gradlew assembleDevDebug

# Build staging release variant
./gradlew assembleStagingRelease

# Build production release variant
./gradlew assembleProdRelease

# Install dev debug on device
./gradlew installDevDebug

# Run tests for staging
./gradlew testStagingDebug
```

### Building from Android Studio

1. Go to **Build > Select Build Variant**
2. Choose your desired variant (e.g., `devDebug`, `prodRelease`)
3. Run or build normally

## Properties File Format

```properties
# API Configuration
api.base.url=https://your-api-url.com/v1/
api.key=your_api_key

# Debug Settings
debug.mode=true

# Network Configuration
network.timeout=30

# Feature Flags (optional)
feature.progress.tracking=true
feature.workout.templates=false
```

## Adding New Configuration Values

### Step 1: Add to Properties Files

Add the new property to all three properties files:

```properties
# app/src/dev/application.properties
new.feature.enabled=true

# app/src/staging/application.properties
new.feature.enabled=true

# app/src/prod/application.properties
new.feature.enabled=false
```

### Step 2: Add BuildConfig Field

Update `app/build.gradle.kts` in each flavor:

```kotlin
productFlavors {
    create("dev") {
        val props = loadProperties(getPropertiesFile("dev"))
        // ... existing fields ...
        buildConfigField("boolean", "NEW_FEATURE_ENABLED",
            props.getProperty("new.feature.enabled", "false"))
    }
    // Repeat for staging and prod
}
```

### Step 3: Add to AppConfig

Update `AppConfig.kt`:

```kotlin
object AppConfig {
    val isNewFeatureEnabled: Boolean
        get() = BuildConfig.NEW_FEATURE_ENABLED
}
```

### Step 4: Use in Code

```kotlin
if (AppConfig.isNewFeatureEnabled) {
    // Feature logic
}
```

## Security Best Practices

### DO NOT Commit Sensitive Data

Never commit sensitive data (API keys, secrets) to version control:

1. Add production properties to `.gitignore`:
```gitignore
# Ignore production secrets
app/src/prod/application.properties
```

2. Use the template file:
```bash
cp app/src/prod/application.properties.template app/src/prod/application.properties
# Edit application.properties with real values
```

3. Consider using environment variables for CI/CD:
```kotlin
buildConfigField("String", "API_KEY",
    System.getenv("PROD_API_KEY") ?: props.getProperty("api.key", ""))
```

## Example: NetworkModule Integration

The `NetworkModule` automatically uses BuildConfig values:

```kotlin
@Provides
@Singleton
fun provideRetrofit(): Retrofit {
    return Retrofit.Builder()
        .baseUrl(AppConfig.apiBaseUrl)  // Uses flavor-specific URL
        .build()
}

@Provides
@Singleton
fun provideOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder()
        .connectTimeout(AppConfig.networkTimeout.toLong(), TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor())  // Only logs in debug mode
        .build()
}
```

## Multiple Variants on Same Device

Because each flavor has a unique application ID:
- `dev.tomislavmiksik.phoenix.dev`
- `dev.tomislavmiksik.phoenix.staging`
- `dev.tomislavmiksik.phoenix` (prod)

You can install all three variants on the same device simultaneously!

## Troubleshooting

### Properties not loading

1. Check file exists at correct path: `app/src/{flavor}/application.properties`
2. Verify property names match exactly
3. Clean and rebuild: `./gradlew clean build`

### BuildConfig not found

1. Ensure `buildFeatures { buildConfig = true }` in `build.gradle.kts`
2. Sync Gradle
3. Rebuild project

### Wrong configuration loaded

1. Check selected build variant in Android Studio
2. Verify you're building the correct variant from command line
3. Clean build: `./gradlew clean assembleDevDebug`

## Advanced: Dynamic Configuration

For runtime configuration changes, consider combining BuildConfig with:
- **Remote Config** (Firebase Remote Config)
- **Feature Flags** (LaunchDarkly, Flagsmith)
- **A/B Testing** frameworks

Use BuildConfig for compile-time defaults and override at runtime when needed.
