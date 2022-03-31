package com.jetbrains.handson.chat.client.allMembersData

import com.jetbrains.handson.chat.client.OperatingParameters.Identity

private const val MIN_ID_DISPLAY_LENGTH = 5

// Singleton object for storing info about all Members
object AllMembers {
    val listOfAllMembers = mutableListOf<clientData>()
    private var IDTruncationLength = MIN_ID_DISPLAY_LENGTH

    private fun updateIDTruncation() {
        var index: Int = MIN_ID_DISPLAY_LENGTH
        while (true) {
            val used = mutableListOf<String>()
            for (client in listOfAllMembers) {
                val fingerprint = client.ID
                val subString = fingerprint.substring(0, index)
                if (used.contains(subString)) {
                    index++
                    continue
                }
                used.add(subString)
            }
            IDTruncationLength = index
            break
        }
    }

    fun getFingerprintTruncation() = IDTruncationLength

    fun status() {
        var isCoord = "."
        for (client in listOfAllMembers) {
            if (client.ID == Identity.fingerprint) {
                if (client.isCoord) {
                    isCoord = " and you are the coordinator my friend! 💏"
                }
                break
            }
        }
        println("There are ${listOfAllMembers.size} users here$isCoord")
    }

    fun printMembers(listOfMembers: MutableList<clientData> = listOfAllMembers) {
        updateIDTruncation()
        listOfMembers.forEach {
            if (it.ID == Identity.fingerprint) println("This is you:")
            println(
                """
                Member name ${it.name}
                ${it.name}s fingerprint ${it.ID.substring(0, IDTruncationLength)}
                ${it.name}s shared IP address and port ${it.IP}:${it.port}
                ${it.name} is ${if (!it.isCoord) "not " else ""}the coordinator
            """.trimIndent()
            )
            println()
        }
        println("End of list of members.")
    }

    fun findMemberID(listOfMembers: MutableList<clientData> = listOfAllMembers):String{
        val matchingMembers = mutableListOf<clientData>()
        print("Please enter the member you wish to message.\nYou can enter their (partial or full) name, fingerprint or ip address: ")
        val input = readln()
        for (client in listOfMembers){
            if (client.name.contains(input, true) or
                client.ID.contains(input,true) or
                client.IP.contains(input, true)) matchingMembers.add(client)
        }
        if (matchingMembers.size == 1) {
            println("Matched")
            printMembers(matchingMembers)
            return matchingMembers.first().ID
        }
        if (matchingMembers.isEmpty()) {
            println("There are no members matching you search query. Restarting")
            return findMemberID()
        }
        println("There multiple matches to your search:")
        printMembers(matchingMembers)
        return findMemberID(matchingMembers)
    }

    fun getMemberByID(ID:String): clientData {
        listOfAllMembers.forEach { if (it.ID == ID) return it }
        return clientData("DISCONNECTED MEMBER", ID, "::!", 1234)
    }
}