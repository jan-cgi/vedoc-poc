package com.example.xmljsonadapter.vehicle

import com.example.xmljsonadapter.config.RabbitMQConfig.Companion.VEHICLE_CREATE_KEY
import com.example.xmljsonadapter.config.RabbitMQConfig.Companion.VEHICLE_EXCHANGE
import com.example.xmljsonadapter.config.RabbitMQConfig.Companion.VEHICLE_GET_REQUEST_KEY
import com.example.xmljsonadapter.config.RabbitMQConfig.Companion.VEHICLE_UPDATE_KEY
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

@Service
class RabbitMQProducer(
    private val defaultRabbitTemplate: RabbitTemplate,
    private val rpcRabbitTemplate: RabbitTemplate,
) {

    fun createVehicle(vehicleJson: String) {
        defaultRabbitTemplate.convertAndSend(
            VEHICLE_EXCHANGE,
            VEHICLE_CREATE_KEY,
            vehicleJson
        )
    }

    fun getVehicle(fin: String): String {
        return rpcRabbitTemplate.convertSendAndReceive(
            VEHICLE_EXCHANGE,
            VEHICLE_GET_REQUEST_KEY,
            fin
        ) as? String ?: throw IllegalStateException("Timed out waiting for vehicle response for$fin")
    }

    fun updateVehicle(vehicleJson: String) {
        defaultRabbitTemplate.convertAndSend(
            VEHICLE_EXCHANGE,
            VEHICLE_UPDATE_KEY,
            vehicleJson
        )
    }

}
