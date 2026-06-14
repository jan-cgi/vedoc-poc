plugins {
    kotlin("jvm") version "2.3.20"
    id("io.gatling.gradle") version "3.15.1"
}

group = "com.example"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    gatlingImplementation("com.ibm.mq:com.ibm.mq.jakarta.client:9.4.5.1")
}

tasks.withType<JavaCompile> {
    options.release.set(25)
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_25)
    }
}

gatling {
    includeMainOutput = false
    includeTestOutput = false
}
