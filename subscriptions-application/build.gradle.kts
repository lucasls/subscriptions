plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    kotlin("kapt")
}

java.sourceCompatibility = JavaVersion.VERSION_11

dependencies {
    val mapStructVersion = "1.4.2.Final"
    val springsdocVersion = "1.5.4"
    val kotestVersion: String by rootProject.extra

    // Project
    implementation(project(":subscriptions-domain"))

    // Spring
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    // Mapstruct
    implementation("org.mapstruct:mapstruct:$mapStructVersion")
    kapt("org.mapstruct:mapstruct-processor:$mapStructVersion")

    // Springdoc (Open API)
    implementation("org.springdoc:springdoc-openapi-ui:$springsdocVersion")
    implementation("org.springdoc:springdoc-openapi-kotlin:$springsdocVersion")

    // Database
    implementation("org.postgresql:postgresql:42.2.18")
    implementation("org.flywaydb:flyway-core")

    // Other
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.kotest:kotest-assertions-core:$kotestVersion")
}