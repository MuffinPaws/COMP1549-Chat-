package com.jetbrains.handson.chat.client

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.types.int
import java.net.InetAddress

class OperatingParameters : CliktCommand() {
    val name: String by option(help = "Your display name").prompt("What is your display name")
    val serverIP: String by option(help = "The server's IP address ").prompt("What is the server's IP address")
    val serverPort: Int by option(help = "The server's port").int().prompt("What is the port at the server")
    val clientIP: String by option(help = "Which IP address to use").prompt("What is the IP address you want to use")
    val clientPort: Int by option(help = "Which port to use").int().prompt("What is the port you want to use")

    override fun run() {
        echo("Starting App")

        echo("Verifying client info")
        if (VerifyIP.isNotValid(clientIP)) echo("Warning the IP address you want use may be Incorrect")
        //TODO check if client Port is okay

        echo("Verifying server info")
        if (VerifyIP.isNotValid(serverIP)) echo("Warning the server's IP address may be Incorrect")
        //TODO check if server Port is okay
        //TODO check if server reachable

        echo("generating your fingerprint")
        //TODO generate client ID object
    }

    object VerifyIP {
        //TODO  if needed add bogon check
        fun isNotValid(IP: String): Boolean {
            val parsedIP: String = if (IP.contains(':')) {
                InetAddress.getByName(IP).hostAddress.replaceFirst(":0", ":").replace(":0", "")
            } else {
                InetAddress.getByName(IP).hostAddress
            }
            return IP != parsedIP
        }
    }

    object VerifyPort {

    }
}