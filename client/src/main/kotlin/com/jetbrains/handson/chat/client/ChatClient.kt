package com.jetbrains.handson.chat.client

import com.jetbrains.handson.chat.client.commandsMenu.Menu
import com.jetbrains.handson.chat.client.commandsMenu.MenuTasks
import com.jetbrains.handson.chat.client.operatingParameters.OperatingParameters
import com.jetbrains.handson.chat.client.allClientsData.AllClientsList
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
                println(frame.readText()) //TODO remove
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
    while (true){
        Thread.sleep(1000) //TODO check value
        if (AllClientsList.listOf.isEmpty()) continue else break
    }
    println("You are connected!")
    //for each user input
    while (true) {
        AllClientsList.Status()
        val task = Menu.getTask()
        val exit = { x: String ->
            println("${x}ting")
            println("Connection closed. Goodbye!")
            exitProcess(0)
        }
        when (task){
            MenuTasks.EXIT -> exit("Exi")
            MenuTasks.QUIT -> exit("Quit")
            MenuTasks.SEND -> print("Please type your massage: ")
            MenuTasks.READ -> {
                Messages.read()
                continue
            }
            MenuTasks.HISTORY -> {
                Messages.read(true)
                continue
            }
            MenuTasks.MEMBERS -> {
                AllClientsList.printMembers()
                continue
            }
            else -> {
                println("Error parsing task input.🤦 Please try again.")
                continue
            }
        }
        val message = readln()
        if (message.isBlank()) continue
        // member can request existing members
        try {
            // send what you typed
            val messageP = Message(toID = "hdsfg", data = message, type = ApplicationDataType.TEXT)
            send(Json.encodeToString(messageP))
        } catch (e: Exception) {
            println("Error while sending: " + e.localizedMessage)
            return
        }
    }
}