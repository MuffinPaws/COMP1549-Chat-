package com.jetbrains.handson.chat.client

import com.jetbrains.handson.chat.client.OperatingParameters.OperatingParameters
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.system.exitProcess

val operatingParameters = OperatingParameters()

fun main(args: Array<String>) {
    // Parse CLI Parameters
    operatingParameters.main(args)
    // use KTOR client web sockets
    val client = HttpClient {
        install(WebSockets)
    }
    //run blocking tread/container/instance (nothing else at the same time) TODO check if comment correct
    runBlocking {
        //create client websocket instance
        client.webSocket(
                method = HttpMethod.Get,
                host = operatingParameters.serverIP,
                port = operatingParameters.serverPort,
                path = "/chat"
        )
        {
            val messageOutputRoutine = launch { outputMessages() }
            val userInputRoutine = launch { inputMessages() }

            userInputRoutine.join() // Wait for completion; either "exit" or error
            messageOutputRoutine.cancelAndJoin()
        }
    }
    //release system resources
    client.close()
    println("Connection closed. Goodbye!")
}

// Double check this part
suspend fun DefaultClientWebSocketSession.outputMessages() {
    try {
        //for each incoming message
        for (message in incoming) {
            //TODO change to any data type
            message as? Frame.Text ?: continue

            // print message parsed from server
            println(message.readText())

        }
    } catch (e: Exception) {
        //log error
        println("Info while receiving: " + e.localizedMessage)
    }
}

suspend fun DefaultClientWebSocketSession.inputMessages() {
    //TODO Init client config message and serialize
    send(operatingParameters.clientData)
    while (true) {
        //for each user input
        //TODO change
        val message = readLine()
        if (message.isNullOrBlank()) continue
        if (message.equals("exit", true)) exitProcess(0)
        if (message.equals("quit", true)) {
            println("Connection closed. Goodbye!")
            exitProcess(0)
        }
        // member can request existing members
        if (message.equals("/members")) {
            println("Returning existing members from server's set...")
            send("/members")
        } else {
            try {
                // send what you typed
                send(message)
            } catch (e: Exception) {
                println("Error while sending: " + e.localizedMessage)
                return
            }
        }
    }
}

