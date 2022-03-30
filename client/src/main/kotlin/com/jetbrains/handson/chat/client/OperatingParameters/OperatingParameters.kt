package com.jetbrains.handson.chat.client.OperatingParameters

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.output.TermUi.echo
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.types.int
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.net.InetAddress

class OperatingParameters : CliktCommand() {
    val name: String by option(help = "Your display name").prompt("What is your display name")
    val serverIP: String by option(help = "The server's IP address ").prompt("What is the server's IP address")
    val serverPort: Int by option(help = "The server's port").int().prompt("What is the port at the server")
    val clientIP: String by option(help = "Which IP address to share").prompt("What is the IP address you want to share")
    val clientPort: Int by option(help = "Which port to share").int().prompt("What is the port you want to share")
    var clientData: String = ""

    override fun run() {
        echo("Starting App") // TODO can this be moved out of here?
        echo("generating your fingerprint")
        echo("Your ID is ${IDFingerprintKeyPair.fingerprint}")

        echo("Verifying client info")
        if (VerifyIP.isNotValid(clientIP)) echo("Warning the IP address you want use may be Incorrect")
        if (VerifyPort.isNotValid(clientPort)) echo("Warning the port you want to use may be incorrect")

        echo("Verifying server info")
        if (VerifyIP.isNotValid(serverIP)) echo("Warning the server's IP address may be Incorrect")
        if (VerifyPort.isNotValid(serverPort)) echo("Warning the server's port you want to use may be incorrect")
        //TODO check if server reachable

        clientData = Json.encodeToString(clientData(name = name, IP = clientIP, port = clientPort))
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
        private const val PORT_MIN = 0
        private const val PORT_MAX = 65535
        private const val PORT_PRIVILEGE_MAX = 1023

        fun isNotValid(port: Int): Boolean {
            if (port <= PORT_PRIVILEGE_MAX) echo("Warning port may be privileged")
            return port !in PORT_MIN..PORT_MAX
        }
    }
}