package com.jetbrains.handson.chat.client.allClientsData

import kotlinx.serialization.Serializable

// Message is a model for deserialization of
@Serializable
data class clientData(val name: String, val ID: String, val IP: String, val port: Int, val isCoord: Boolean = false)

