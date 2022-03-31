package com.jetbrains.handson.chat.client

//List of all possible message type the app can receive
enum class ApplicationDataType {
    TEXT, FILE, CONFIG;

    companion object {
        fun findDataType(name: String): ApplicationDataType {
            for (enum in values()) {
                if (enum.name.equals(name, true)) return enum
            }
            return TEXT
        }
    }
}