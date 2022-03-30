package com.jetbrains.handson.chat.client

import com.jetbrains.handson.chat.client.OperatingParameters.IDFingerprintKeyPair
import kotlinx.serialization.Serializable

@Serializable
data class Message(val fromID: String = IDFingerprintKeyPair.ID.first, val toID: String, val data: String, val type: AplicationDataType) {
    fun display() = when (type) {
        AplicationDataType.TEXT -> TextMessageBox(data).display()
        AplicationDataType.FILE -> TODO()
        AplicationDataType.PING -> TODO()
        AplicationDataType.CONFIG -> TODO()
    }
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

}