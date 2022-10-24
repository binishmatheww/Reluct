plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.compose")
}

android {
    namespace = "work.racka.reluct.common.features.screen_time"
    buildFeatures { compose = true }
    composeOptions { kotlinCompilerExtensionVersion = Versions.composeCompiler }
}

kotlin {
    android()
    jvm("desktop")

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":common:core-navigation"))
                implementation(project(":common:domain"))
                implementation(project(":common:model"))
                implementation(project(":common:mvvm-core"))
                implementation(project(":common:persistence:settings"))
                implementation(project(":common:system-services"))

                implementation(Dependencies.Kotlin.Coroutines.core)
                implementation(Dependencies.Koin.core)
                implementation(Dependencies.Log.kermit)
            }
        }

        val commonTest by getting {
            dependencies {
                implementation(Dependencies.Mockk.core)
                implementation(Dependencies.Mockk.commonMultiplatform)
                implementation(Dependencies.Koin.test)
                implementation(Dependencies.Kotlin.Coroutines.test)
                implementation(Dependencies.Squareup.Testing.turbine)
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(Dependencies.Android.Essential.coreKtx)
                implementation(Dependencies.Android.Essential.saveState)

                // Compose
                implementation(project(":android:compose:theme"))
                implementation(project(":android:compose:components"))
                with(Dependencies.Android.Compose) {
                    implementation(runtime)
                    implementation(ui)
                    implementation(animation)
                    implementation(material)
                    implementation(preview)
                    implementation(activity)
                    implementation(viewModel)
                    implementation(materialIconsCore)
                    implementation(materialIconsExtended)
                    implementation(foundation)
                    implementation(foundationLayout)
                    implementation(material3)
                    implementation(tooling)
                }
                implementation(Dependencies.Android.Coil.image)
            }
        }

        val androidTest by getting

        val desktopMain by getting {
            dependencies {
                implementation(Dependencies.Log.slf4j)
            }
        }

        val desktopTest by getting
    }
}
