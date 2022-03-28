package com.jetbrains.handson.chat.client

data class Message(val data:String){
    //List of all possible message type the app can receive
    enum class AplicationDataType{

    }
}

//TODO fix comment bellow
// abstract class for providing a framework for storing T data
open class messageBox<T>(data: T){
    // TODO Need to check with bellissimo and samuele about how they are handing incoming frames
    // Is it only AS Text or Any?
    open fun serialise(){
        TODO("The serialise function has not been created😒")
    }
}