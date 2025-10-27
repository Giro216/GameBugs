plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.gamebugs"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.gamebugs"
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
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)

    //noinspection UseTomlInstead,GradleDependency
    implementation("com.squareup.retrofit2:retrofit:3.0.0")
    //noinspection UseTomlInstead,GradleDependency
    implementation("com.squareup.retrofit2:converter-simplexml:3.0.0")
    //noinspection UseTomlInstead,GradleDependency
    implementation("com.squareup.okhttp3:logging-interceptor:5.2.1")
    //noinspection UseTomlInstead,GradleDependency
    implementation("org.simpleframework:simple-xml:2.7.1")
    val roomVersion = "2.8.1"
    //noinspection UseTomlInstead,GradleDependency
    ksp("androidx.room:room-compiler:$roomVersion")
    //noinspection UseTomlInstead,GradleDependency
    implementation("androidx.room:room-runtime:$roomVersion")
    //noinspection UseTomlInstead,GradleDependency
    implementation("androidx.compose.ui:ui:1.5.4")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.unit)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.compose.runtime)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.ui.tooling)
}