package deps

object Android {
    object Gradle : Group("com.android.tools.build", "4.1.0-alpha09") {
        val Plugin = artifact("gradle")
    }

    object X {

        val AppCompat = dependency("androidx.appcompat", "appcompat", "1.1.0")
        val Browser = dependency("androidx.browser", "browser", "1.2.0")
        val Core = dependency("androidx.core", "core-ktx", "1.2.0")

        object Compose : Group("androidx.compose", "0.1.0-dev11") {
            val Runtime = artifact("compose-runtime")
        }

        object Ui : Group("androidx.ui", Compose.version) {
            val Animation = artifact("ui-animation")
            val Core = artifact("ui-core")
            val Foundation = artifact("ui-foundation")
            val Layout = artifact("ui-layout")
            val Material = artifact("ui-material")
            val Tooling = artifact("ui-tooling")
        }
    }
}

object Arrow : Group("io.arrow-kt", "0.10.5") {
    val Core = artifact("arrow-core")
    val Meta = artifact("arrow-meta")
    val Syntax = artifact("arrow-syntax")
}

object ChrisBanes {
    object Accompanist : Group("dev.chrisbanes.accompanist", "0.1.2") {
        val Coil = artifact("accompanist-coil")
    }
}

object Dropbox {
    val Store = dependency("com.dropbox.mobile.store", "store4", "4.0.0-alpha03")
}

object Facebook {
    val SoLoader = dependency("com.facebook.soloader", "soloader", "0.9.0")

    object Flipper : Group("com.facebook.flipper", "0.43.0") {
        val Debug = artifact("flipper")
        val Release = artifact("flipper-noop")
        val Network = artifact("flipper-network-plugin")
    }
}

object Kotlin : Group("org.jetbrains.kotlin", "1.3.72") {
    object Coroutines : Group("org.jetbrains.kotlinx", "1.3.6") {
        val Android = artifact("kotlinx-coroutines-android")

        object Core {
            val Jvm = artifact("kotlinx-coroutines-core")
        }
    }

    object Gradle {
        val Plugin = artifact("kotlin-gradle-plugin")
    }

    object StdLib {
        val Jvm = artifact("kotlin-stdlib")
    }

    object Test {
        val JUnit5 = artifact("kotlin-test-junit5")
        val Jvm = artifact("kotlin-test")
    }
}

object Max : Group("com.michaelpardo.max", "0.1.0-SNAPSHOT") {
    val Navigator = artifact("navigator")
    val Uri = artifact("uri")
}

object Square {
    object Moshi : Group("com.squareup.moshi", "1.9.2") {
        val Kotlin = artifact("moshi-kotlin")
    }

    object Retrofit : Group("com.squareup.retrofit2", "2.9.0") {
        val Core = artifact("retrofit")

        object Converter {
            val Moshi = artifact("converter-moshi")
        }
    }
}

val Oolong = dependency("org.oolong-kt", "oolong", "2.0.4")
