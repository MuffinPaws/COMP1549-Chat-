package com.jetbrains.handson.chat.client

import com.jetbrains.handson.chat.client.Menu.Menu
import com.jetbrains.handson.chat.client.Menu.Tasks
import com.jetbrains.handson.chat.client.OperatingParameters.OperatingParameters
import com.jetbrains.handson.chat.client.allClientsData.allClients
import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.http.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.selects.select
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
    println("Connection closed. 😿 Goodbye! 👋")
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
                if (Messages.put(message) == true && message.type == ApplicationDataType.PING){
                    //TODO ping reply
                }
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
    send(operatingParameters.clientData)
    println("Loading ⏳")
    //loop to wait for init of all clients list
    while (allClients.listOf.isEmpty()){
        Thread.sleep(10)
    }
    println("You are connected!")
    //for each user input
    input@while (true) {
        allClients.Status()
        val task = Menu.getTask()
        val exit = { x: String ->
            println("${x}ting")
            println("Connection closed. Goodbye!")
            exitProcess(0)
        }
        when (task){
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
                allClients.printMembers()
                continue
            }
            Tasks.LISTEN -> {
                listen
                continue
            }
            else -> {
                println("Error parsing task input.🤦 Please try again.")
                continue
            }
        }
        print("Do you want to send a 'broadcast' or 'private' message: ")
        val messages = when(readln()){
            "broadcast" -> Message.messageBroadcast()
            "private" -> Message.message1to1()
            else -> {
                println("Error parsing task input.🤦 Please try again.")
                continue
            }
        }
        // member can request existing members
        messages.forEach {
            try {
                // send what you typed
                send(Json.encodeToString(it))
            } catch (e: Exception) {
                println("Error while sending: " + e.localizedMessage)
                return
            }
        }
    }
}

