package com.jetbrains.handson.chat.client

import kotlinx.serialization.Serializable

@Serializable
data class Message(val data: String, val type: AplicationDataType) {
    fun display() = when (type) {
        AplicationDataType.TEXT -> TextMessageBox(data).display()
        AplicationDataType.FILE -> TODO()
        AplicationDataType.PING -> TODO()
    }
}


//List of all possible message type the app can receive
enum class AplicationDataType {
    TEXT, FILE, PING, ;

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