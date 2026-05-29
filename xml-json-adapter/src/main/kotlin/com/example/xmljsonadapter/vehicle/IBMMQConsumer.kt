package com.example.xmljsonadapter.vehicle

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

@Component
class IBMMQConsumer(
    private val rabbitMQProducer: RabbitMQProducer,
    private val jsonMapper: JsonMapper,
    private val xmlMapper: XmlMapper
) {

    @JmsListener(destination = "DEV.QUEUE.1")
    fun createVehicle(vehicleXML: String) {
        val node = xmlMapper.readTree(vehicleXML)
        val vehicleJson = jsonMapper.writeValueAsString(node)
        rabbitMQProducer.createVehicle(vehicleJson)
    }

    @JmsListener(destination = "DEV.QUEUE.2")
    fun readVehicle(fin: String): String {
        val vehicleJson = rabbitMQProducer.getVehicle(fin)
        val node = jsonMapper.readTree(vehicleJson)
        return xmlMapper.writeValueAsString(node)
    }

}
