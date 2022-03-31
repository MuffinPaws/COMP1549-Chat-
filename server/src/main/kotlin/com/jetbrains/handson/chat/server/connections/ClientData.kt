package com.jetbrains.handson.chat.server.connections

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
data class ClientData(
    val name: String,
    val ID: String,
    val IP: String,
    val port: Int,
    @EncodeDefault var isCoord: Boolean = false
) {
    companion object {
        val allClients = mutableSetOf<ClientData>()
    }

    init {
        allClients.add(this)
    }

    fun getAllClients() = Json.encodeToString(allClients.toList())
}