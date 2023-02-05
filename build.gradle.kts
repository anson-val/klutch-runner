val exposedVersion: String by project

plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "com.example"
version = "0.0.1"

kotlin {
    jvmToolchain(18)
}

application {
    mainClass.set("com.example.ApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("org.postgresql:postgresql:42.5.2")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.slf4j:slf4j-api:2.0.6")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
