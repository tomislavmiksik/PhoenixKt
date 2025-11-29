import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.detekt)
    id("com.google.android.gms.oss-licenses-plugin")
}

fun getPropertiesFile(flavor: String): File {
    return file("app/env/$flavor/application.properties")
}

fun loadProperties(file: File): Properties {
    val properties = Properties()
    if (file.exists()) {
        FileInputStream(file).use { properties.load(it) }
    }
    return properties
}

android {
    namespace = "dev.tomislavmiksik.phoenix"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "dev.tomislavmiksik.phoenix"
        minSdk = 30
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            val devProperties = loadProperties(getPropertiesFile("dev"))

            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"
            resValue("string", "app_name", "Phoenix Dev")

            val apiUrl = devProperties.getProperty("dev.api.baseUrl")
            ?: devProperties.getProperty("api.baseUrl")
            ?: "https://dev-api.example.com/"

            buildConfigField("String", "API_BASE_URL", "\"$apiUrl\"")

            val apiKey = devProperties.getProperty("dev.api.key")
                ?: devProperties.getProperty("api.key")
                ?: ""

            buildConfigField("String", "API_KEY", "\"$apiKey\"")

            val debugMode = devProperties.getProperty("dev.debug.mode")
                ?: devProperties.getProperty("debug.mode")
                ?: false

            buildConfigField("Boolean", "DEBUG_MODE", "$debugMode")

            val networkTimeout = devProperties.getProperty("dev.network.timeout")
                ?: devProperties.getProperty("network.timeout")
                ?: 30

            buildConfigField("Integer", "NETWORK_TIMEOUT", "$networkTimeout")
        }
        create("staging") {
            dimension = "environment"
            applicationIdSuffix = ".staging"
            versionNameSuffix = "-staging"
            resValue("string", "app_name", "Phoenix Staging")
        }
        create("prod") {
            dimension = "environment"
            resValue("string", "app_name", "Phoenix")
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Core Android
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.navigation.compose)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Dependency Injection
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)
    implementation(libs.hilt.navigation.compose)

    // Networking
    implementation(libs.retrofit)
    implementation(libs.retrofit.kotlinx.serialization)
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging.interceptor)
    implementation(libs.kotlinx.serialization.json)

    // Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Image Loading
    implementation(libs.coil.compose)

    // Coroutines
    implementation(libs.kotlinx.coroutines.android)

    // Lifecycle
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // OSS Licenses
    implementation(libs.play.services.oss.licenses)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}

// Detekt Configuration
detekt {
    buildUponDefaultConfig = true
    allRules = false
    config.setFrom("$projectDir/../detekt.yml")
    baseline = file("$projectDir/detekt-baseline.xml")

    source.setFrom(
        "src/main/java",
        "src/test/java",
        "src/androidTest/java"
    )
}