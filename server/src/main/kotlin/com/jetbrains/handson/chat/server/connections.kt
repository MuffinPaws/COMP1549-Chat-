package com.jetbrains.handson.chat.server

import io.ktor.http.cio.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.*

object connections {
    // set of all client connections
    val setOf = Collections.synchronizedSet<Connection?>(LinkedHashSet())
    fun getAllClients(): String {
        val listOfClients = mutableListOf<clientData>()
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