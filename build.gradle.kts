plugins {
    kotlin("jvm") version "1.9.0"
    id("org.springframework.boot") version "3.2.0"
}

apply(plugin = "io.spring.dependency-management")

group = "com.github.scroogemcfawk"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.9")
    // TODO check if it works without logback core
    implementation("ch.qos.logback:logback-core:1.4.11")
    implementation("ch.qos.logback:logback-classic:1.4.11")

    implementation("org.jsoup:jsoup:1.16.2")

    implementation("org.springframework.boot:spring-boot-starter")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}
