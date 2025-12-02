plugins {
    kotlin("jvm") version "2.1.0"
}

repositories {
    mavenCentral()
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(24))
    }
}

tasks.wrapper {
    gradleVersion = "8.10"
    distributionType = Wrapper.DistributionType.ALL
}

sourceSets {
    named("main") {
        java.srcDirs("src")
    }
}
