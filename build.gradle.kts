import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

subprojects {
    buildscript {
        repositories {
            mavenCentral()
        }
    }

    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jetbrains.kotlin.kapt")
    apply(plugin = "org.jetbrains.kotlin.plugin.spring")
    apply(plugin = "org.jetbrains.kotlin.plugin.jpa")

    apply(plugin = "org.springframework.boot")
    apply(plugin = "io.spring.dependency-management")

    apply(plugin = "com.google.cloud.tools.jib")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    repositories {
        mavenCentral()
    }

    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-reflect")
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

        implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
        implementation("org.mapstruct:mapstruct:${Constants.mapstructVersion}")
        kapt("org.mapstruct:mapstruct-processor:${Constants.mapstructVersion}")

        testImplementation("org.springframework.boot:spring-boot-starter-test")
        testImplementation("org.testcontainers:junit-jupiter:${Constants.testcontainersVersion}")
        testImplementation("org.testcontainers:testcontainers:${Constants.testcontainersVersion}")
    }

    jib {
        from {
            image = "arm64v8/eclipse-temurin:17-jre-ubi9-minimal"
        }
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}

repositories {
    mavenCentral()
}

plugins {
    kotlin("jvm") version Constants.kotlinVersion
    kotlin("kapt") version Constants.kotlinVersion
    kotlin("plugin.spring") version Constants.kotlinVersion
    kotlin("plugin.jpa") version Constants.kotlinVersion
    id("org.springframework.boot") version Constants.springVersion
    id("io.spring.dependency-management") version Constants.dependencyManagementVersion
    id("com.google.cloud.tools.jib") version Constants.jibVersion
    id("org.jlleitschuh.gradle.ktlint") version Constants.ktlintVersion
}
