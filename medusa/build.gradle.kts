plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.maven.publish)
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

android {
    namespace = "com.trendyol.medusalib"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
        consumerProguardFiles("consumer-rules.pro")

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
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }
}

group = "com.trendyol"
version = "0.13.0"

mavenPublishing {
    publishToMavenCentral(automaticRelease = false)
    pom {
        name = "medusa"
        description = "Android Fragment Stack Controller"
        url = "https://github.com/Trendyol/medusa"
        licenses {
            license {
                name = "Medusa License"
                url = "https://github.com/Trendyol/medusa/blob/master/LICENSE"
            }
        }
        developers {
            developer {
                id = "erkutaras"
                name = "Erkut Aras"
                email = "erkut.aras@trendyol.com"
            }
            developer {
                id = "muratcanbur"
                name = "Murat Can Bur"
                email = "muratcan.bur@trendyol.com"
            }
            developer {
                id = "MertNYuksel"
                name = "Mert Nevzat Yüksel"
                email = "mert.yuksel@trendyol.com"
            }
            developer {
                id = "ibrahimsn98"
                name = "İbrahim Süren"
                email = "ibrahim.suren@trendyol.com"
            }
            developer {
                id = "mucahidkambur"
                name = "Mücahid Kambur"
                email = "mucahid.kambur@trendyol.com"
            }
            developer {
                id = "azizutku"
                name = "Aziz Utku Kağıtçı"
                email = "utku.kagitci@trendyol.com"
            }
        }
        scm {
            connection = "scm:git:github.com/Trendyol/medusa.git"
            developerConnection = "scm:git:ssh://github.com/Trendyol/medusa.git"
            url = "https://github.com/Trendyol/medusa/tree/main"
        }
    }
    val signingKeyId = System
        .getenv("ORG_GRADLE_PROJECT_signingInMemoryKeyId")
        .orEmpty()
    val signingKeyPassword = System
        .getenv("ORG_GRADLE_PROJECT_signingInMemoryKeyPassword")
        .orEmpty()
    val signingKey = System
        .getenv("ORG_GRADLE_PROJECT_signingInMemoryKey")
        .orEmpty()
    if (signingKeyId.isNotEmpty() && signingKey.isNotEmpty() && signingKeyPassword.isNotEmpty()) {
        signAllPublications()
    }
}

dependencies {
    implementation(libs.androidXAppCompat)
    implementation(libs.androidXLifecycleCommon)
    implementation(libs.kotlinStdLib)
    testImplementation(libs.androidXFragmentTesting)
    testImplementation(libs.junit)
    testImplementation(libs.robolectric)
    testImplementation(libs.truth)
    androidTestImplementation(libs.androidXTestExtJunit)
    androidTestImplementation(libs.androidXTestRunner)
    debugImplementation(libs.androidXFragmentTesting)
    androidTestImplementation(libs.truth)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.espressoTest)

}

