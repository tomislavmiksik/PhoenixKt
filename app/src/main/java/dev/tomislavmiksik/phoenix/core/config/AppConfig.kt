package dev.tomislavmiksik.phoenix.core.config

import dev.tomislavmiksik.phoenix.BuildConfig

/**
 * Central configuration accessor for the application.
 * Provides a clean interface to access BuildConfig values.
 * This abstraction makes it easier to test and mock configurations.
 */
interface AppConfig {

    /**
     * Base URL for API requests
     */
    val apiBaseUrl: String

    /**
     * API key for authentication
     */
    val apiKey: String

    /**
     * Debug mode flag
     */
    val isDebugMode: Boolean

    /**
     * Network timeout in seconds
     */
    val networkTimeout: Int

    /**
     * Application version name
     */
    val versionName: String

    /**
     * Application version code
     */
    val versionCode: Int

    /**
     * Build type (debug/release)
     */
    val buildType: String

    /**
     * Flavor (dev/staging/prod)
     */
    val flavor: String
}

/**
 * Default implementation of [AppConfig] that reads from BuildConfig.
 */
class AppConfigImpl : AppConfig {
    override val apiBaseUrl: String
        get() = BuildConfig.API_BASE_URL

    override val apiKey: String
        get() = BuildConfig.API_KEY

    override val isDebugMode: Boolean
        get() = BuildConfig.DEBUG_MODE

    override val networkTimeout: Int
        get() = BuildConfig.NETWORK_TIMEOUT

    override val versionName: String
        get() = BuildConfig.VERSION_NAME

    override val versionCode: Int
        get() = BuildConfig.VERSION_CODE

    override val buildType: String
        get() = BuildConfig.BUILD_TYPE

    override val flavor: String
        get() = BuildConfig.FLAVOR
}
