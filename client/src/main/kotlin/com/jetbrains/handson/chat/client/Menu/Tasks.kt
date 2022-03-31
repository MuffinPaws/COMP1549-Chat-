package com.jetbrains.handson.chat.client.Menu

enum class Tasks{
    EXIT,
    QUIT,
    SEND,
    READ,
    HISTORY,
    MEMBERS,
    HELP,
    UNKNOWN;

    companion object{
        fun findTask(input:String): Tasks {
            for (enum in values()){
                if (enum.name.equals(input, true)) return enum
            }
            return UNKNOWN
        }
    }
}