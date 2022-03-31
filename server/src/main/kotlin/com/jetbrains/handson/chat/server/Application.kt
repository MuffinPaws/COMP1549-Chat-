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
    // for all client connection create a KTOR thread
    routing {
        webSocket("/chat") {
            // add connection to set of connections
            val clientData: clientData = Json.decodeFromString((incoming.receive() as Frame.Text).readText())
            val thisConnection = Connection(this, clientData)
            setOf += thisConnection
            // advise client of the just established connection
            println(getAllClients()) // TODO remove this
            try {
                // when user connects log
                println("Adding ${thisConnection.clientData.name}")
                // if only 1 user, that's the coordinator
                if (setOf.count() == 1) {
                    thisConnection.isCoord = true
                }
                //send to all clients list of all clients (including new member)
                Connections.broadcast(getAllClients())
                for (frame in incoming) {
                    try {
                        frame as? Frame.Text ?: continue
                        val receivedData = frame.readText()
                        val receivedMessage = Json.decodeFromString<Message>(receivedData)
                        // text to be sent to all members
                        //TODO change to 1 to 1
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
                // remove that client's connection from the connections hashset
                setOf -= thisConnection
                // inform all clients of dismember
                // if the COORD disconnects, then someone else has to take that role
                if (setOf.isNotEmpty()) {
                    setNewCoord()
                }
                Connections.broadcast(getAllClients())
            }
        }
    }
}


fun setNewCoord() {
    /*
    This function checks the presence of a coordinator in the connections set.
    If there is not, sets that role to the first connection in the set.
     */
    var counter = 0
    setOf.forEach { connection: Connection -> if (connection.isCoord) counter++ }
    if (counter == 0) {
        println("Setting ${setOf.elementAt(0).clientData.name} as the new COORD...")
        setOf.elementAt(0).isCoord = true
    }
}


//suspend fun getExisistingMembers(connections: MutableSet<Connection>, thisConnection: Connection) {
//    /*
//    This function checks if a member has requested the server (by using the /members command)
//    to get the list of existing members.
//    */
//    var listOfExistingMembers = ""
//    connections.forEach {
//        listOfExistingMembers += "[name: ${it.clientData.name}, " +
//                "coord: ${it.isCoord}, id: ciccio99, IP: 000, Port: 000]\n"
//    }
//    println("Sending list of existing members to ${thisConnection.clientData.name}...")
//    thisConnection.session.send(listOfExistingMembers)
//    thisConnection.session.send("End of list!")
//}