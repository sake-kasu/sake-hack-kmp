import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.googleServices)
    kotlin("android")
}

android {
    namespace = "org.sake_hack"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.sake_hack"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildTypes {
        debug {
            buildConfigField("String", "GOOGLE_CLIENT_ID_DEV", "\"${System.getenv("GOOGLE_CLIENT_ID_DEV") ?: ""}\"")
        }
        release {
            @Suppress("UnstableApiUsage")
            isMinifyEnabled = false
            buildConfigField("String", "GOOGLE_CLIENT_ID_PROD", "\"${System.getenv("GOOGLE_CLIENT_ID_PROD") ?: ""}\"")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
    }

    @Suppress("UnstableApiUsage")
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {
    implementation(projects.composeApp)
    implementation(libs.androidx.activity.compose)

    // Koin for Android
    implementation(libs.koin.android)
}
