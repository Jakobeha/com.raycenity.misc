@file:Suppress("PropertyName")

val kotlin_version: String by project

plugins {
    application
    kotlin("jvm")
}

group = "com.raycenity"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlin_version")
    // Tests
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
