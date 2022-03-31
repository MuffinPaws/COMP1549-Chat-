package com.jetbrains.handson.chat.client

import com.jetbrains.handson.chat.client.OperatingParameters.Identity
import com.jetbrains.handson.chat.client.allClientsData.allClients
import com.jetbrains.handson.chat.client.allClientsData.clientData
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

@Serializable
data class Message(
    @EncodeDefault val fromID: String = Identity.fingerprint,
    val toID: String,
    val data: String,
    val type: ApplicationDataType,
    @EncodeDefault val time: Long = System.currentTimeMillis()
) {
    fun display() = when (type) {
        ApplicationDataType.TEXT -> TextMessageBox(data).display(toID, time)
        ApplicationDataType.FILE -> println() //TODO impliment or remove
        ApplicationDataType.PING -> println()
        ApplicationDataType.CONFIG -> ConfigMessageBox(data).updateMembers()
    }

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


object Messages {
    val messages = mutableMapOf<Message, Boolean>()

    fun put(message: Message, read: Boolean = false): Boolean? {
        //TODO filter config and ping
        if (message.type == ApplicationDataType.PING) {
            messages.put(message, read)
            return true
        }
        if (message.type == ApplicationDataType.CONFIG) {
            message.display()
            return false
        }
        return messages.put(message, read)
    }

    fun read(isRead: Boolean = false) {
        for ((message, read) in messages) {
            if (message.type != ApplicationDataType.TEXT) continue
            if (read == isRead) message.display()
            messages[message] = true
        }
    }


}


//List of all possible message type the app can receive
enum class ApplicationDataType {
    TEXT, FILE, PING, CONFIG;

    companion object {
        fun findDataType(name: String): ApplicationDataType {
            for (enum in values()) {
                if (enum.name.equals(name, true)) return enum
            }
            return PING
        }
    }
}

//TODO fix comment bellow
// abstract class for providing a framework for storing T data
abstract class MessageBox<T>(t: T) {
    val data = t
    open fun display() {
        TODO("The display function has not been created😒")
    }
}

class TextMessageBox(data: String) : MessageBox<String>(data) {
    fun display(fromID: String, time: Long) {
        val fromMemberName = allClients.getMemberByID(fromID).name
        val timeString = DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochMilli(time))
        println(
            """
            Message from: $fromMemberName
            ${fromMemberName}s fingerprint: ${fromID.substring(0..allClients.getFingerprintTruncation())}
            Received at: $timeString
            Message:
            $data
        """.trimIndent()
        )
    }
}

class FileMessageBox(data: String) : MessageBox<String>(data) {

}

class PingMessageBox(ID: Int) : MessageBox<Int>(ID) {

}

class ConfigMessageBox(dataB64: String) : MessageBox<String>(dataB64) {
    private val dataParsed = String(Base64.getUrlDecoder().decode(dataB64))
    fun updateMembers() {
        val newClientLis = Json.decodeFromString<List<clientData>>(dataParsed)
        allClients.listOf.removeAll { true }
        allClients.listOf.addAll(newClientLis)
    }
}