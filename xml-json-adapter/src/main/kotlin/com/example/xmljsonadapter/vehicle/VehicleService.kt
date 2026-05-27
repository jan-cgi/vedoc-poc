package com.example.xmljsonadapter.vehicle

import com.example.xmljsonadapter.config.RabbitMQConfig.Companion.VEHICLE_CREATE_KEY
import com.example.xmljsonadapter.config.RabbitMQConfig.Companion.VEHICLE_EXCHANGE
import com.example.xmljsonadapter.config.RabbitMQConfig.Companion.VEHICLE_GET_REQUEST_KEY
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class VehicleService(
    private val defaultRabbitTemplate: RabbitTemplate,
    private val rpcRabbitTemplate: RabbitTemplate,
) {

    fun createVehicle(vehicle: String) {
        defaultRabbitTemplate.convertAndSend(
            VEHICLE_EXCHANGE,
            VEHICLE_CREATE_KEY,
            vehicle
        )
    }

    fun getVehicle(fin: String): String {
        return rpcRabbitTemplate.convertSendAndReceive(
            VEHICLE_EXCHANGE,
            VEHICLE_GET_REQUEST_KEY,
            fin
        ) as String
    }

}
