import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    dependencies {
        classpath ("org.springframework.boot:spring-boot-gradle-plugin:1.6.21")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
        classpath ("org.jetbrains.kotlin:kotlin-allopen:1.6.21")
        classpath ("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.17.1")
        classpath ("com.diffplug.spotless:spotless-plugin-gradle:5.14.3")
    }
}

plugins {
    id("org.springframework.boot") version "2.7.5"
    id("io.spring.dependency-management") version "1.0.15.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    id ("org.jetbrains.kotlin.plugin.jpa") version "1.6.21"
    id ("org.sonarqube") version "3.1.1"
    id ("jacoco")

}
group = "br.com.samples"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "2021.0.4"

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation ("org.eclipse.jgit:org.eclipse.jgit:5.4.2.201908231537-r")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation ("io.github.microutils:kotlin-logging:3.0.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-jdk8")
    implementation("org.springframework.cloud:spring-cloud-starter-sleuth")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

sonarqube {
    properties {
        property ("sonar.projectName", "${project.name}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.getByName<Jar>("jar") {
    enabled = false
}