package com.jetbrains.handson.chat.server

import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.routing.*
import io.ktor.websocket.*
import java.util.*
import kotlin.collections.LinkedHashSet

// args are collected from HODEL and passed to Netty server
fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused")
fun Application.module() {
    // use KTOR websockets
    install(WebSockets)
    // for all client connection create a KTOR thread
    routing {
        // set of all client connections
        val connections = Collections.synchronizedSet<Connection?>(LinkedHashSet())
        webSocket("/chat") {
            // add connection to set of connections
            val thisConnection = Connection(this)
            connections += thisConnection
            // advise client of the just established connection
            thisConnection.session.send("CONNECTION ESTABLISHED")
            // get client info
            val clientInfo = (incoming.receive() as Frame.Text).readText().split(",")
            // set username with the one received,
            // if empty string: use the default sequential unique ID
            thisConnection.name = clientInfo.elementAt(0).substring(6).ifEmpty { thisConnection.name }

            try {
                // when user connects log
                println("Adding ${thisConnection.name}")
                // show the user that the connection as been established
                send("You are connected! There are ${connections.count()} users here.")
                // if only 1 user, that's the coordinator
                if (connections.count() == 1) {
                    send("You are the coordinator my friend!")
                    thisConnection.name += "-COORD"
                    thisConnection.coord = 1
                }

                for (frame in incoming) {
                    frame as? Frame.Text ?: continue
                    val receivedText = frame.readText()
                    // if received text is a /command run its own function, else send the text to all the members
                    if (receivedText == "/members") {
                        getExisistingMembers(connections, thisConnection)
                    } else {
                        // text to be sent to all members
                        val textWithUsername = "[${thisConnection.name}]: $receivedText"
                        connections.forEach {
                            it.session.send(textWithUsername)
                        }
                    }
                }
            } catch (e: Exception) {
                println(e.localizedMessage)
            } finally {
                // when client disconnects, log client leaving
                println("Removing ${thisConnection.name}")
                // remove that client's connection from the connections hashset
                connections -= thisConnection
                // if the COORD disconnects, then someone else has to take that role
                if (connections.isNotEmpty()) {
                    setNewCoord(connections)
                }
            }
        }
    }
}


suspend fun setNewCoord(connections: MutableSet<Connection>) {
    /*
    This function checks the presence of a coordinator in the connections set.
    If there is not, sets that role to the first connection in the set.
     */
    var counter = 0
    connections.forEach { connection: Connection -> if (connection.coord == 1) counter++ }
    if (counter == 0) {
        println("Setting ${connections.elementAt(0).name} as the new COORD...")
        connections.elementAt(0).coord = 1
        connections.forEach { it.session.send("${connections.elementAt(0).name} is the new coordinator!") }
        connections.elementAt(0).name += "-COORD"
    }
}


suspend fun getExisistingMembers(connections: MutableSet<Connection>, thisConnection: Connection) {
    /*
    This function checks if a member has requested the server (by using the /members command)
    to get the list of existing members.
    */
    var listOfExistingMembers = ""
    connections.forEach {
        listOfExistingMembers += "[name: ${it.name}, " +
                "coord: ${it.coord}, id: ciccio99, IP: 000, Port: 000]\n"
    }
    println("Sending list of existing members to ${thisConnection.name}...")
    thisConnection.session.send(listOfExistingMembers)
    thisConnection.session.send("End of list!")
}