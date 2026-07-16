package org.example.rabbitmqtestclient.config

import com.rabbitmq.client.DefaultSaslConfig
import org.springframework.boot.amqp.autoconfigure.ConnectionFactoryCustomizer
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

    @Bean
    fun certificateSaslCustomizer(): ConnectionFactoryCustomizer =
        ConnectionFactoryCustomizer { it.saslConfig = DefaultSaslConfig.EXTERNAL }

    companion object {
        const val VEHICLE_EXCHANGE = "vedoc.vehicle.exchange"
        const val VEHICLE_CREATE_KEY = "vedoc.vehicle.create.key"
        const val VEHICLE_GET_REQUEST_KEY = "vedoc.vehicle.get.request.key"
        const val VEHICLE_GET_RESPONSE_QUEUE = "vedoc.vehicle.get.response.queue.test-client"
        const val VEHICLE_GET_RESPONSE_KEY = "vedoc.vehicle.get.response.key.test-client"
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
    fun defaultRabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate {
        return RabbitTemplate(connectionFactory)
    }

    @Bean
    fun rpcRabbitTemplate(connectionFactory: ConnectionFactory): RabbitTemplate {
        val template = RabbitTemplate(connectionFactory)
        template.setReplyAddress("$VEHICLE_EXCHANGE/$VEHICLE_GET_RESPONSE_KEY")
        template.setReplyTimeout(60_000)
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
