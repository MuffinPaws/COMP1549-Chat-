package com.jetbrains.handson.chat.server

import io.ktor.http.cio.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

object Connections {
    // set of all client connections
    val setOf = Collections.synchronizedSet<Connection?>(LinkedHashSet())

    // return Message Json object containing info about all clients
    fun getAllClients(): String {
        val listOfClients = mutableListOf<ClientData>()
        setOf.forEach { listOfClients.add(it.clientData) }
        val toBase64URL = Base64.getUrlEncoder().withoutPadding()::encodeToString
        val data = toBase64URL(Json.encodeToString(listOfClients.toList()).toByteArray())
        return Json.encodeToString(Message("server", "init", data, "CONFIG"))
    }

    // send message to all clients
    suspend fun broadcast(message: String) {
        setOf.forEach {
            it.session.send(message)
        }
    }

    // send message to client with matching ID
    suspend fun send(message: String, Id: String) {
        for (connection in setOf) {
            if (connection.clientData.ID == Id) {
                connection.session.send(message)
                break
            }
        }
    }
}