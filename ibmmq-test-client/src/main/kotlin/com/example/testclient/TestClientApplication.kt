package com.example.testclient

import jakarta.jms.TextMessage
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.core.io.Resource
import org.springframework.jms.core.JmsTemplate
import org.springframework.scheduling.annotation.EnableAsync
import tools.jackson.databind.json.JsonMapper
import tools.jackson.dataformat.xml.XmlMapper
import java.nio.charset.Charset

@EnableAsync
@SpringBootApplication
class TestClientApplication(
    private val jmsTemplate: JmsTemplate,
    private val asyncClient: AsyncClient,
    @Value("classpath:example-data/9BM9581341H041434.xml") private val file1: Resource,
    @Value("classpath:example-data/WDB9700751K874214.xml") private val file2: Resource,
    private val jsonMapper: JsonMapper,
    private val xmlMapper: XmlMapper,
) {

    @Bean
    fun run() = CommandLineRunner {

        jmsTemplate.convertAndSend("DEV.QUEUE.VEHICLE.CREATE", file2.getContentAsString(Charset.defaultCharset()))

        Thread.sleep(1000)

        val response = jmsTemplate.sendAndReceive("DEV.QUEUE.VEHICLE.GET.REQUEST", "DEV.QUEUE.VEHICLE.GET.RESPONSE") { session ->
            session.createTextMessage(xmlMapper.writeValueAsString(VehicleGetRequest("WDB9700751K874214", version = 2)))
        } as TextMessage

        println(response.text)

        val updateVehicle = """
            <vehicleUpdate>
                <vehicleDatacard>
                    <fin>WDB9700751K874214</fin>
                    <vehicleModelDescription>Updated model description</vehicleModelDescription>
                    <fixingPartsAvailable>true</fixingPartsAvailable>
                </vehicleDatacard>
                <reference>
                    <company>Updated company</company>
                </reference>
            </vehicleUpdate>
        """.trimIndent()

        jmsTemplate.convertAndSend("DEV.QUEUE.VEHICLE.UPDATE", updateVehicle)

//        for (i in 1..50) {
//            asyncClient.read("WDB9700751K874214")
//        }
//
//        Thread.sleep(30000)

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

data class VehicleGetRequest(
    val fin: String,
    val version: Int = 2,
)
