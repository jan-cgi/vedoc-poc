package com.example.xmljsonadapter.vehicle

import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class Runner(private val rabbitTemplate: RabbitTemplate) : CommandLineRunner {

    override fun run(vararg args: String) {
        val vehicle = """
        {
          "vehicleDatacard": {
            "fin": "9BM9581341H041434",
            "productSeries": "958",
            "productSeriesBrand": "mb",
            "productSeriesDesignation": "Baureihe 958",
            "vehicleModelDesignation": "958134"
          },
          "reference": {
            "productgroup": {
              "id": "3",
              "designation": "TRUCK"
            },
            "company": {
              "id": "1",
              "designation": "Daimler Truck AG"
            }
          }
        }
    """.trimIndent()

        rabbitTemplate.convertAndSend("vehicle.create.queue", vehicle)
    }

}