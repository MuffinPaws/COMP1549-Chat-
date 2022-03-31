package com.jetbrains.handson.chat.client.Message

import com.jetbrains.handson.chat.client.allClientsData.allClients
import java.time.Instant
import java.time.format.DateTimeFormatter

// Display function for TEXT messages
class TextMessageBox(data: String) : MessageBox<String>(data) {
    fun display(fromID: String, time: Long) {
        val fromMemberName = allClients.getMemberByID(fromID).name
        val timeString = DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochMilli(time))
        println(
            """
            Message from: $fromMemberName
            ${fromMemberName}s fingerprint: ${fromID.substring(0..allClients.getFingerprintTruncation())}
            Received at: $timeString
            Message:
            $data
        """.trimIndent()
        )
    }
}