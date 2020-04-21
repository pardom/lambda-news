plugins {
    kotlin("jvm")
    kotlin("kapt")
}

dependencies {
    implementation(deps.Arrow.Core)
    implementation(deps.Arrow.Syntax)
    implementation(deps.Kotlin.StdLib.Jvm)
    implementation(deps.Kotlin.Coroutines.Core.Jvm)
}
