buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath(deps.Android.Gradle.Plugin)
        classpath(deps.Kotlin.Gradle.Plugin)
    }
}

subprojects {
    repositories {
        google()
        jcenter()
        maven("https://dl.bintray.com/arrow-kt/arrow-kt")
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }
}
