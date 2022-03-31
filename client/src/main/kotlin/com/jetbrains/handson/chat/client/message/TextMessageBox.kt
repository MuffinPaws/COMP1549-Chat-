package com.jetbrains.handson.chat.client.message

import com.jetbrains.handson.chat.client.allMembersData.AllMembers
import java.time.Instant
import java.time.format.DateTimeFormatter

// Display function for TEXT messages
class TextMessageBox(data: String) : MessageBox<String>(data) {
    fun display(fromID: String, time: Long) {
        val fromMemberName = AllMembers.getMemberByID(fromID).name
        val timeString = DateTimeFormatter.ISO_INSTANT.format(Instant.ofEpochMilli(time))
        println(
            """
            message from: $fromMemberName
            ${fromMemberName}s fingerprint: ${fromID.substring(0..AllMembers.getFingerprintTruncation())}
            Received at: $timeString
            message:
            $data
        """.trimIndent()
        )
    }
}