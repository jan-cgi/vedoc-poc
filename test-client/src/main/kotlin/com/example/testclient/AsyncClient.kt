package com.example.testclient

import jakarta.jms.TextMessage
import org.springframework.jms.core.JmsTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class AsyncClient(private val jmsTemplate: JmsTemplate) {

    @Async
    fun read(fin: String) {
        val response = jmsTemplate.sendAndReceive("DEV.QUEUE.2", "DEV.QUEUE.3") { session ->
            session.createTextMessage(fin)
        } as TextMessage

        println(response.text)
    }

}
