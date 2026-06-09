package com.example.xmljsonadapter.config

import jakarta.jms.ConnectionFactory
import org.springframework.boot.jms.ConnectionFactoryUnwrapper
import org.springframework.boot.jms.autoconfigure.DefaultJmsListenerContainerFactoryConfigurer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.task.VirtualThreadTaskExecutor
import org.springframework.jms.config.DefaultJmsListenerContainerFactory

@Configuration
class JmsVirtualThreadConfig {

    @Bean
    fun jmsListenerContainerFactory(
        configurer: DefaultJmsListenerContainerFactoryConfigurer,
        connectionFactory: ConnectionFactory
    ): DefaultJmsListenerContainerFactory {
        val factory = DefaultJmsListenerContainerFactory()
        configurer.configure(factory, ConnectionFactoryUnwrapper.unwrapCaching(connectionFactory))
        factory.setTaskExecutor(VirtualThreadTaskExecutor("jms-"))
        return factory
    }

}
