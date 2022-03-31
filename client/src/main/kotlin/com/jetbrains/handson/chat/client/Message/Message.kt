package com.jetbrains.handson.chat.client.Message

import com.jetbrains.handson.chat.client.OperatingParameters.Identity
import com.jetbrains.handson.chat.client.allClientsData.allClients
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable

// Message data class
@Serializable
data class Message(
    @EncodeDefault val fromID: String = Identity.fingerprint,
    val toID: String,
    val data: String,
    val type: ApplicationDataType,
    @EncodeDefault val time: Long = System.currentTimeMillis()
) {

    // Display message
    fun display() = when (type) {
        ApplicationDataType.TEXT -> TextMessageBox(data).display(fromID, time)
        ApplicationDataType.FILE -> println() //TODO For future implementation
        ApplicationDataType.CONFIG -> ConfigMessageBox(data).updateMembers()
    }

    // Share functions for all messages
    companion object {
        fun message1to1(): MutableList<Message> {
            val message = mutableListOf<Message>()
            val toID = allClients.findMemberID()
            val input = getInput()
            message.add(Message(toID = toID, data = input, type = ApplicationDataType.TEXT))
            return message
        }

        fun messageBroadcast(): MutableList<Message> {
            val messages = mutableListOf<Message>()
            val input = getInput()
            for (client in allClients.listOf){
                if (client.ID == Identity.fingerprint) continue
                messages.add(Message(toID = client.ID, data = input, type = ApplicationDataType.TEXT))
            }
            return messages
        }

        private fun getInput(): String {
            input@ while (true) {
                print("Please type your massage: ")
                val input = readln()
                // if input is blank double check
                if (input.isBlank()) {
                    while (true) {
                        print("Your message is blank. Are you sure you want to send a blank message? (enter yes or no): ")
                        when (readln().lowercase().first()) {
                            'y' -> break
                            'n' -> continue@input
                            else -> println("unknown command🥴")
                        }
                    }
                }
                return input
            }
        }
    }
}


