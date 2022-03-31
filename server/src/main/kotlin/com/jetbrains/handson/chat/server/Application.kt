package com.jetbrains.handson.chat.server

import com.jetbrains.handson.chat.server.Connections.getAllClients
import com.jetbrains.handson.chat.server.Connections.setOf
import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.time.Duration

// args are collected from HOCON and passed to Netty server
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    // use KTOR websockets
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    // for all client connection create a KTOR routing scope coroutine
    routing {
        webSocket("/chat") {
            // parse client data
            val clientData: ClientData = Json.decodeFromString((incoming.receive() as Frame.Text).readText())
            // create connection 
            val thisConnection = Connection(this, clientData)
            // add connection to set of connections
            setOf += thisConnection
            try {
                // when user connects log
                println("Adding ${thisConnection.clientData.name}")
                // if only 1 user, that's the coordinator
                if (setOf.count() == 1) {
                    thisConnection.isCoord = true
                }
                //send to all clients list of all clients (including new member)
                Connections.broadcast(getAllClients())
                // for each incoming frame
                for (frame in incoming) {
                    try {
                        // check if frame is text
                        frame as? Frame.Text ?: continue
                        // extract text from frame
                        val receivedData = frame.readText()
                        // parse message header
                        val receivedMessage = Json.decodeFromString<Message>(receivedData)
                        // send message to recipient
                        Connections.send(receivedData, receivedMessage.toID)
                    } catch (e: Exception) {
                        println("Received malformed frame: " + e.localizedMessage)
                    }
                }
            } catch (e: Exception) {
                println(e.localizedMessage)
            } finally {
                // when client disconnects, log client leaving
                println("Removing ${thisConnection.clientData.name}")
                // remove that client's connection from the connections set
                setOf -= thisConnection
                // if the coordinator disconnects then find new coordinator
                if (setOf.isNotEmpty()) {
                    setNewCoord()
                }
                // inform all the clients with updates client lists
                Connections.broadcast(getAllClients())
            }
        }
    }
}

/*
This function checks the presence of a coordinator in the connections set.
If there is not, sets that role to the first connection in the set.
*/
fun setNewCoord() {
    var counter = false
    setOf.forEach { connection: Connection -> if (connection.isCoord) counter = true }
    if (counter) {
        println("Setting ${setOf.elementAt(0).clientData.name} as the new COORD...")
        setOf.elementAt(0).isCoord = true
    }
}