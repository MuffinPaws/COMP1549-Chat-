package com.jetbrains.handson.chat.server.connections

import io.ktor.http.cio.websocket.*

//Connection model
class Connection(val session: DefaultWebSocketSession, val clientData: ClientData) {
    // by default no connection/member is coordinator
    var isCoord = false
        set(value) {
            field = value
            clientData.isCoord = value
        }
}
