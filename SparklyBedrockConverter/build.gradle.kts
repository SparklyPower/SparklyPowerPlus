import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.0.20"
    kotlin("plugin.serialization") version "2.0.20"
    application
}

group = "net.sparklypower.bedrockconverter"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-cio:3.0.3")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")
    testImplementation(kotlin("test"))
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("net.sparklypower.bedrockconverter.SparklyBedrockConverterKt")
}

tasks.test {
    useJUnitPlatform()
}