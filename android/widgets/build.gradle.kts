plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-parcelize")
}

android.apply {
    namespace = "work.racka.reluct.android.widgets"

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = Versions.composeCompiler
    }
}

dependencies {
    // Dependency Modules
    implementation(project(":common:model"))
    implementation(project(":android:compose:theme"))
    implementation(project(":android:compose:components"))
    implementation(project(":common:data"))

    // Core Functionality
    with(Dependencies.Android.Essential) {
        implementation(coreKtx)
        implementation(material)
    }

    // Glance
    implementation(Dependencies.Android.Extras.glanceAppWidget)

    implementation(Dependencies.Android.Compose.materialIconsCore)
    implementation(Dependencies.Android.Compose.materialIconsExtended)

    // Koin
    implementation(Dependencies.Koin.core)

    // Testing
    with(Dependencies.Android.JUnit) {
        testImplementation(core)
        testImplementation(test)
        testImplementation(testExtKtx)
        androidTestImplementation(test)
    }

    with(Dependencies.Android.TestCore) {
        testImplementation(testArch)
        androidTestImplementation(testArch)
        androidTestImplementation(testKtx)
    }
}