import java.util.Properties
import java.net.URI

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

val localSettings = Properties().apply {
    val settingsFile = rootProject.file("local.properties")
    if (settingsFile.isFile) settingsFile.inputStream().use { load(it) }
}
val debugBaseUrl = providers.gradleProperty("kinetix.baseUrl")
    .orElse(providers.environmentVariable("KINETIX_BASE_URL"))
    .orElse(localSettings.getProperty("kinetix.baseUrl", "http://127.0.0.1:5068/"))
    .get().trim().trimEnd('/') + "/"
val releaseBaseUrl = providers.gradleProperty("kinetix.releaseBaseUrl")
    .orElse(providers.environmentVariable("KINETIX_RELEASE_BASE_URL"))
    .orElse(localSettings.getProperty("kinetix.releaseBaseUrl", ""))
    .get().trim()
val debugUri = URI(debugBaseUrl)
require(debugUri.scheme in listOf("http", "https") && !debugUri.host.isNullOrBlank()
    && debugUri.rawQuery == null && debugUri.rawFragment == null && debugUri.rawUserInfo == null) {
    "kinetix.baseUrl must be an HTTP(S) backend URL."
}

val validateReleaseBackend = tasks.register("validateReleaseBackend") {
    inputs.property("baseUrl", releaseBaseUrl)
    doLast {
        val endpoint = inputs.properties["baseUrl"].toString()
        val uri = runCatching { URI(endpoint) }.getOrNull()
        check(uri?.scheme == "https" && !uri.host.isNullOrBlank()
            && uri.host != "api.example.com" && uri.rawQuery == null
            && uri.rawFragment == null && uri.rawUserInfo == null) {
            "Set kinetix.releaseBaseUrl to your deployed HTTPS backend before building release."
        }
    }
}
tasks.matching { it.name == "preReleaseBuild" }.configureEach {
    dependsOn(validateReleaseBackend)
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

            // Start-Kinetix.ps1 forwards this port over USB or wireless debugging.
            manifestPlaceholders["usesCleartextTraffic"] = "true"
            buildConfigField(
                "String",
                "BASE_URL",
                "\"$debugBaseUrl\""
            )
        }

        release {
            isMinifyEnabled = false

            manifestPlaceholders["usesCleartextTraffic"] = "false"
            buildConfigField(
                "String",
                "BASE_URL",
                "\"${releaseBaseUrl.trimEnd('/')}/\""
            )

            proguardFiles(
                getDefaultProguardFile(
                    "proguard-android-optimize.txt"
                )
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
