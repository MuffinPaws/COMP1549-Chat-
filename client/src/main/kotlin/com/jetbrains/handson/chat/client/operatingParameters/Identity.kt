package com.jetbrains.handson.chat.client.operatingParameters

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.util.*

// TODO better obj name?
object Identity {
    val fingerprint: String
    val key: KeyPair

    init {
        val generator = KeyPairGenerator.getInstance("RSA")
        generator.initialize(4096)

        val getHash = MessageDigest.getInstance("SHA3-512")

        val toBase64URL = Base64.getUrlEncoder().withoutPadding()::encodeToString

        key = generator.generateKeyPair()
        fingerprint = toBase64URL(getHash.digest(key.public.encoded))
    }
}