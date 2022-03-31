package com.jetbrains.handson.chat.client.allClientsData

import com.jetbrains.handson.chat.client.OperatingParameters.Identity

object allClients {
    val listOf = mutableListOf<clientData>()
    fun Status(): Unit {
        var isCoord = ""
        for (client in listOf){
            if (client.ID == Identity.fingerprint){
                if (client.isCoord){
                    isCoord = " You are the coordinator my friend! 💏"
                }
                break
            }
        }
        print("There are ${listOf.size} users here.$isCoord")
    }
}