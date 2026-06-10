package com.example.xmljsonadapter.vehicle

import java.time.Instant

data class VehicleUpdateEvent(
    val fin: String,
    val vehicleId: String,
    val changedAt: Instant,
    val changes: List<FieldChange>,
)

data class FieldChange(
    val path: String,
    val oldValue: Any?,
    val newValue: Any?,
)
