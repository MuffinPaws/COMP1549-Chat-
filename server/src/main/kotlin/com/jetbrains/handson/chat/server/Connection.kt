package com.jetbrains.handson.chat.server

import io.ktor.http.cio.websocket.*
import java.util.concurrent.atomic.AtomicInteger

//Connection model
class Connection(val session: DefaultWebSocketSession) {
    //singleton instance shared by all connections
    companion object {
        //sequential unique ID generator TODO change to client finger print
        var lastId = AtomicInteger(0)
    }
    //client DATA
    val name = "user${lastId.getAndIncrement()}"
}