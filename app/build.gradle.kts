@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    id ("com.google.dagger.hilt.android")
    kotlin("kapt")
}

android {
    namespace = "com.example.mediaplayer3"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mediaplayer3"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.activity.compose)
    implementation(platform(libs.compose.bom))
    implementation(libs.ui)
    implementation(libs.ui.graphics)
    implementation(libs.ui.tooling.preview)
    implementation(libs.material3)
    implementation(libs.io.coeil.kt)
    implementation(libs.compose.icon.material.extended)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.lifecycle.runtime.ktx.compose)
    implementation(libs.android.hilt)
    implementation(libs.android.hilt.navigation)
    implementation(libs.material)
    implementation(libs.androidx.media3.session)
    implementation(libs.androidx.media3.ui)
    kapt("com.google.dagger:hilt-compiler:2.50")
    implementation(project(":mpdatadrovider"))
    implementation(project(":mplogger"))
    implementation(project(":mpmediamanager"))
    implementation(project(":mpstorage"))
    implementation(project(":mpeventhandler"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(platform(libs.compose.bom))
    androidTestImplementation(libs.ui.test.junit4)

    debugImplementation(libs.ui.tooling)
    debugImplementation(libs.ui.test.manifest)
}

kapt {
    correctErrorTypes = true
}