package com.jetbrains.handson.chat.server.test

import com.jetbrains.handson.chat.server.*
import org.junit.jupiter.api.Test
import io.ktor.application.*
import io.ktor.http.cio.websocket.*
import io.ktor.server.testing.*


internal class ApplicationKtTest {

    @Test
    fun testConversation() {
        withTestApplication(Application::module) {
            handleWebSocketConversation("/echo") { incoming, outgoing ->
                val greetingText = (incoming.receive() as Frame.Text).readText()
                kotlin.test.assertEquals("Please enter your name", greetingText)

                outgoing.send(Frame.Text("JetBrains"))
                val responseText = (incoming.receive() as Frame.Text).readText()
                kotlin.test.assertEquals("Hi, JetBrains!", responseText)

                outgoing.send(Frame.Text("bye"))
                val closeReason = (incoming.receive() as Frame.Close).readReason()?.message
                kotlin.test.assertEquals("Client said BYE", closeReason)
            }
        }
    }


    @Test
    fun setNewCoord() {
    }

    @Test
    fun getExisistingMembers() {
    }
}