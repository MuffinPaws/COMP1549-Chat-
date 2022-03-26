package com.jetbrains.handson.chat.server

import io.ktor.http.cio.websocket.*
import java.util.concurrent.atomic.AtomicInteger

class Connection(val session: DefaultWebSocketSession) {
    companion object {
        var lastId = AtomicInteger(0)
    }
    val name = "user${lastId.getAndIncrement()}"
}

/*
 Note that we are using AtomicInteger as a thread-safe data structure for the counter.
 ensures that two users will never receive the same ID for their username –
 even when their two Connection objects are created simultaneously on separate threads.
 */