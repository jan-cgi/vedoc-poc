package com.example.vedoc.config

import org.springframework.amqp.core.Queue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
class RabbitMQConfig {

    @Bean
    fun vehicleCreateQueue(): Queue {
        return Queue("vehicle.create.queue", true)
    }

    @Bean
    fun vehicleReadQueue(): Queue {
        return Queue("vehicle.read.queue", true)
    }

}