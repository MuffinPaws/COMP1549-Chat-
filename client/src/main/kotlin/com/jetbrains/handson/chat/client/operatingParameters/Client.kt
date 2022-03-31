package com.jetbrains.handson.chat.client.operatingParameters

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

// Client is a model for serialization of client data
@Serializable
data class Client(
        val name: String,
        @EncodeDefault val ID: String = Identity.ID,
        val IP: String,
        val port: Int)