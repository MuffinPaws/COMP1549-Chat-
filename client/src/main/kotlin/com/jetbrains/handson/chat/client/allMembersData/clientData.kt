package com.jetbrains.handson.chat.client.allMembersData

import kotlinx.serialization.Serializable

// Message is a model for deserialization of clientData
@Serializable
data class clientData(val name: String, val ID: String, val IP: String, val port: Int, val isCoord: Boolean = false)

