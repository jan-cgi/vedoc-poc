package com.example.loadtest

import com.ibm.mq.jakarta.jms.MQQueueConnectionFactory
import com.ibm.msg.client.jakarta.wmq.WMQConstants
import io.gatling.javaapi.core.CoreDsl.constantUsersPerSec
import io.gatling.javaapi.core.CoreDsl.global
import io.gatling.javaapi.core.CoreDsl.scenario
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.jms.JmsDsl.jms
import jakarta.jms.Connection
import jakarta.jms.ConnectionFactory
import jakarta.jms.Session
import jakarta.jms.TextMessage
import java.time.Duration
import java.util.UUID

class VedocIbmMqSimulation : Simulation() {

    private companion object {
        const val CREATE_QUEUE = "DEV.QUEUE.VEHICLE.CREATE"
        const val GET_REQUEST_QUEUE = "DEV.QUEUE.VEHICLE.GET.REQUEST"
        const val GET_RESPONSE_QUEUE = "DEV.QUEUE.VEHICLE.GET.RESPONSE"
        const val UPDATE_QUEUE = "DEV.QUEUE.VEHICLE.UPDATE"
        const val LARGE_VEHICLE_TEMPLATE = "example-data/large-vehicle-template.xml"
        const val FIN_PLACEHOLDER = "{{FIN}}"
        const val VEHICLE_NOT_FOUND_PREFIX = "No vehicle found with fin:"
    }

    private val existingFin = property("vehicleFin", "WDB9700751K874214")
    private val testDuration = Duration.ofSeconds(property("durationSeconds", "300").toLong())

    private val readRate = property("readRate", "10.0").toDouble()
    private val createRate = property("createRate", (1.0 / 60.0).toString()).toDouble()
    private val updateRate = property("updateRate", "3.0").toDouble()
    private val vehicleXmlTemplate = loadResource(property("vehicleXmlResource", LARGE_VEHICLE_TEMPLATE))
    private val replyTimeoutMillis = property("replyTimeoutMillis", "10000").toLong()
    private val mqUser = property("mqUser", "IBM_MQ_USER", "app")
    private val mqPassword = property("mqPassword", "IBM_MQ_PASSWORD", "password")
    private val connectionFactory = ibmMqConnectionFactory()
    private val seedVehicleEnabled = property("seedVehicle", "true").toBoolean()
    private val seedTimeout = Duration.ofSeconds(property("seedTimeoutSeconds", "30").toLong())
    private val seedPollInterval = Duration.ofMillis(property("seedPollIntervalMillis", "500").toLong())

    private val jmsProtocol = jms
        .connectionFactory(connectionFactory)
        .credentials(mqUser, mqPassword)
        .listenerThreadCount(property("jmsListenerThreads", "8").toInt())
        .replyTimeout(replyTimeoutMillis)
        .matchByMessageId()

    private val reads = scenario("vehicle reads")
        .exec(
            jms("read vehicle")
                .requestReply()
                .queue(GET_REQUEST_QUEUE)
                .replyQueue(GET_RESPONSE_QUEUE)
                .textMessage(readVehicleXml(existingFin))
        )

    private val creates = scenario("vehicle creates")
        .exec(
            jms("create vehicle")
                .send()
                .queue(CREATE_QUEUE)
                .textMessage { largeVehicleXml(uniqueFin()) }
        )

    private val updates = scenario("vehicle updates")
        .exec(
            jms("update vehicle")
                .send()
                .queue(UPDATE_QUEUE)
                .textMessage { updateVehicleXml(existingFin) }
        )

    init {
        setUp(
            reads.injectOpen(constantUsersPerSec(readRate).during(testDuration)),
            creates.injectOpen(constantUsersPerSec(createRate).during(testDuration)),
            updates.injectOpen(constantUsersPerSec(updateRate).during(testDuration)),
        )
            .protocols(jmsProtocol)
            .assertions(global().failedRequests().count().shouldBe(0L))
    }

    override fun before() {
        if (seedVehicleEnabled) {
            seedExistingVehicle()
        }
    }

    private fun ibmMqConnectionFactory(): ConnectionFactory {
        return MQQueueConnectionFactory().apply {
            hostName = property("mqHost", "IBM_MQ_HOST", "localhost")
            port = property("mqPort", "IBM_MQ_PORT", "1414").toInt()
            queueManager = property("mqQueueManager", "IBM_MQ_QUEUE_MANAGER", "QM1")
            channel = property("mqChannel", "IBM_MQ_CHANNEL", "DEV.APP.SVRCONN")
            transportType = WMQConstants.WMQ_CM_CLIENT
        }
    }

    private fun property(name: String, defaultValue: String): String {
        return property(name, name.replace(Regex("([a-z])([A-Z])"), "$1_$2").uppercase(), defaultValue)
    }

    private fun property(name: String, envName: String, defaultValue: String): String {
        return System.getProperty(name)
            ?: System.getenv(envName)
            ?: defaultValue
    }

    private fun uniqueFin(): String {
        return "LT" + UUID.randomUUID().toString().replace("-", "").take(15)
    }

    private fun readVehicleXml(fin: String): String {
        return """
            <vehicleGetRequest>
                <fin>$fin</fin>
                <version>2</version>
            </vehicleGetRequest>
        """.trimIndent()
    }

    private fun largeVehicleXml(fin: String): String {
        return vehicleXmlTemplate.replace(FIN_PLACEHOLDER, fin)
    }

    private fun updateVehicleXml(fin: String): String {
        return """
            <vehicleUpdate>
                <vehicleDatacard>
                    <fin>$fin</fin>
                    <vehicleModelDescription>Load test update ${System.nanoTime()}</vehicleModelDescription>
                    <fixingPartsAvailable>true</fixingPartsAvailable>
                </vehicleDatacard>
                <reference>
                    <company>Load Test Update</company>
                </reference>
            </vehicleUpdate>
        """.trimIndent()
    }

    private fun loadResource(path: String): String {
        val normalizedPath = path.removePrefix("/")
        return requireNotNull(Thread.currentThread().contextClassLoader.getResourceAsStream(normalizedPath)) {
            "Classpath resource not found: $normalizedPath"
        }
            .bufferedReader(Charsets.UTF_8)
            .use { it.readText() }
    }

    private fun seedExistingVehicle() {
        if (readVehicleOverJms(existingFin).isVehicleFound()) {
            println("Seed vehicle $existingFin already exists")
            return
        }

        println("Seed vehicle $existingFin not found; publishing create message")
        sendTextMessage(CREATE_QUEUE, largeVehicleXml(existingFin))

        val deadline = System.nanoTime() + seedTimeout.toNanos()
        while (System.nanoTime() < deadline) {
            Thread.sleep(seedPollInterval.toMillis())
            if (readVehicleOverJms(existingFin).isVehicleFound()) {
                println("Seed vehicle $existingFin is readable")
                return
            }
        }

        error("Seed vehicle $existingFin was not readable within ${seedTimeout.seconds} seconds")
    }

    private fun readVehicleOverJms(fin: String): String {
        return withJmsSession { connection, session ->
            val requestQueue = session.createQueue(GET_REQUEST_QUEUE)
            val responseQueue = session.createQueue(GET_RESPONSE_QUEUE)
            val producer = session.createProducer(requestQueue)
            producer.use {
                val request = session.createTextMessage(readVehicleXml(fin))
                request.jmsReplyTo = responseQueue
                producer.send(request)

                val consumer = session.createConsumer(
                    responseQueue,
                    "JMSCorrelationID = '${request.jmsMessageID}'",
                )
                consumer.use {
                    connection.start()
                    val response = consumer.receive(replyTimeoutMillis)
                    require(response is TextMessage) {
                        "Did not receive a text response while checking seed vehicle $fin"
                    }
                    response.text
                }
            }
        }
    }

    private fun sendTextMessage(queueName: String, text: String) {
        withJmsSession { _, session ->
            val queue = session.createQueue(queueName)
            val producer = session.createProducer(queue)
            producer.use {
                producer.send(session.createTextMessage(text))
            }
        }
    }

    private fun <T> withJmsSession(block: (Connection, Session) -> T): T {
        val connection = connectionFactory.createConnection(mqUser, mqPassword)
        return connection.use {
            val session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE)
            session.use {
                block(connection, session)
            }
        }
    }

    private fun String.isVehicleFound(): Boolean {
        return !startsWith(VEHICLE_NOT_FOUND_PREFIX)
    }
}
