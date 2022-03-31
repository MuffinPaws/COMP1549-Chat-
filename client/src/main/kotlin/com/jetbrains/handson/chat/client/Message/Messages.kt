package com.jetbrains.handson.chat.client.Message

object Messages {
    val messages = mutableMapOf<Message, Boolean>()

    fun put(message: Message, read: Boolean = false): Boolean? {
        //TODO filter config and ping
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