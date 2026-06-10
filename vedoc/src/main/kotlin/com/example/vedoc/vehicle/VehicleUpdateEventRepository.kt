package com.example.vedoc.vehicle

import org.springframework.data.mongodb.repository.MongoRepository

interface VehicleUpdateEventRepository : MongoRepository<VehicleUpdateEvent, String> {

    fun findByIsPublishedFalseOrderByChangedAtAsc(): List<VehicleUpdateEvent>

}
