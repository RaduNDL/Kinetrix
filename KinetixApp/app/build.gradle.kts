plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.kinetix.app"

    compileSdk = 37

    defaultConfig {
        applicationId = "com.kinetix.app"

        minSdk = 28
        targetSdk = 37

        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false

            /*
             * Pentru emulatorul Android Studio:
             * 10.0.2.2 = calculatorul/laptopul gazda.
             *
             * Nu este necesar adb reverse.
             */
            buildConfigField(
                "String",
                "BASE_URL",
                "\"http://10.0.2.2:5068/\""
            )
        }

        release {
            isMinifyEnabled = false

            /*
             * Înlocuiește adresa înainte de un build release real.
             */
            buildConfigField(
                "String",
                "BASE_URL",
                "\"https://api.example.com/\""
            )

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                ),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "/META-INF/LICENSE.md"
            excludes += "/META-INF/LICENSE-notice.md"
        }
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    // Jetpack Compose
    implementation(
        platform(
            "androidx.compose:compose-bom:2025.12.00"
        )
    )

    implementation(
        "androidx.compose.ui:ui"
    )

    implementation(
        "androidx.compose.ui:ui-graphics"
    )

    implementation(
        "androidx.compose.ui:ui-tooling-preview"
    )

    implementation(
        "androidx.compose.material3:material3"
    )

    implementation(
        "androidx.compose.material:material-icons-extended"
    )

    // Android core
    implementation(
        "androidx.core:core-ktx:1.17.0"
    )

    implementation(
        "androidx.activity:activity-compose:1.12.0"
    )

    // Lifecycle
    implementation(
        "androidx.lifecycle:lifecycle-runtime-ktx:2.9.4"
    )

    implementation(
        "androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4"
    )

    // Navigation
    implementation(
        "androidx.navigation:navigation-compose:2.9.5"
    )

    // Retrofit
    implementation(
        "com.squareup.retrofit2:retrofit:2.11.0"
    )

    implementation(
        "com.squareup.retrofit2:converter-gson:2.11.0"
    )

    // OkHttp
    implementation(
        "com.squareup.okhttp3:okhttp:4.12.0"
    )

    implementation(
        "com.squareup.okhttp3:logging-interceptor:4.12.0"
    )

    // Coroutines
    implementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0"
    )

    // DataStore
    implementation(
        "androidx.datastore:datastore-preferences:1.2.1"
    )

    // Room
    implementation(
        "androidx.room:room-runtime:2.8.4"
    )

    implementation(
        "androidx.room:room-ktx:2.8.4"
    )

    ksp(
        "androidx.room:room-compiler:2.8.4"
    )

    // Health Connect
    implementation(
        "androidx.health.connect:connect-client:1.2.0-alpha06"
    )

    // Unit tests
    testImplementation(
        "junit:junit:4.13.2"
    )

    // Instrumentation tests
    androidTestImplementation(
        platform(
            "androidx.compose:compose-bom:2025.12.00"
        )
    )

    androidTestImplementation(
        "androidx.compose.ui:ui-test-junit4"
    )

    androidTestImplementation(
        "androidx.test.espresso:espresso-core:3.7.0"
    )

    androidTestImplementation(
        "androidx.test.ext:junit:1.3.0"
    )

    // Debug tools
    debugImplementation(
        "androidx.compose.ui:ui-tooling"
    )

    debugImplementation(
        "androidx.compose.ui:ui-test-manifest"
    )
}