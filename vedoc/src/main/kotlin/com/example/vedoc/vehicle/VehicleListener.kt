package com.example.vedoc.vehicle

import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper

@Component
class VehicleListener(
    private val jsonMapper: JsonMapper,
    private val vehicleRepository: VehicleRepository
) {

    @RabbitListener(queues = ["vehicle.create.queue"])
    fun vehicleCreatedEvent(vehicleString: String) {
        val vehicle = jsonMapper.readValue(vehicleString, Vehicle::class.java)
        vehicleRepository.save(vehicle)
    }

}