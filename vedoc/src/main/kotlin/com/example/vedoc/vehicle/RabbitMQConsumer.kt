package com.example.vedoc.vehicle

import com.example.vedoc.config.RabbitMQConfig.Companion.VEHICLE_CREATE_QUEUE
import com.example.vedoc.config.RabbitMQConfig.Companion.VEHICLE_GET_REQUEST_QUEUE
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.readValue

@Component
class RabbitMQConsumer(
    private val jsonMapper: JsonMapper,
    private val vehicleRepository: VehicleRepository
) {

    @RabbitListener(queues = [VEHICLE_CREATE_QUEUE])
    fun createVehicle(vehicleJson: String) {
        val vehicle = jsonMapper.readValue<Vehicle>(vehicleJson)
        vehicleRepository.save(vehicle)
    }

    @RabbitListener(queues = [VEHICLE_GET_REQUEST_QUEUE])
    fun getVehicle(fin: String): String {
        return vehicleRepository.findByVehicleDatacardFin(fin)
            ?.toDto()
            ?.let(jsonMapper::writeValueAsString)
            ?: "No vehicle found with fin: $fin"
    }

}
