package com.example.loadtest

import com.ibm.mq.jakarta.jms.MQQueueConnectionFactory
import com.ibm.msg.client.jakarta.wmq.WMQConstants
import io.gatling.javaapi.core.CoreDsl.constantUsersPerSec
import io.gatling.javaapi.core.CoreDsl.global
import io.gatling.javaapi.core.CoreDsl.rampUsersPerSec
import io.gatling.javaapi.core.CoreDsl.scenario
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.jms.JmsDsl.jms
import jakarta.jms.Connection
import jakarta.jms.ConnectionFactory
import jakarta.jms.Session
import jakarta.jms.TextMessage
import java.time.Duration
import java.util.concurrent.ThreadLocalRandom
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

    private val existingFin = "WDB9700751K874214"
    private val seedVehicleCount = 10
    private val seededFins = buildSeededFins(existingFin, seedVehicleCount)
    private val testDuration = Duration.ofSeconds(3600)

    private val readRate = 10.0
    private val createRate = 1.0 / 60.0
    private val updateRate = 3.0
    private val warmUpEnabled = true
    private val warmUpDuration = Duration.ofSeconds(60)
    private val warmUpReadStartRate = 1.0
    private val warmUpReadEndRate = 5.0
    private val vehicleXmlTemplate = loadResource(LARGE_VEHICLE_TEMPLATE)
    private val replyTimeoutMillis = 60_000L
    private val mqUser = "app"
    private val mqPassword = "password"
    private val connectionFactory = ibmMqConnectionFactory()
    private val seedVehicleEnabled = true
    private val seedTimeout = Duration.ofSeconds(30)
    private val seedPollInterval = Duration.ofMillis(500)

    private val jmsProtocol = jms
        .connectionFactory(connectionFactory)
        .credentials(mqUser, mqPassword)
        .listenerThreadCount(8)
        .replyTimeout(replyTimeoutMillis)
        .matchByMessageId()

    private val warmUpReads = scenario("vehicle read warm-up")
        .exec(
            jms("warm-up read vehicle")
                .requestReply()
                .queue(GET_REQUEST_QUEUE)
                .replyQueue(GET_RESPONSE_QUEUE)
                .textMessage { readVehicleXml(randomSeededFin()) }
        )

    private val reads = scenario("vehicle reads")
        .exec(
            jms("read vehicle")
                .requestReply()
                .queue(GET_REQUEST_QUEUE)
                .replyQueue(GET_RESPONSE_QUEUE)
                .textMessage { readVehicleXml(randomSeededFin()) }
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
                .textMessage { updateVehicleXml(randomSeededFin()) }
        )

    init {
        val mainLoad = listOf(
            reads.injectOpen(constantUsersPerSec(readRate).during(testDuration)),
            creates.injectOpen(constantUsersPerSec(createRate).during(testDuration)),
            updates.injectOpen(constantUsersPerSec(updateRate).during(testDuration)),
        )

        val load = if (warmUpEnabled && !warmUpDuration.isZero && !warmUpDuration.isNegative) {
            listOf(
                warmUpReads.injectOpen(
                    rampUsersPerSec(warmUpReadStartRate)
                        .to(warmUpReadEndRate)
                        .during(warmUpDuration)
                ).andThen(mainLoad)
            )
        } else {
            mainLoad
        }

        setUp(load)
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
            hostName = "localhost"
            port = 1414
            queueManager = "QM1"
            channel = "DEV.APP.SVRCONN"
            transportType = WMQConstants.WMQ_CM_CLIENT
        }
    }

    private fun uniqueFin(): String {
        return "LT" + UUID.randomUUID().toString().replace("-", "").take(15)
    }

    private fun buildSeededFins(baseFin: String, count: Int): List<String> {
        require(count > 0) { "seedVehicleCount must be greater than 0" }
        return listOf(baseFin) + (1 until count).map { index ->
            val suffix = index.toString(36).uppercase().padStart(2, '0')
            baseFin.dropLast(2) + suffix
        }
    }

    private fun randomSeededFin(): String {
        return seededFins[ThreadLocalRandom.current().nextInt(seededFins.size)]
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
        seededFins.forEach { fin ->
            if (readVehicleOverJms(fin).isVehicleFound()) {
                println("Seed vehicle $fin already exists")
                return@forEach
            }

            println("Seed vehicle $fin not found; publishing create message")
            sendTextMessage(CREATE_QUEUE, largeVehicleXml(fin))

            val deadline = System.nanoTime() + seedTimeout.toNanos()
            while (System.nanoTime() < deadline) {
                Thread.sleep(seedPollInterval.toMillis())
                if (readVehicleOverJms(fin).isVehicleFound()) {
                    println("Seed vehicle $fin is readable")
                    return@forEach
                }
            }

            error("Seed vehicle $fin was not readable within ${seedTimeout.seconds} seconds")
        }
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
