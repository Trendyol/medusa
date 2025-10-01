plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

android {
    namespace = "com.trendyol.medusa"
    compileSdk = 34


    defaultConfig {
        applicationId = "com.trendyol.medusa"
        minSdk = 21
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(projects.medusalib)
    implementation(libs.androidXAppCompat)
    implementation(libs.androidXConstraintLayout)
    implementation(libs.androidXVectorDrawable)
    implementation(libs.googleMaterial)
    implementation(libs.kotlinStdLib)
    testImplementation(libs.androidXArchCoreTesting)
    testImplementation(libs.androidXTestCoreKtx)
    testImplementation(libs.androidXTestExtJunit)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
}
