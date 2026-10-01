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

            buildConfigField(
                "String",
                "BASE_URL",
                "\"http://127.0.0.1:5068/\""
            )
        }

        release {
            isMinifyEnabled = false

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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
    implementation(
        platform("androidx.compose:compose-bom:2025.12.00")
    )

    implementation("androidx.core:core-ktx:1.17.0")
    implementation(
        "androidx.activity:activity-compose:1.12.0"
    )
    implementation(
        "androidx.lifecycle:lifecycle-runtime-ktx:2.9.4"
    )
    implementation(
        "androidx.lifecycle:lifecycle-viewmodel-compose:2.9.4"
    )

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation(
        "androidx.compose.ui:ui-tooling-preview"
    )
    implementation("androidx.compose.material3:material3")
    implementation(
        "androidx.compose.material:material-icons-extended"
    )

    implementation(
        "androidx.navigation:navigation-compose:2.9.5"
    )

    implementation(
        "com.squareup.retrofit2:retrofit:2.11.0"
    )
    implementation(
        "com.squareup.retrofit2:converter-gson:2.11.0"
    )

    implementation(
        "com.squareup.okhttp3:okhttp:4.12.0"
    )
    implementation(
        "com.squareup.okhttp3:logging-interceptor:4.12.0"
    )

    implementation(
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0"
    )

    implementation(
        "androidx.datastore:datastore-preferences:1.2.1"
    )

    implementation(
        "androidx.room:room-runtime:2.8.4"
    )
    implementation(
        "androidx.room:room-ktx:2.8.4"
    )
    ksp(
        "androidx.room:room-compiler:2.8.4"
    )

    implementation(
        "androidx.health.connect:connect-client:1.2.0-alpha06"
    )

    testImplementation("junit:junit:4.13.2")

    androidTestImplementation(
        platform("androidx.compose:compose-bom:2025.12.00")
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

    debugImplementation(
        "androidx.compose.ui:ui-tooling"
    )
    debugImplementation(
        "androidx.compose.ui:ui-test-manifest"
    )
}