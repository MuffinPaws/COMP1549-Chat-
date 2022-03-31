package com.jetbrains.handson.chat.client.message

import com.jetbrains.handson.chat.client.allMembersData.AllMembers
import com.jetbrains.handson.chat.client.allMembersData.clientData
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.*

// Functions to process CONFIG messages
class ConfigMessageBox(dataB64: String) : MessageBox<String>(dataB64) {
    private val dataParsed = String(Base64.getUrlDecoder().decode(dataB64))
    fun updateMembers() {
        val newClientLis = Json.decodeFromString<List<clientData>>(dataParsed)
        AllMembers.listOfAllMembers.removeAll { true }
        AllMembers.listOfAllMembers.addAll(newClientLis)
    }
}