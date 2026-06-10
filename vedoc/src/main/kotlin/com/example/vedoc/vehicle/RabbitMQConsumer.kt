package com.example.vedoc.vehicle

import com.example.vedoc.config.RabbitMQConfig.Companion.VEHICLE_CREATE_QUEUE
import com.example.vedoc.config.RabbitMQConfig.Companion.VEHICLE_GET_REQUEST_QUEUE
import com.example.vedoc.config.RabbitMQConfig.Companion.VEHICLE_UPDATE_QUEUE
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper
import tools.jackson.module.kotlin.readValue

@Profile("web")
@Component
class RabbitMQConsumer(
    private val jsonMapper: JsonMapper,
    private val vehicleService: VehicleService
) {

    @RabbitListener(queues = [VEHICLE_CREATE_QUEUE])
    fun createVehicle(vehicleJson: String) {
        val vehicle = jsonMapper.readValue<Vehicle>(vehicleJson)
        vehicleService.createVehicle(vehicle)
    }

    @RabbitListener(queues = [VEHICLE_GET_REQUEST_QUEUE])
    fun getVehicle(fin: String): String {
        return vehicleService.getVehicle(fin)
            ?.toDto()
            ?.let(jsonMapper::writeValueAsString)
            ?: "No vehicle found with fin: $fin"
    }

    @RabbitListener(queues = [VEHICLE_UPDATE_QUEUE])
    fun updateVehicle(vehicleJson: String) {
        val vehicle = jsonMapper.readValue<Vehicle>(vehicleJson)
        vehicleService.updateVehicle(vehicle.vehicleDatacard!!.fin!!, vehicle.toDto())
    }

}
