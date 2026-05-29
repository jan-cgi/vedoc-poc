package com.example.xmljsonadapter.vehicle

import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper
import tools.jackson.dataformat.xml.XmlMapper
import tools.jackson.module.kotlin.readValue

@Component
class IBMMQConsumer(
    private val rabbitMQProducer: RabbitMQProducer,
    private val jsonMapper: JsonMapper,
    private val xmlMapper: XmlMapper
) {

    @JmsListener(destination = "DEV.QUEUE.1")
    fun createVehicle(vehicleXML: String) {
        val vehicle = xmlMapper.readValue<Vehicle>(vehicleXML)
        val vehicleJson = jsonMapper.writeValueAsString(vehicle)
        rabbitMQProducer.createVehicle(vehicleJson)
    }

    @JmsListener(destination = "DEV.QUEUE.2")
    fun readVehicle(fin: String): String {
        val vehicleJson = rabbitMQProducer.getVehicle(fin)
        return runCatching {
            val vehicle = jsonMapper.readValue<Vehicle>(vehicleJson)
            xmlMapper.writeValueAsString(vehicle)
        }.getOrElse {
            vehicleJson
        }
    }

}
