package com.jetbrains.handson.chat.client.allClientsData

import com.jetbrains.handson.chat.client.OperatingParameters.Identity
import com.jetbrains.handson.chat.client.operatingParameters

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
            break
        }
    }

    fun Status(): Unit {
        var isCoord = "."
        for (client in listOf) {
            if (client.ID == Identity.fingerprint) {
                if (client.isCoord) {
                    isCoord = " and you are the coordinator my friend! 💏"
                    //TODO update operating parameters
                }
                break
            }
        }
        println("There are ${listOf.size} users here$isCoord")
    }

    //TODO check
    fun getCoordID(): String {
        for (client in listOf) {
            if (client.isCoord) return client.ID
        }
        return ""
    }

    fun printMembers(listOfMembers: MutableList<clientData> = listOf) {
        updateFingerprintTruncation()
        listOfMembers.forEach {
            if (it.ID == Identity.fingerprint) println("This is you:")
            println(
                """
                Member name ${it.name}
                ${it.name}s fingerprint ${it.ID.substring(0, fingerprintTruncation)}
                ${it.name}s shared IP address and port ${it.IP}:${it.port}
                ${it.name} is ${if (!it.isCoord) "not " else ""}the coordinator
            """.trimIndent()
            )
            println()
        }
        println("End of list of members.")
    }

    fun findMemberID(listOfMembers: MutableList<clientData> = listOf):String{
        val matchingMembers = mutableListOf<clientData>()
        print("Please enter the member you wish to message.\nYou can enter their (partial or full) name, fingerprint or ip address: ")
        val input = readln()
        for (client in listOfMembers){
            if (client.name.contains(input, true) or
                client.ID.contains(input,true) or
                client.IP.contains(input, true)) matchingMembers.add(client)
        }
        if (matchingMembers.size == 1) return matchingMembers.first().ID
        if (matchingMembers.isEmpty()) {
            println("There are no members matching you search query. Restarting")
            return findMemberID()
        }
        println("There multiple matches to your search:")
        println(matchingMembers)
        return findMemberID(matchingMembers)
    }
}