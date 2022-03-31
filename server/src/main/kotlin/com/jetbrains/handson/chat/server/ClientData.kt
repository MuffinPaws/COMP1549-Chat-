package com.jetbrains.handson.chat.server

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

// ClientData is a model for deserialization of message headers
@Serializable
data class ClientData(
    val name: String, val ID: String, val IP: String, val port: Int, @EncodeDefault var isCoord: Boolean = false
)