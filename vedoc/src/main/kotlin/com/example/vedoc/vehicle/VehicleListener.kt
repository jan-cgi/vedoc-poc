package com.example.vedoc.vehicle

import com.example.vedoc.config.RabbitMQConfig.Companion.VEHICLE_CREATE_QUEUE
import com.example.vedoc.config.RabbitMQConfig.Companion.VEHICLE_GET_REQUEST_QUEUE
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import tools.jackson.databind.json.JsonMapper

@Component
class VehicleListener(
    private val jsonMapper: JsonMapper,
    private val vehicleRepository: VehicleRepository
) {

    @RabbitListener(queues = [VEHICLE_CREATE_QUEUE])
    fun createVehicle(vehicleString: String) {
        val vehicle = jsonMapper.readValue(vehicleString, Vehicle::class.java)
        vehicleRepository.save(vehicle)
    }

    @RabbitListener(queues = [VEHICLE_GET_REQUEST_QUEUE])
    fun getVehicle(fin: String): String {
        val vehicle =  vehicleRepository.findByVehicleDatacardFin(fin)
        return jsonMapper.writeValueAsString(vehicle ?: "No vehicle found with fin: $fin")
    }

}
