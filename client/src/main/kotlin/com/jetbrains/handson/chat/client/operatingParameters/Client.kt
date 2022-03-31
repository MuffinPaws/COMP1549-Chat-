package com.jetbrains.handson.chat.client.operatingParameters

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

@Serializable
data class Client(
    val name: String,
    @EncodeDefault val ID: String = Identity.fingerprint,
    val IP: String,
    val port: Int
)