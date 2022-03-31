package com.jetbrains.handson.chat.client.Message

import com.jetbrains.handson.chat.client.allClientsData.allClients
import com.jetbrains.handson.chat.client.allClientsData.clientData
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.*

// Functions to process CONFIG messages
class ConfigMessageBox(dataB64: String) : MessageBox<String>(dataB64) {
    private val dataParsed = String(Base64.getUrlDecoder().decode(dataB64))
    fun updateMembers() {
        val newClientLis = Json.decodeFromString<List<clientData>>(dataParsed)
        allClients.listOf.removeAll { true }
        allClients.listOf.addAll(newClientLis)
    }
}