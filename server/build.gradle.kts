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
val logback_version: String by project

dependencies {
    implementation("ch.qos.logback:logback-classic:$logback_version")

    implementation("io.ktor:ktor-server-netty-jvm:2.0.0-eap-256")
    implementation("io.ktor:ktor-server-websockets-jvm:2.0.0-eap-256")


}
/*
 ktor-server-netty adds Ktor together with the Netty engine, allowing us to use server functionality without having to rely on an external application container.
 ktor-websockets allows us to use the WebSocket Ktor plugin, the main communication mechanism for our chat.
 logback-classic provides an implementation of SLF4J, allowing us to see nicely formatted logs in our console.
*/

