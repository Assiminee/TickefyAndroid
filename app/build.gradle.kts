plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.pjtfacerec"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.pjtfacerec"
        minSdk = 30
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "LOGIN_EMAIL", "\"${findProperty("LOGIN_EMAIL") ?: System.getenv("LOGIN_EMAIL") ?: ""}\"")
        buildConfigField("String", "LOGIN_PASSWORD", "\"${findProperty("LOGIN_PASSWORD") ?: System.getenv("LOGIN_PASSWORD") ?: ""}\"")
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
    buildFeatures {
        viewBinding = true
        android.buildFeatures.buildConfig = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.camera.lifecycle)
    implementation(libs.androidx.activity.v1100)
    implementation("androidx.camera:camera-view:1.4.2")
    implementation("androidx.camera:camera-camera2:1.4.2")
    implementation("androidx.camera:camera-core:1.4.2")
    implementation("androidx.camera:camera-lifecycle:1.4.2")
    implementation("androidx.activity:activity:1.10.1")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}