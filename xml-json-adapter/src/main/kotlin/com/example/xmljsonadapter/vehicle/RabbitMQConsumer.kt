package com.example.xmljsonadapter.vehicle

import com.example.xmljsonadapter.config.RabbitMQConfig.Companion.VEHICLE_UPDATE_EVENT_QUEUE
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

    @RabbitListener(queues = [VEHICLE_UPDATE_EVENT_QUEUE])
    fun forwardUpdateVehicleEvent(vehicleUpdateEventJson: String) {
        val vehicleUpdateEvent = jsonMapper.readValue<VehicleUpdateEvent>(vehicleUpdateEventJson)
        val vehicleXml = xmlMapper.writeValueAsString(vehicleUpdateEvent)
        jmsTemplate.convertAndSend("DEV.QUEUE.VEHICLE.UPDATE.EVENT", vehicleXml)
    }

}
