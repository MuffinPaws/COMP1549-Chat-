package com.jetbrains.handson.chat.client.allClientsData

import kotlinx.serialization.Serializable

@Serializable
data class ClientData(
    val name: String,
    val ID: String,
    val IP: String,
    val port: Int,
    val isCoord: Boolean = false
)

