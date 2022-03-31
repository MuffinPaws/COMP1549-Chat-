package com.jetbrains.handson.chat.server

import io.ktor.http.cio.websocket.*

//Connection is a model for deserialization of a Connection
class Connection(val session: DefaultWebSocketSession, val clientData: ClientData) {
    // by default no connection/member is coordinator
    var isCoord = false
        //When the state of coordinator is change also update corresponding ClientData
        set(value) {
            field = value
            clientData.isCoord = value
        }
}
