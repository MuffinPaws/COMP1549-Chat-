package com.jetbrains.handson.chat.server

import io.ktor.http.cio.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

object Connections {
    // set of all client connections
    val setOf = Collections.synchronizedSet<Connection?>(LinkedHashSet())
    fun getAllClients(): String {
        val listOfClients = mutableListOf<clientData>()
        setOf.forEach { listOfClients.add(it.clientData) }
        val toBase64URL = Base64.getUrlEncoder().withoutPadding()::encodeToString
        //TODO use Message data class to wrap
        val data = toBase64URL(Json.encodeToString(listOfClients.toList()).toByteArray())
        return Json.encodeToString(Message("server", "init", data, "CONFIG"))
    }

    suspend fun broadcast(message: String) {
        setOf.forEach {
            it.session.send(message)
        }
    }

    suspend fun send(message: String, Id: String) {
        for (connection in setOf) {
            if (connection.clientData.ID == Id) {
                connection.session.send(message)
                break
            }
        }
    }
}