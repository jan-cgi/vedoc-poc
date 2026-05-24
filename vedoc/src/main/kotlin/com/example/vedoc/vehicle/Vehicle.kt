package com.example.vedoc.vehicle

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("vehicles")
data class Vehicle(
    @Id
    val id: String? = null,
    val vin: String,
    val make: String,
    val model: String,
    val year: Int,
)
