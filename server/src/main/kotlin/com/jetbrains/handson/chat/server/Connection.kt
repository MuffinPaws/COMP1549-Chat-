package com.jetbrains.handson.chat.server

import io.ktor.http.cio.websocket.*

//Connection model
class Connection(val session: DefaultWebSocketSession, val clientData: clientData) {
    // by default no connection/member is coordinator
    var isCoord = false
        set(value) {
            field = value
            clientData.isCoord = value
        }
}
