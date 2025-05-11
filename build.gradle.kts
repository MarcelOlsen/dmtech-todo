import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.9.23"
    id("org.jetbrains.compose") version "1.6.1"
    // The 'application' plugin conflicts with compose.desktop's application setup
    // application
}

group = "com.example"
version = "1.0-SNAPSHOT"

// Add Java toolchain configuration
// Compose requires Java 11 or higher
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

repositories {
    mavenCentral()
    google() // Required for Compose
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev") // Required for Compose
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(kotlin("stdlib"))
    implementation("org.burnoutcrew.composereorderable:reorderable:0.9.6")
    implementation(compose.materialIconsExtended)
}

// Configure Kotlin compilation
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11" // Set JVM target to 11 for Compose
}

// Compose Desktop specific configuration
compose.desktop {
    application {
        mainClass = "com.example.todo.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "ToDoApp"
            packageVersion = "1.0.0"
        }
    }
}