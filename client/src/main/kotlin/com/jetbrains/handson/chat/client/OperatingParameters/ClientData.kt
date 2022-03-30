package com.jetbrains.handson.chat.client.OperatingParameters

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

@Serializable
data class clientData(
        val name: String,
        @EncodeDefault val ID: String = IDFingerprintKeyPair.fingerprint,
        val IP: String,
        val port: Int)