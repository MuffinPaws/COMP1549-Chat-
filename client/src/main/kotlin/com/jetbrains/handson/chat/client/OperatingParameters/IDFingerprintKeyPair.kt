package com.jetbrains.handson.chat.client.OperatingParameters

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.util.*

// TODO better obj name?
object IDFingerprintKeyPair {
    val ID: Pair<String, KeyPair>

    init {
        val generator = KeyPairGenerator.getInstance("RSA")
        generator.initialize(4096)

        val getHash = MessageDigest.getInstance("SHA3-512")

        val toBase64URL = Base64.getUrlEncoder().withoutPadding()::encodeToString

        val keyPair = generator.generateKeyPair()
        val fingerprint = toBase64URL(getHash.digest(keyPair.public.encoded))

        ID = Pair(fingerprint, keyPair)
    }
}