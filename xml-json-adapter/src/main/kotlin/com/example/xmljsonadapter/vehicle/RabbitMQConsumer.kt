package com.example.xmljsonadapter.vehicle

import com.example.xmljsonadapter.config.RabbitMQConfig.Companion.VEHICLE_UPDATE_QUEUE
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.jms.core.JmsTemplate
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper
import tools.jackson.dataformat.xml.XmlMapper
import tools.jackson.module.kotlin.readValue

@Component
class RabbitMQConsumer(
    private val jmsTemplate: JmsTemplate,
    private val jsonMapper: JsonMapper,
    private val xmlMapper: XmlMapper
) {

    @RabbitListener(queues = [VEHICLE_UPDATE_QUEUE])
    fun updateVehicle(vehicleJson: String) {
        val vehicle = jsonMapper.readValue<Vehicle>(vehicleJson)
        val vehicleXml = xmlMapper.writeValueAsString(vehicle)
        jmsTemplate.convertAndSend("DEV.QUEUE.VEHICLE.UPDATE", vehicleXml)
    }

}
