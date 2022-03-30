package com.jetbrains.handson.chat.client

import com.jetbrains.handson.chat.client.OperatingParameters.Identity
import com.jetbrains.handson.chat.client.allClientsData.allClients
import com.jetbrains.handson.chat.client.allClientsData.clientData
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

@Serializable
data class Message(
    @EncodeDefault val fromID: String = Identity.fingerprint,
    val toID: String,
    val data: String,
    val type: AplicationDataType,
    @EncodeDefault val time: Long = System.currentTimeMillis()) {
    fun display() = when (type) {
        AplicationDataType.TEXT -> TextMessageBox(data).display()
        AplicationDataType.FILE -> println() //TODO impliment or remove
        AplicationDataType.PING -> println()
        AplicationDataType.CONFIG -> ConfigMessgaeBox(data).updateMemebers()
    }
}

object Messagaes{
    val messages = mutableMapOf<Message, Boolean>()

    fun put(message: Message, read: Boolean = false): Boolean? = messages.put(message, read)
}


//List of all possible message type the app can receive
enum class AplicationDataType {
    TEXT, FILE, PING, CONFIG;

    companion object {
        fun findDataType(name: String): AplicationDataType {
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
    override fun display() {
        println("Message is $data")
    }
}

class FileMessageBox(data: String) : MessageBox<String>(data) {

}

class PingMessageBox(ID: Int) : MessageBox<Int>(ID) {

}

class ConfigMessgaeBox(data: String) : MessageBox<String>(data) {
    fun updateMemebers(){
        val newClientLis = Json.decodeFromString<List<clientData>>(data)
        allClients.listOf.removeAll { true }
        allClients.listOf.addAll(newClientLis)
    }
}