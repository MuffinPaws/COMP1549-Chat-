package com.jetbrains.handson.chat.server

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val fromID: String,
    val toID: String,
    val data: String,
    val type: String,
    @EncodeDefault val time: Long = System.currentTimeMillis()
)