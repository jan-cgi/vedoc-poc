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

    @JmsListener(destination = "DEV.QUEUE.VEHICLE.CREATE")
    fun createVehicle(vehicleXML: String) {
        val vehicle = xmlMapper.readValue<Vehicle>(vehicleXML)
        val vehicleJson = jsonMapper.writeValueAsString(vehicle)
        rabbitMQProducer.createVehicle(vehicleJson)
    }

    @JmsListener(destination = "DEV.QUEUE.VEHICLE.GET.REQUEST")
    fun readVehicle(vehicleGetRequestXML: String): String {
        val vehicleGetRequest = xmlMapper.readValue<VehicleGetRequest>(vehicleGetRequestXML)

        val vehicleJson = rabbitMQProducer.getVehicle(vehicleGetRequest.fin)

        return runCatching {
            val vehicle = jsonMapper.readValue<Vehicle>(vehicleJson)
            vehicle.toXml(vehicleGetRequest.version)
        }.getOrElse {
            vehicleJson
        }
    }

    @JmsListener(destination = "DEV.QUEUE.VEHICLE.UPDATE")
    fun updateVehicle(vehicleXML: String) {
        val vehicle = xmlMapper.readValue<Vehicle>(vehicleXML)
        val vehicleJson = jsonMapper.writeValueAsString(vehicle)
        rabbitMQProducer.updateVehicle(vehicleJson)
    }

    private fun Vehicle.toXml(version: Int): String {
        return when (version) {
            1 -> xmlMapper.writeValueAsString(toV1())
            2 -> xmlMapper.writeValueAsString(this)
            else -> error("Unsupported vehicle response version: $version")
        }
    }

}
