package com.jetbrains.handson.chat.server

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import java.util.*


fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    //use KTOR websockets
    install(WebSockets)
    //for each client connection create a KTOR thread
    routing {
        // list of all clients
        val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
        //server listens on path /chat
        webSocket("/chat") {
            //when user connects log
            println("Adding user!")
            //create a connection instance using the Connection model
            val thisConnection = Connection(this)
            //add connection to list of all clients
            connections += thisConnection
            try {
                send("You are connected! There are ${connections.count()} users here.")
                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()
                    val textWithUsername = "[${thisConnection.name}]: $receivedText"
                    connections.forEach {
                        it.session.send(textWithUsername)
                    }
                }
            } catch (e: Exception) {
                println(e.localizedMessage)
            } finally {
                println("Removing $thisConnection!")
                connections -= thisConnection
            }
        }
    }
}

