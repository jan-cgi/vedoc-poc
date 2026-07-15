package com.example.testclient

import jakarta.jms.TextMessage
import org.springframework.jms.core.JmsTemplate
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import tools.jackson.dataformat.xml.XmlMapper

@Component
class AsyncClient(
    private val jmsTemplate: JmsTemplate,
    private val xmlMapper: XmlMapper,
) {

    @Async
    fun read(fin: String, version: Int = 2) {
        val response = jmsTemplate.sendAndReceive(
            "DEV.QUEUE.VEHICLE.GET.REQUEST",
            "DEV.QUEUE.VEHICLE.GET.RESPONSE"
        ) { session ->
            session.createTextMessage(xmlMapper.writeValueAsString(VehicleGetRequest(fin, version)))
        } as TextMessage

        println("received")
    }

}
