plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdkVersion(29)
    defaultConfig {
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
    buildFeatures {
        // Waiting for these issues to be resolved:
        //   - https://youtrack.jetbrains.com/issue/KT-34583
        //   - https://issuetracker.google.com/issues/143232368
        compose = true
    }
    composeOptions {
        kotlinCompilerVersion = "1.3.70-dev-withExperimentalGoogleExtensions-20200424"
        kotlinCompilerExtensionVersion = deps.Android.X.Compose.version
    }
    packagingOptions {
        exclude("META-INF/*kotlin*")
    }
}

dependencies {
    api(project(":lambda-news:core"))
    api(project(":lambda-news:view"))
    implementation(deps.Android.X.AppCompat)
    implementation(deps.Android.X.Browser)
    implementation(deps.Android.X.Compose.Runtime)
    implementation(deps.Android.X.Core)
    implementation(deps.Android.X.Ui.Animation)
    implementation(deps.Android.X.Ui.Core)
    implementation(deps.Android.X.Ui.Foundation)
    implementation(deps.Android.X.Ui.Layout)
    implementation(deps.Android.X.Ui.Material)
    implementation(deps.Android.X.Ui.Tooling)
    implementation(deps.Arrow.Core)
    implementation(deps.Arrow.Syntax)
    implementation(deps.Kotlin.Coroutines.Android)
    implementation(deps.Kotlin.StdLib.Jvm)
    implementation(deps.Max.Navigator)
    implementation(deps.Oolong)
}
