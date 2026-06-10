package com.example.vedoc.vehicle

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant

@Document("vehicle_update_events")
data class VehicleUpdateEvent(
    @Id
    val id: String? = null,
    val fin: String,
    val vehicleId: String,
    val changedAt: Instant = Instant.now(),
    val changes: List<FieldChange>,
    val isPublished: Boolean = false,
)

data class VehicleUpdateEventDTO(
    val fin: String,
    val vehicleId: String,
    val changedAt: Instant = Instant.now(),
    val changes: List<FieldChange>,
)

data class FieldChange(
    val path: String,
    val oldValue: Any?,
    val newValue: Any?,
)

fun VehicleUpdateEvent.toDTO(): VehicleUpdateEventDTO {
    return VehicleUpdateEventDTO(
        fin = fin,
        vehicleId = vehicleId,
        changedAt = changedAt,
        changes = changes,
    )
}
