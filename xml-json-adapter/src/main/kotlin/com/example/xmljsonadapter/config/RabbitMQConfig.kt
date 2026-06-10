package com.example.xmljsonadapter.config

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfig {

    companion object {
        const val VEHICLE_EXCHANGE = "vedoc.vehicle.exchange"
        const val VEHICLE_CREATE_KEY = "vedoc.vehicle.create.key"
        const val VEHICLE_UPDATE_EVENT_QUEUE = "vedoc.vehicle.update.event.queue"
        const val VEHICLE_UPDATE_EVENT_KEY = "vedoc.vehicle.update.event.key"
        const val VEHICLE_GET_REQUEST_KEY = "vedoc.vehicle.get.request.key"
        const val VEHICLE_GET_RESPONSE_QUEUE = "vedoc.vehicle.get.response.queue"
        const val VEHICLE_GET_RESPONSE_KEY = "vedoc.vehicle.get.response.key"
        const val VEHICLE_UPDATE_KEY = "vedoc.vehicle.update.key"
    }

    @Bean
    fun vehicleExchange(): DirectExchange {
        return DirectExchange(VEHICLE_EXCHANGE)
    }

    @Bean
    fun vehicleGetResponseQueue(): Queue {
        return Queue(VEHICLE_GET_RESPONSE_QUEUE, true)
    }

    @Bean
    fun vehicleGetResponseBinding(vehicleGetResponseQueue: Queue, vehicleExchange: DirectExchange): Binding {
        return BindingBuilder
            .bind(vehicleGetResponseQueue)
            .to(vehicleExchange)
            .with(VEHICLE_GET_RESPONSE_KEY)
    }

    @Bean
    fun vehicleUpdateEventQueue(): Queue {
        return Queue(VEHICLE_UPDATE_EVENT_QUEUE, true)
    }

    @Bean
    fun vehicleUpdateBinding(vehicleUpdateEventQueue: Queue, vehicleExchange: DirectExchange): Binding {
        return BindingBuilder
            .bind(vehicleUpdateEventQueue)
            .to(vehicleExchange)
            .with(VEHICLE_UPDATE_EVENT_KEY)
    }

    @Bean
    fun defaultRabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate {
        return RabbitTemplate(connectionFactory)
    }

    @Bean
    fun rpcRabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate {
        val template = RabbitTemplate(connectionFactory)
        template.setReplyAddress("$VEHICLE_EXCHANGE/$VEHICLE_GET_RESPONSE_KEY")
        template.setReplyTimeout(10_000)
        return template
    }

    @Bean
    fun replyContainer(
        connectionFactory: ConnectionFactory,
        rpcRabbitTemplate: RabbitTemplate
    ): SimpleMessageListenerContainer {
        val container = SimpleMessageListenerContainer()
        container.connectionFactory = connectionFactory
        container.setQueueNames(VEHICLE_GET_RESPONSE_QUEUE)
        container.setMessageListener(rpcRabbitTemplate)
        return container
    }

}
