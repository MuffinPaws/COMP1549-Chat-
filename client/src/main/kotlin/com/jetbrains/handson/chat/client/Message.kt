package com.jetbrains.handson.chat.client

import kotlinx.serialization.Serializable

@Serializable
data class Message(val data: String) {
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
}

//TODO fix comment bellow
// abstract class for providing a framework for storing T data
abstract class MessageBox<T>(t: T) {
    val data = t
    open fun serialise() {
        TODO("The serialise function has not been created😒")
    }
}

class TextMessageBox(data: String) : MessageBox<String>(data) {
    override fun serialise() {
        TODO("Not yet implemented")
    }

}

class FileMessageBox(data: String) : MessageBox<String>(data) {
    override fun serialise() {
        TODO("Not yet implemented")
    }

}

class PingMessageBox(ID: Int) : MessageBox<Int>(ID) {
    override fun serialise() {
        TODO("Not yet implemented")
    }

}