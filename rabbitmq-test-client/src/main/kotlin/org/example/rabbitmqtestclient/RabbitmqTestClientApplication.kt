package org.example.rabbitmqtestclient

import org.example.rabbitmqtestclient.config.RabbitMQConfig.Companion.VEHICLE_CREATE_KEY
import org.example.rabbitmqtestclient.config.RabbitMQConfig.Companion.VEHICLE_EXCHANGE
import org.example.rabbitmqtestclient.config.RabbitMQConfig.Companion.VEHICLE_GET_REQUEST_KEY
import org.example.rabbitmqtestclient.config.RabbitMQConfig.Companion.VEHICLE_UPDATE_KEY
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class RabbitmqTestClientApplication(
    private val defaultRabbitTemplate: RabbitTemplate,
    private val rpcRabbitTemplate: RabbitTemplate,
) {

    @Bean
    fun run() = CommandLineRunner {

        // CREATE VEHICLE
        defaultRabbitTemplate.convertAndSend(
            VEHICLE_EXCHANGE,
            VEHICLE_CREATE_KEY,
            """
                {
                  "vehicleDatacard": {
                    "checkDigit": "7",
                    "fin": "WDD2050401R123456",
                    "fixingPartsAvailable": false,
                    "prodOrderTextAvailable": false,
                    "productGroupIndication": "PKW",
                    "productSeries": "205",
                    "productSeriesBrand": "Mercedes-Benz",
                    "productSeriesDesignation": "C-Klasse",
                    "vehicleModelDescription": "C 220 d Limousine",
                    "vehicleModelDesignation": "C220D",
                    "activeAssignedFpd": "FPD-001",
                    "activeCustomerServiceData": "SERVICE_DATA",
                    "activeModelPlate": "MODEL_PLATE",
                    "activeProductDate": "2024-03-18",
                    "activeProductionInfo": "PRODUCTION_INFO",
                    "activeState": "ACTIVE"
                  },
                  "reference": {
                    "productgroup": "PKW",
                    "company": "Mercedes-Benz AG"
                  }
                }
            """.trimIndent()
        )

        Thread.sleep(1000)

        // GET VEHICLE
        val response = rpcRabbitTemplate.convertSendAndReceive(
            VEHICLE_EXCHANGE,
            VEHICLE_GET_REQUEST_KEY,
            "WDD2050401R123456"
        ) as? String ?: throw IllegalStateException("Timed out waiting for vehicle response for WDD2050401R123456")
        println(response)

        // UPDATE VEHICLE
        defaultRabbitTemplate.convertAndSend(
            VEHICLE_EXCHANGE,
            VEHICLE_UPDATE_KEY,
            """
                {
                  "vehicleDatacard": {
                    "fin": "WDD2050401R123456",
                    "fixingPartsAvailable": true,
                    "vehicleModelDescription": "Updated model description"
                  },
                  "reference": {
                    "company": "Updated company"
                  }
                }
            """.trimIndent()
        )

    }

}

fun main(args: Array<String>) {
    runApplication<RabbitmqTestClientApplication>(*args)
}
