package com.jetbrains.handson.chat.client.message

// abstract class for providing a framework for storing Any data (generic)
abstract class MessageBox<T>(t: T) {
    val data = t
    open fun display() {
        TODO("The display function has not been created😒")
    }
}