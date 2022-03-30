package com.jetbrains.handson.chat.client

import com.jetbrains.handson.chat.client.OperatingParameters.OperatingParameters
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
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
        for (frame in incoming) {
            try {
                //TODO change to any data type
                frame as? Frame.Text ?: continue
                val message = Json.decodeFromString<Message>(frame.readText())
                // print frame parsed from server
                Messagaes.put(message)
                //message.display()
            } catch (e: Exception) {
                println("Info while receiving: " + e.localizedMessage)
            }

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
        println(
            """
                Type 'exit' or 'quit' to close the program. 
                type 'read' to read messages
                type 'send' to type a new message (Press Enter to send it)
                type 'history' to fetch messages history
                type 'members' to list all members
            """.trimIndent()
        )
        val exit = { x: String ->
            println("${x}ting")
            println("Connection closed. Goodbye!")
            exitProcess(0)
        }
        when (readln()) {
            "exit" -> exit("Exit")
            "quit" -> exit("Quit")
            "members" -> continue // TODO implement
            "send" -> print("Please type your massage: ")
            else -> {
                println("unknown command")
                continue
            }
        }
        val message = readln()
        if (message.isBlank()) continue
        // member can request existing members
        try {
            // send what you typed
            val messageP = Message(toID = "hdsfg", data = message, type = AplicationDataType.TEXT)
            send(Json.encodeToString(messageP))
        } catch (e: Exception) {
            println("Error while sending: " + e.localizedMessage)
            return
        }
    }
}

