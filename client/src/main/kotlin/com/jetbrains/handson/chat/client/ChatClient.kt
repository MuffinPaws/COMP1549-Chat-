package com.jetbrains.handson.chat.client

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.types.int
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.*

fun main() {
    val client = HttpClient {
        install(WebSockets)
    }
    runBlocking {
        client.webSocket(method = HttpMethod.Get, host = "127.0.0.1", port = 8080, path = "/chat") {
            while (true) {
                val othersMessage = incoming.receive() as? Frame.Text ?: continue
                println(othersMessage.readText())
                val myMessage = readLine()
                if (myMessage != null) {
                    send(myMessage)
                }
            }
        }
    }
    client.close()
    println("Connection closed. Goodbye!")
}

suspend fun DefaultClientWebSocketSession.inputMessages() {
    try {
        for (message in incoming) {
            message as? Frame.Text ?: continue
            println(message.readText())
        }
    } catch (e: Exception) {
        println("Error while receiving: " + e.localizedMessage)
    }
}

suspend fun DefaultClientWebSocketSession.outputMessages() {
    while (true) {
        val message = readLine() ?: ""
        if (message.equals("exit", true)) return
        try {
            send(message)
        } catch (e: Exception) {
            println("Error while sending: " + e.localizedMessage)
            return
        }
    }
}

class OperatingParameters : CliktCommand() {
    val name: String by option(help = "Your display name").prompt("What is your display name")
    val serverIP: String by option(help = "The server's IP address ").prompt("What is the server's IP address")
    val serverPort: Int by option(help="The server's port").int().prompt("What is the port at the server")
    val clientIP: String by option(help = "Which IP address to use").prompt("What is the IP address you want to use")
    val clientPort: Int by option(help="Which port to use").int().prompt("What is the port you want to use")

    override fun run() {
        TODO("Not yet implemented")
    }

}