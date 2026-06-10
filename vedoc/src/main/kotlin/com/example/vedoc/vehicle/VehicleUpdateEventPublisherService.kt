package com.example.vedoc.vehicle

import com.example.vedoc.config.RabbitMQConfig.Companion.VEHICLE_EXCHANGE
import com.example.vedoc.config.RabbitMQConfig.Companion.VEHICLE_UPDATE_EVENT_KEY
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import tools.jackson.databind.json.JsonMapper

@Profile("batch-publish-update")
@Service
class VehicleUpdateEventPublisherService(
    private val vehicleUpdateEventRepository: VehicleUpdateEventRepository,
    private val rabbitTemplate: RabbitTemplate,
    private val jsonMapper: JsonMapper
) {

    fun publishUnpublishedEvents(): Int {
        val events = vehicleUpdateEventRepository.findByIsPublishedFalseOrderByChangedAtAsc()

        events.forEach { event ->
            rabbitTemplate.convertAndSend(
                VEHICLE_EXCHANGE,
                VEHICLE_UPDATE_EVENT_KEY,
                jsonMapper.writeValueAsString(event.toDTO())
            )
            vehicleUpdateEventRepository.save(event.copy(isPublished = true))
        }

        return events.size
    }

}
