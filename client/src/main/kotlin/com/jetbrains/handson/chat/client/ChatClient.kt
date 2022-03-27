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

//TODO parse args
fun main() {
    //use KTOR client web sockets
    val client = HttpClient {
        install(WebSockets)
    }
    //run blocking tread/container/instance (nothing else at the same time) TODO check if comment correct
    runBlocking {
        //create client websocket instance TODO add client init parameters
        client.webSocket(method = HttpMethod.Get, host = "127.0.0.1", port = 8080, path = "/chat") {
            while(true) {
                //parse incoming message
                val othersMessage = incoming.receive() as? Frame.Text ?: continue
                //print incoming message
                println(othersMessage.readText())
                //read user input
                val myMessage = readLine()
                //send message if not null
                if(myMessage != null) {
                    send(myMessage)
                }
            }
        }
    }
    //release system resources TODO try with resources
    client.close()
    println("Connection closed. Goodbye!")
}

suspend fun DefaultClientWebSocketSession.inputMessages() {
    try {
        //for each incoming message
        for (message in incoming) {
            //TODO change to any data type
            message as? Frame.Text ?: continue
            println(message.readText())
        }
    } catch (e: Exception) {
        //log error
        println("Error while receiving: " + e.localizedMessage)
    }
}

suspend fun DefaultClientWebSocketSession.outputMessages() {
    while (true) {
        //for each user input
        //TODO change
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
        echo("Starting App")

        echo("Verifying client info")
        //TODO check if client IP is okay
        //TODO check if client Port is okay

        echo("Verifying server info")
        //TODO check if server IP is okay
        //TODO check if server Port is okay
        //TODO check if server reachable

        echo("generating your fingerprint")
        //TODO generate client ID object
    }

}

object verifyIP{
    //TODO add
}
