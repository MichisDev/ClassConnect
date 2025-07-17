import java.io.File
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    id("org.jetbrains.kotlin.android")
    alias(libs.plugins.google.gms.google.services)
}

val localProperties = Properties().apply {
    load(File(rootDir, "local.properties").inputStream())
}
val googleApiKey: String = localProperties["GOOGLE_API_KEY"] as? String
    ?: throw GradleException("GOOGLE_API_KEY no está definido en local.properties")


android {
    namespace = "com.example.agendadigital"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.agendadigital"
        minSdk = 27
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Inyectamos la clave API
        buildConfigField("String", "GOOGLE_API_KEY", "\"$googleApiKey\"")
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
        viewBinding = true
        buildConfig = true
    }

}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.auth)
    implementation(libs.volley)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation(platform("com.google.firebase:firebase-bom:33.10.0"))

    // Depenpencia firebase-messaging FCM
    implementation("com.google.firebase:firebase-messaging:23.4.1")

    // Volley
    implementation("com.android.volley:volley:1.2.1")

    // calendario
    implementation("com.applandeo:material-calendar-view:1.9.0-rc03")

    // graficos
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")


}