import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.21"
    application
}

group = "info.skyblond"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.6.0")

    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("org.bitlet:weupnp:0.1.4")
//    implementation("com.offbynull.portmapper:portmapper:2.0.5")
    implementation("com.google.code.gson:gson:2.8.6")
    implementation("io.javalin:javalin:3.13.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile>() {
    kotlinOptions.jvmTarget = "11"
}

application {
    mainClass.set("MainKt")
}