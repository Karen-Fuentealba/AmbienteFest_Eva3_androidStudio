plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.antaedo_karfuentealba.eva3_ambientefest"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.antaedo_karfuentealba.eva3_ambientefest"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    }
}

dependencies {

        // Retrofit + Moshi
        implementation("com.squareup.retrofit2:retrofit:2.11.0")
        implementation("com.squareup.retrofit2:converter-moshi:2.11.0")
        // Agregar Moshi core explícito para asegurar availability de KotlinJsonAdapterFactory
        implementation("com.squareup.moshi:moshi:1.15.1")
        // Usaremos reflexión en lugar de code-gen para evitar problemas de build.
        implementation("com.squareup.moshi:moshi-kotlin:1.15.1")

        // OkHttp
        implementation("com.squareup.okhttp3:okhttp:4.12.0")
        implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

        // Jetpack Compose BOM (ya lo tienes)
        implementation(platform(libs.androidx.compose.bom))


        // Material3 (sin versión porque BOM la maneja)
        implementation("androidx.compose.material3:material3")
        // Material icons extended
        implementation("androidx.compose.material:material-icons-extended")

        // ViewModel para Compose
        implementation("androidx.lifecycle:lifecycle-viewmodel-compose")

        // Activity Compose
        implementation("androidx.activity:activity-compose")

        // Coroutines
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0")

        // kotlin-reflect (necesario para moshi kotlin reflection en algunos entornos)
        implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.0")

        // DataStore
        implementation("androidx.datastore:datastore-preferences:1.1.1")

        // Core KTX y otros (ya los tienes con libs.*)
        implementation(libs.androidx.core.ktx)
        implementation(libs.androidx.lifecycle.runtime.ktx)
        implementation(libs.androidx.activity.compose)
        implementation(libs.androidx.compose.ui)
        implementation(libs.androidx.compose.ui.graphics)
        implementation(libs.androidx.compose.ui.tooling.preview)
        implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.navigation.compose)

        // Coil para imágenes remotas
        implementation("io.coil-kt:coil-compose:2.4.0")

    // Testing
    testImplementation(libs.junit)
    testImplementation("org.mockito:mockito-core:5.3.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.9.0")
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

}