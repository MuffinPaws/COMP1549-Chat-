package com.jetbrains.handson.chat.server

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

@Serializable
data class clientData(
    val name: String, val ID: String, val IP: String, val port: Int, @EncodeDefault var isCoord: Boolean = false
)