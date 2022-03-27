package com.jetbrains.handson.chat.server

import io.ktor.server.application.*
import io.ktor.websocket.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import java.util.*

//args are collected from HODEL and passed to Netty server
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    //use KTOR websockets
    install(WebSockets)
    //for all client connection create a KTOR thread
    routing {
        // list of all clients
        val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
        //server listens on path /chat
        //for each client connection creat a KTOR websockt thread
        webSocket("/chat") {
            //when user connects log
            println("Adding user!")
            //create a connection instance using the Connection model
            val thisConnection = Connection(this)
            //add connection to list of all clients
            connections += thisConnection
            try {
                //Notify new client state all connected clients
                send("You are connected! There are ${connections.count()} users here.")
                // for each incoming frame (message)
                for (frame in incoming) {
                    //TODO change to any incoming data
                    frame as? Frame.Text ?: continue
                    //parse data and format data
                    val receivedText = frame.readText()
                    val textWithUsername = "[${thisConnection.name}]: $receivedText"
                    //TODO change to 1 to 1 messaging
                    connections.forEach {
                        it.session.send(textWithUsername)
                    }
                }
            } catch (e: Exception) {
                //log what went wrong
                println(e.localizedMessage)
            } finally {
                // when client disconnects
                // log client leaving
                println("Removing $thisConnection!")
                //TODO change to mark client as offline
                connections -= thisConnection
            }
        }
    }
}

