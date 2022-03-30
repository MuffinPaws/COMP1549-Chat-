package com.jetbrains.handson.chat.server

import kotlinx.serialization.Serializable

@Serializable
data class clientData(val name: String, val ID: String, val IP: String, val port: Int) {
    companion object {
        val allClients = mutableSetOf<clientData>()
    }

    init {
        allClients.add(this)
    }

    fun getAllClients() = allClients
}