package com.example.vedoc.config

import com.rabbitmq.client.DefaultSaslConfig
import org.springframework.boot.amqp.autoconfigure.ConnectionFactoryCustomizer
import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("web", "batch-publish-update")
@Configuration
class RabbitMQConfig {

    @Bean
    fun certificateSaslCustomizer(): ConnectionFactoryCustomizer =
        ConnectionFactoryCustomizer { it.saslConfig = DefaultSaslConfig.EXTERNAL }

    companion object {
        const val VEHICLE_EXCHANGE = "vedoc.vehicle.exchange"
        const val VEHICLE_CREATE_QUEUE = "vedoc.vehicle.create.queue"
        const val VEHICLE_CREATE_KEY = "vedoc.vehicle.create.key"
        const val VEHICLE_UPDATE_QUEUE = "vedoc.vehicle.update.queue"
        const val VEHICLE_UPDATE_KEY = "vedoc.vehicle.update.key"
        const val VEHICLE_UPDATE_EVENT_KEY = "vedoc.vehicle.update.event.key"
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

    @Bean
    fun vehicleUpdateQueue(): Queue {
        return Queue(VEHICLE_UPDATE_QUEUE, true)
    }

    @Bean
    fun vehicleUpdateBinding(vehicleUpdateQueue: Queue, vehicleExchange: DirectExchange): Binding {
        return BindingBuilder
            .bind(vehicleUpdateQueue)
            .to(vehicleExchange)
            .with(VEHICLE_UPDATE_KEY)
    }

}
