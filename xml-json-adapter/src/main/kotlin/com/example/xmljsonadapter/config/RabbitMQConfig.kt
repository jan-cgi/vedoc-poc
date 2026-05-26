package com.example.xmljsonadapter.config

import org.springframework.amqp.core.Queue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfig {

    @Bean
    fun vehicleResponseQueue(): Queue {
        return Queue("vehicle.response.queue", true)
    }

}