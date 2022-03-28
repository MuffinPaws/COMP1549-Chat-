package com.jetbrains.handson.chat.server

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import java.util.*


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    install(WebSockets)
    routing {
        // set of connections
        val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
        webSocket("/chat") {
            // add connection to set of connections
            val thisConnection = Connection(this)
            connections += thisConnection
            try {
                send("You are connected! There are ${connections.count()} users here.")

                // if only 1 user, that's the coordinator
                if (connections.count() == 1) {
                    send("You are the coordinator my friend!")
                    thisConnection.name += "-COORD"
                    thisConnection.coord = 1
                }
                println("Adding ${thisConnection.name}")

                for (frame in incoming) {
                    frame as? Frame.Text ?: continue

                    // if the COORD disconnects, then someone else has to take that role
                    var counter = 0
                    val checkForCood = { connection: Connection -> if (connection.coord == 1) counter++ }
                    connections.forEach(checkForCood)
                    if (counter == 0) {
                        connections.elementAt(0).coord = 1
                        connections.elementAt(0).name += "-COORD"
                    }

                    val receivedText = frame.readText()
                    val textWithUsername = "[${thisConnection.name}]: $receivedText"
                    connections.forEach {
                        it.session.send(textWithUsername)
                    }
                }
            } catch (e: Exception) {
                println(e.localizedMessage)
            } finally {
                println("Removing ${thisConnection.name}")
                connections -= thisConnection
            }
        }
    }
}



