import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
}

java.sourceCompatibility = JavaVersion.VERSION_11

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Spring (only for dependency injection and transaction management)
    implementation(platform(SpringBootPlugin.BOM_COORDINATES))
    implementation("org.springframework:spring-context")

    // Other
    api("org.joda:joda-money:1.0.1")
}