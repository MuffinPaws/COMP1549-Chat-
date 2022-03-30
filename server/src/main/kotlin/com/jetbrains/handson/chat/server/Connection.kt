package com.jetbrains.handson.chat.server

import io.ktor.http.cio.websocket.*
import java.util.concurrent.atomic.AtomicInteger

//Connection model
//TODO add val ID drop companion
class Connection(val session: DefaultWebSocketSession, val clientData: clientData) {

    // by default no connection/member is coordinator (set to zero)
    var coord = 0

}

/*
 Note that we are using AtomicInteger as a thread-safe data structure for the counter.
 ensures that two users will never receive the same ID for their username –
 even when their two Connection objects are created simultaneously on separate threads.
 */