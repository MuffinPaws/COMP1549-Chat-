package com.jetbrains.handson.chat.server

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.encodeToString
import java.time.Duration
import java.util.*

// args are collected from HOCON and passed to Netty server
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

// set of all client connections
val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())

@Suppress("unused")
fun Application.module(testing: Boolean = false) {
    // use KTOR websockets
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    // for all client connection create a KTOR thread
    routing {
        // the following line is used to test the Application.module() with /chat route
        get("/chat") { call.respondText("Hello, world!") }

        webSocket("/chat") {
            // add connection to set of connections
            val clientData: clientData = Json.decodeFromString((incoming.receive() as Frame.Text).readText())
            val thisConnection = Connection(this, clientData)
            connections += thisConnection
            // advise client of the just established connection
            thisConnection.session.send("CONNECTION ESTABLISHED")
            send(Json.encodeToString(clientData.getAllClients().toList()))
            println(Json.encodeToString(clientData.getAllClients().toList())) // TODO remove this
            try {
                // when user connects log
                println("Adding ${thisConnection.clientData.name}")
                // show the user that the connection as been established
                send("You are connected! There are ${connections.count()} users here.")
                // if only 1 user, that's the coordinator
                if (connections.count() == 1) {
                    send("You are the coordinator my friend!")
                    thisConnection.isCoord = true
                }

                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()
                    // if received text is a /command run its own function, else send the text to all the members
                    if (receivedText == "/members") {
                        getExistingMembers(thisConnection)
                    } else {
                        // text to be sent to all members
                        val sendersName = if (thisConnection.isCoord) "${thisConnection.clientData.name}-COORD"
                        else thisConnection.clientData.name
                        val textWithUsername = "[${sendersName}]: $receivedText"
                        connections.forEach {
                            it.session.send(receivedText)
                        }
                    }
                }
            } catch (e: Exception) {
                println(e.localizedMessage)
            } finally {
                // when client disconnects, log client leaving
                println("Removing ${thisConnection.clientData.name}")
                // remove that client's connection from the connections hashset
                connections -= thisConnection
                // if the COORD disconnects, then someone else has to take that role
                if (connections.isNotEmpty()) {
                    setNewCoord()
                }
            }
        }
    }
}


suspend fun setNewCoord() {
    /*
    This function checks the presence of a coordinator in the connections set.
    If there is not, sets that role to the first connection in the set.
     */
    var counter = 0
    connections.forEach { connection: Connection -> if (connection.isCoord) counter++ }
    if (counter == 0) {
        println("Setting ${connections.elementAt(0).clientData.name} as the new COORD...")
        connections.elementAt(0).isCoord = true
        connections.forEach { it.session.send("${connections.elementAt(0).clientData.name} is the new coordinator!") }
    }
}


suspend fun getExistingMembers(thisConnection: Connection) {
    /*
    This function checks if a member has requested the server (by using the /members command)
    to get the list of existing members.
    */
    var listOfExistingMembers = ""
    connections.forEach {
        listOfExistingMembers += "[name: ${it.clientData.name}, " +
                "coord: ${it.isCoord}, id: ciccio99, IP: 000, Port: 000]\n"
    }
    println("Sending list of existing members to ${thisConnection.clientData.name}...")
    thisConnection.session.send(listOfExistingMembers)
    thisConnection.session.send("End of list!")
}