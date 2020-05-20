plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
}

android {
    compileSdkVersion(29)
    defaultConfig {
        applicationId = "news.lambda.android"
        minSdkVersion(21)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0.0"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    packagingOptions {
        exclude("META-INF/*kotlin*")
    }
}

dependencies {
    debugImplementation(deps.Facebook.Flipper.Debug)
    debugImplementation(deps.Facebook.Flipper.Network)
    debugImplementation(deps.Facebook.SoLoader)
    implementation(deps.Android.X.AppCompat)
    implementation(deps.Android.X.Core)
    implementation(deps.Arrow.Core)
    implementation(deps.Arrow.Syntax)
    implementation(deps.Dropbox.Store)
    implementation(deps.Kotlin.Coroutines.Android)
    implementation(deps.Kotlin.StdLib.Jvm)
    implementation(deps.Max.Navigator)
    implementation(deps.Oolong)
    implementation(deps.Square.Moshi.Kotlin)
    implementation(deps.Square.Retrofit.Converter.Moshi)
    implementation(deps.Square.Retrofit.Core)
    implementation(project(":lambda-news:android-compose"))
    implementation(project(":lambda-news:core"))
    implementation(project(":lambda-news:view"))
    kapt(deps.Arrow.Meta)
    releaseImplementation(deps.Facebook.Flipper.Release)
}
