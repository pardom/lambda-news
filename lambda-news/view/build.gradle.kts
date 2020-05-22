plugins {
    kotlin("jvm")
}

dependencies {
    implementation(deps.Arrow.Core)
    implementation(deps.Arrow.Syntax)
    implementation(deps.Kotlin.StdLib.Jvm)
    implementation(deps.Max.Uri)
    implementation(deps.Oolong)
    implementation(project(":lambda-news:core"))
}
