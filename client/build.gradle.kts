plugins {
    kotlin("jvm") version "1.5.20"
}

group = "com.jetbrains.handson"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

val ktor_version: String by project

dependencies {
    implementation("io.ktor:ktor-client-websockets:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("com.github.ajalt.clikt:clikt:3.4.0")

}
/*
ktor-client-cio provides a client implementation of Ktor on top of coroutines ("Coroutine-based I/O").
ktor-client-websockets is the counterpart to the ktor-websockets dependency on the server, and allows us to consume WebSockets from the client with the same API as the server.
 */

