plugins {
    kotlin("jvm") version "1.5.20"
}

group = "com.jetbrains.handson"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        name = "ktor-eap"
    }
}

val ktor_version: String by project

dependencies {
    implementation("com.github.ajalt.clikt:clikt:3.4.0")

    implementation("io.ktor:ktor-client-websockets-jvm:2.0.0-eap-256")
    implementation("io.ktor:ktor-client-cio-jvm:2.0.0-eap-256")
}

/*
ktor-client-cio provides a client implementation of Ktor on top of coroutines ("Coroutine-based I/O").
ktor-client-websockets is the counterpart to the ktor-websockets dependency on the server, and allows us to consume WebSockets from the client with the same API as the server.
 */

