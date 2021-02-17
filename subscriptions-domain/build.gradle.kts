import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
}

java.sourceCompatibility = JavaVersion.VERSION_11

dependencies {
    val kotestVersion: String by rootProject.extra
    val mockkVersion: String by rootProject.extra
    val kotlinLoggingVersion: String by rootProject.extra

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Spring (only for dependency injection and transaction management)
    implementation(platform(SpringBootPlugin.BOM_COORDINATES))
    implementation("org.springframework:spring-context")

    // Other
    api("org.joda:joda-money:1.0.1")
    implementation("io.github.microutils:kotlin-logging:$kotlinLoggingVersion")

    // Test
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
}