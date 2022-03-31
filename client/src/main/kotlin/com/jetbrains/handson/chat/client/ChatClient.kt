package com.jetbrains.handson.chat.client

import com.jetbrains.handson.chat.client.menu.Menu
import com.jetbrains.handson.chat.client.menu.Tasks
import com.jetbrains.handson.chat.client.message.Message
import com.jetbrains.handson.chat.client.message.Messages
import com.jetbrains.handson.chat.client.operatingParameters.OperatingParameters
import com.jetbrains.handson.chat.client.allMembersData.AllMembers
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
    //runBlocking means that the thread that runs it gets blocked for the duration of the call, until all the coroutines inside
    try {
        runBlocking {
            //create client websocket instance
            client.webSocket(
                method = HttpMethod.Get,
                host = operatingParameters.serverIP,
                port = operatingParameters.serverPort,
                path = "/chat"
            )
            {
                // launch coroutines for incoming and outgoing messages
                val messageOutputRoutine = launch { outputMessages() }
                val userInputRoutine = launch { inputMessages() }

                // join the sending messages coroutine
                userInputRoutine.join() // Wait for completion; either "exit" or error
                messageOutputRoutine.cancelAndJoin()
            }
        }
    } catch (e: Exception) {
        println("Something went wrong when connecting with server. Maybe you or the server is offline.")
        println("Details of issue:\n" + e.localizedMessage)
    } finally {
        //release system resources
        client.close()
        println("Connection closed. 😿 Goodbye! 👋")
    }
}

// Coroutine for incoming messages
suspend fun DefaultClientWebSocketSession.outputMessages() {
    try {
        //for each incoming message
        for (frame in incoming) {
            try {
                // parse incoming frame
                frame as? Frame.Text ?: continue
                val message = Json.decodeFromString<Message>(frame.readText())
                // store message
                Messages.put(message)
                // if listening is enabled print status and read message
                if (operatingParameters.listening) {
                    AllMembers.status()
                    Messages.read()
                }
            } catch (e: Exception) {
                // log error
                println("Info while receiving: " + e.localizedMessage)
            }

        }
    } catch (e: Exception) {
        // log error
        println("Info while receiving: " + e.localizedMessage)
    }
}

// Coroutine for handling user commands (send messages, read messages, get help info and activate listen mode)
suspend fun DefaultClientWebSocketSession.inputMessages() {
    // Send to server this clients info
    send(operatingParameters.clientData)
    println("Loading ⏳")
    //loop to wait for list of all clients from server
    while (AllMembers.listOfAllMembers.isEmpty()) {
        Thread.sleep(10)
    }
    println("You are connected!")
    //for each user command
    input@ while (true) {
        // Print status
        AllMembers.status()
        // When in listening mode await interrupt from user
        if (operatingParameters.listening) readln()
        // get user command
        val task = Menu.getTask()
        // lamda for exit message
        val exit = { x: String ->
            println("${x}ting")
            println("Connection closed. Goodbye!")
            exitProcess(0)
        }
        // do user task
        when (task) {
            Tasks.EXIT -> exit("Exi")
            Tasks.QUIT -> exit("Quit")
            Tasks.SEND -> println("Loading ⏳")
            Tasks.READ -> {
                Messages.read()
                continue
            }
            Tasks.HISTORY -> {
                Messages.read(true)
                continue
            }
            Tasks.MEMBERS -> {
                AllMembers.printMembers()
                continue
            }
            Tasks.LISTEN -> {
                operatingParameters.listening = !operatingParameters.listening
                continue
            }
            else -> {
                println("Error parsing task input.🤦 Please try again.")
                continue
            }
        }
        // asking to start a conversation private or broadcast
        print("Do you want to send a 'broadcast' or 'private' message: ")
        val messages = when (readln()) {
            "broadcast" -> Message.messageBroadcast()
            "private" -> Message.message1to1()
            else -> {
                println("Error parsing task input.🤦 Please try again.")
                continue
            }
        }
        // Send each message
        messages.forEach {
            try {
                send(Json.encodeToString(it))
            } catch (e: Exception) {
                println("Error while sending: " + e.localizedMessage)
                return
            }
        }
    }
}

