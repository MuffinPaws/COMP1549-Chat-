package com.jetbrains.handson.chat.client.commandsMenu

enum class MenuTasks{
    EXIT,
    QUIT,
    SEND,
    READ,
    HISTORY,
    MEMBERS,
    HELP,
    UNKNOWN;

    companion object{
        fun findTask(input:String): MenuTasks {
            for (enum in values()){
                if (enum.name.equals(input, true)) return enum
            }
            return UNKNOWN
        }
    }
}