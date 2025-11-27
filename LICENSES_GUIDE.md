# Licenses Guide

This guide explains the licensing setup in PhoenixKt for App Store compliance.

## Overview

The app includes proper licensing for both the project itself and all third-party open source dependencies.

## Project License

**File:** `/LICENSE`

- **Type:** MIT License
- **Copyright:** © 2025 Tomislav Miksik
- This covers your original code

## Third-Party Licenses

### Automated with Google's OSS Licenses Plugin

We use Google's official OSS Licenses Plugin to automatically:
- ✅ Scan all dependencies
- ✅ Extract their licenses
- ✅ Generate a beautiful licenses screen
- ✅ Keep everything up-to-date automatically

### How It Works

1. **Build Time:** The plugin runs during build and scans your dependencies
2. **Generation:** It creates license data from `pom.xml` files of each library
3. **Runtime:** The `OssLicensesMenuActivity` displays this data

### Configuration

**Build Configuration:**
```kotlin
// Root build.gradle.kts
buildscript {
    dependencies {
        classpath("com.google.android.gms:oss-licenses-plugin:0.10.6")
    }
}

// app/build.gradle.kts
plugins {
    id("com.google.android.gms.oss-licenses-plugin")
}

dependencies {
    implementation("com.google.android.gms:play-services-oss-licenses:17.1.0")
}
```

## About Screen

### Location
`ui/about/AboutScreen.kt`

### Features
- App information (name, version)
- Button to view open source licenses
- Copyright notice
- Navigation back to Home

### Navigation
```
Home Screen
    ↓ [About & Licenses button]
About Screen
    ↓ [View Open Source Licenses button]
Google's Auto-Generated Licenses Activity
```

### Code Example

```kotlin
// Opening the licenses screen
private fun openOssLicenses(context: Context) {
    val intent = Intent(context, OssLicensesMenuActivity::class.java)
    context.startActivity(intent)
}
```

## App Store Requirements

### Google Play Store ✅
- **Requirement:** Display licenses for open source software
- **Solution:** Google's OSS Licenses Plugin (official solution)
- **Compliance:** ✅ Fully compliant

### Apple App Store ✅
- **Requirement:** Acknowledge use of third-party code
- **Solution:** About screen with licenses accessible in-app
- **Compliance:** ✅ Fully compliant

## Covered Dependencies

The plugin automatically handles licenses for:

- ✅ Kotlin & Kotlin Coroutines
- ✅ Jetpack Compose & Material 3
- ✅ Hilt (Dagger)
- ✅ Room Database
- ✅ Retrofit & OkHttp
- ✅ Kotlin Serialization
- ✅ Coil
- ✅ All AndroidX libraries
- ✅ **Any future dependencies you add**

## Maintenance

### Adding New Dependencies

**NO action required!**

When you add new dependencies to `build.gradle.kts`:
1. The plugin automatically detects them on next build
2. Their licenses are extracted from their POM files
3. They appear in the licenses screen automatically

### Checking Generated Licenses

After building, the plugin generates files in:
```
app/build/generated/third_party_licenses/
```

You can inspect these if needed, but it's automatic.

## Build Output

When you build, you'll see these tasks:
```
> Task :app:devDebugOssDependencyTask
> Task :app:devDebugOssLicensesTask
```

This means the plugin successfully scanned and generated licenses.

## Testing

### How to Test

1. Build the app: `./gradlew assembleDevDebug`
2. Run the app
3. Navigate: Home → About & Licenses → View Open Source Licenses
4. Verify all dependencies are listed

### What to Check

- ✅ All major dependencies appear
- ✅ License text is readable
- ✅ Navigation works smoothly
- ✅ UI looks professional

## Troubleshooting

### Missing Licenses

If a dependency doesn't appear:
1. Check if it has a POM file with license info
2. Check the build output for warnings
3. Some dependencies might not declare licenses properly

### Custom Dependencies

For internal or custom libraries without proper POM files:
- Manually add to the About screen description
- Or create a custom `third_party_licenses.json`

## Benefits

### vs Manual Approach

| Feature | Automated (Current) | Manual |
|---------|-------------------|--------|
| Setup Time | ✅ 5 minutes | ❌ 1-2 hours |
| Maintenance | ✅ Zero | ❌ Update every dependency change |
| Accuracy | ✅ Always up-to-date | ❌ Prone to errors |
| Professional Look | ✅ Google-designed UI | 🤷 Custom implementation |
| Play Store Compliance | ✅ Official solution | ✅ If done correctly |

## Best Practices

### DO ✅
- Keep the OSS Licenses plugin updated
- Test the licenses screen before each release
- Include a link to your project's LICENSE in the About screen
- Make the licenses easily accessible (max 2 taps from home)

### DON'T ❌
- Manually edit generated license files (they're overwritten)
- Remove the licenses button from the app
- Skip testing the licenses screen
- Forget to update the app version in About screen

## Files Structure

```
PhoenixKt/
├── LICENSE                                  # Project MIT License
├── app/
│   ├── build.gradle.kts                    # OSS plugin applied here
│   └── src/main/java/.../
│       └── ui/
│           ├── about/
│           │   ├── AboutScreen.kt          # About & Licenses UI
│           │   └── AboutNavigation.kt      # Navigation setup
│           ├── home/
│           │   └── HomeScreen.kt           # "About & Licenses" button
│           └── main/
│               └── MainNavigation.kt       # About route added
└── build.gradle.kts                         # OSS plugin dependency
```

## References

- [Google OSS Licenses Plugin](https://github.com/google/play-services-plugins/tree/master/oss-licenses-plugin)
- [Play Services OSS Licenses](https://developers.google.com/android/guides/opensource)
- [Google Play Policy - Third-party Code](https://support.google.com/googleplay/android-developer/answer/9888379)

## License Attributions Example

When the user taps "View Open Source Licenses", they'll see:

```
Open Source Licenses

━━━━━━━━━━━━━━━━━━━━━━
Kotlin Standard Library
━━━━━━━━━━━━━━━━━━━━━━
Apache License 2.0
[Full license text...]

━━━━━━━━━━━━━━━━━━━━━━
Jetpack Compose
━━━━━━━━━━━━━━━━━━━━━━
Apache License 2.0
[Full license text...]

... and so on for all dependencies
```

Everything is automatic and beautiful! 🎉
