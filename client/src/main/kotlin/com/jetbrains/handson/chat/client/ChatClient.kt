package com.jetbrains.handson.chat.client

import com.jetbrains.handson.chat.client.OperatingParameters.OperatingParameters
import com.jetbrains.handson.chat.client.allClientsData.allClients
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
    //TODO fix waiting for server list
//    while (allClients.listOf.size==0){
//        //loop to wait for init of all clients list
//    }
    println("You are connected!")
    //for each user input
    while (true) {
        allClients.Status()
        //TODO change move to short menu with long menu for help
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
            "exit" -> exit("Exi")
            "quit" -> exit("Quit")
            "read" -> Messages.read()
            "history" -> Messages.read(true)
            "members" -> continue // TODO implement
            "send" -> print("Please type your massage: ")
            else -> {
                println("unknown command🥴")
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

enum class tasks{
    EXIT,
    QUIT,
    SEND,
    READ,
    HISTORY,
    MEMBERS,
    HELP,
    UNKOWN;

    companion object{
        fun findTask(input:String):tasks{
            for (enum in values()){
                if (enum.name.equals(input, true)) return enum
            }
            return UNKOWN
        }
    }
}

object menu{
    const val shortMenu = "For list of all commands enter help. \n Please enter command: "
    const val longMenu = """
                Type 'exit' or 'quit' to close the program. 
                type 'read' to read messages
                type 'send' to type a new message (Press Enter to send it)
                type 'history' to fetch messages history
                type 'members' to list all members
                Please enter command: 
    """
    private fun print(short:Boolean = true){
        if (short) return print(shortMenu)
        print(longMenu.trimIndent())
    }

    fun getTask(shortMenu:Boolean = true){
        print(shortMenu)
        val input = tasks.findTask(readln())
    }
}