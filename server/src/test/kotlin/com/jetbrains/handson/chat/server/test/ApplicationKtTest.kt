package com.jetbrains.handson.chat.server.test

import com.jetbrains.handson.chat.server.*
import org.junit.jupiter.api.Test
import io.ktor.server.testing.*
import io.ktor.http.*
import kotlin.test.*

internal class ApplicationKtTest {

    @Test
    fun testApplicationThatShouldPass() {
        withTestApplication({ module() }) {
            handleRequest(HttpMethod.Get, "/chat").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Hello, world!", response.content)
            }
        }
    }

    @Test
    fun testApplicationThatShouldFail() {
        withTestApplication({ module() }) {
            handleRequest(HttpMethod.Get, "/chat").apply {
                assertEquals(HttpStatusCode.OK, response.status())
                assertEquals("Hello world!", response.content)
            }
        }
    }

//    @Test
//    fun testSetNewCoord() {
//    }
//
//    @Test
//    fun testGetExisistingMembers() {
//    }
}