package com.example.testclient

import jakarta.jms.TextMessage
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.jms.core.JmsTemplate
import org.springframework.scheduling.annotation.EnableAsync

@EnableAsync
@SpringBootApplication
class TestClientApplication(private val jmsTemplate: JmsTemplate, private val asyncClient: AsyncClient) {

    @Bean
    fun run() = CommandLineRunner {
        val vehicle = """
            <root>
                <vehicleDatacard>
                    <fin>9BM9581341H041434</fin>
                    <productSeries>958</productSeries>
                    <productSeriesBrand>mb</productSeriesBrand>
                    <productSeriesDesignation>Baureihe 958</productSeriesDesignation>
                    <vehicleModelDesignation>958134</vehicleModelDesignation>
                </vehicleDatacard>            
                <reference>
                    <productgroup>
                        <id>3</id>
                        <designation>TRUCK</designation>
                    </productgroup>            
                    <company>
                        <id>1</id>
                        <designation>Daimler Truck AG</designation>
                    </company>
                </reference>
            </root>
            """.trimIndent()

        for (i in 1..25) {
            asyncClient.read("9BM9581341H041434")
        }

        Thread.sleep(30000)

//        jmsTemplate.convertAndSend("DEV.QUEUE.1", vehicle)

//        Thread.sleep(1000)
//
//        val response = jmsTemplate.sendAndReceive("DEV.QUEUE.2", "DEV.QUEUE.3") { session ->
//            session.createTextMessage("9BM9581341H041434")
//        } as TextMessage
//
//        println(response.text)

//        var jmsMessageID: String? = null
//
//        jmsTemplate.convertAndSend("DEV.QUEUE.2", "fin1") { message ->
//            jmsMessageID = message.jmsMessageID
//            message
//        }
//
//        println("Nachricht erfolgreich gesendet! Generierte JMSMessageID: $jmsMessageID")
    }

}

fun main(args: Array<String>) {
    runApplication<TestClientApplication>(*args)
}
