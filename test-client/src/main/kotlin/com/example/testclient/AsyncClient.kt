package com.example.testclient

import jakarta.jms.TextMessage
import org.springframework.jms.core.JmsTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component

@Component
class AsyncClient(private val jmsTemplate: JmsTemplate) {

    @Async
    fun read(fin: String) {
        val response = jmsTemplate.sendAndReceive(
            "DEV.QUEUE.VEHICLE.GET.REQUEST",
            "DEV.QUEUE.VEHICLE.GET.RESPONSE"
        ) { session ->
            session.createTextMessage(fin)
        } as TextMessage

        println("received")
    }

}
