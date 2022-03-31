package com.jetbrains.handson.chat.server.connections

import io.ktor.http.cio.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

object ConnectionsSet {
    // set of all client ConnectionsSet
    val setOf = Collections.synchronizedSet<Connection?>(LinkedHashSet())
    fun getAllClients(): String {
        val listOfClients = mutableListOf<ClientData>()
        setOf.forEach { listOfClients.add(it.clientData) }
        val toBase64URL = Base64.getUrlEncoder().withoutPadding()::encodeToString
        return """
            {"fromID":"server","toID":"init","data":"${toBase64URL(Json.encodeToString(listOfClients.toList()).toByteArray())}","type":"CONFIG","time":${System.currentTimeMillis()}}
        """.trimIndent()
    }

    suspend fun broadcast(message: String): Unit {
        setOf.forEach {
            it.session.send(message)
        }
    }
}