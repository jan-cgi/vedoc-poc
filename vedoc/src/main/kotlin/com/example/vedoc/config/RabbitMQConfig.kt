package com.example.vedoc.config

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfig {

    companion object {
        const val VEHICLE_EXCHANGE = "vedoc.vehicle.exchange"
        const val VEHICLE_CREATE_QUEUE = "vedoc.vehicle.create.queue"
        const val VEHICLE_CREATE_KEY = "vedoc.vehicle.create.key"
        const val VEHICLE_GET_REQUEST_QUEUE = "vedoc.vehicle.get.request.queue"
        const val VEHICLE_GET_REQUEST_KEY = "vedoc.vehicle.get.request.key"
    }

    @Bean
    fun vehicleExchange(): DirectExchange {
        return DirectExchange(VEHICLE_EXCHANGE)
    }

    @Bean
    fun vehicleCreateQueue(): Queue {
        return Queue(VEHICLE_CREATE_QUEUE, true)
    }

    @Bean
    fun vehicleCreateBinding(vehicleCreateQueue: Queue, vehicleExchange: DirectExchange): Binding {
        return BindingBuilder
            .bind(vehicleCreateQueue)
            .to(vehicleExchange)
            .with(VEHICLE_CREATE_KEY)
    }

    @Bean
    fun vehicleGetRequestQueue(): Queue {
        return Queue(VEHICLE_GET_REQUEST_QUEUE, true)
    }

    @Bean
    fun vehicleGetRequestBinding(vehicleGetRequestQueue: Queue, vehicleExchange: DirectExchange): Binding {
        return BindingBuilder
            .bind(vehicleGetRequestQueue)
            .to(vehicleExchange)
            .with(VEHICLE_GET_REQUEST_KEY)
    }

}
