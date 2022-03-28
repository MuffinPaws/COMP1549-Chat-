package com.jetbrains.handson.chat.client

data class Message(val data:String){
    enum class dataType{

    }
}

open class messageBox<T>(data: T){
    open fun serialise(){
        TODO("The serialise function has not been created😒")
    }
}