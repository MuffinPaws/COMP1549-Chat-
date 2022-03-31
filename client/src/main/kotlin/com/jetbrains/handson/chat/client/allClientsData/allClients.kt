package com.jetbrains.handson.chat.client.allClientsData

import com.jetbrains.handson.chat.client.OperatingParameters.Identity

private const val MIN_DISPLAY_LENGHTH = 6 //TODO or 4?

object allClients {
    val listOf = mutableListOf<clientData>()
    private var fingerprintTruncation = MIN_DISPLAY_LENGHTH

    fun updateFingerprintTruncation() {
        var index: Int = MIN_DISPLAY_LENGHTH
        while (true) {
            val used = mutableListOf<String>()
            for (client in listOf) {
                val fingerprint = client.ID
                val subString = fingerprint.substring(0, index)
                if (used.contains(subString)) {
                    index++
                    continue
                }
                used.add(subString)
            }
            fingerprintTruncation = index
        }
    }

    fun Status(): Unit {
        var isCoord = "."
        for (client in listOf) {
            if (client.ID == Identity.fingerprint) {
                if (client.isCoord) {
                    isCoord = "and you are the coordinator my friend! 💏"
                    //TODO update operating parameters
                }
                break
            }
        }
        print("There are ${listOf.size} users here$isCoord")
    }

    //TODO check
    fun getCoordID(): String {
        for (client in listOf) {
            if (client.isCoord) return client.ID
        }
        return ""
    }

    fun getMembers() {
        listOf.forEach {
            println("Memebr")
        }
    }
}