package com.example.vedoc.config

import com.example.vedoc.vehicle.Vehicle
import com.example.vedoc.vehicle.VehicleUpdateEvent
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.index.Index
import org.springframework.data.mongodb.core.index.PartialIndexFilter
import org.springframework.data.mongodb.core.query.Criteria

@Configuration
class MongoIndexConfig(private val mongoTemplate: MongoTemplate) {

    @EventListener(ContextRefreshedEvent::class)
    fun createIndexes() {
        mongoTemplate.indexOps(Vehicle::class.java).createIndex(
            Index()
                .on("vehicleDatacard.fin", Sort.Direction.ASC)
                .unique()
                .named("ux_vehicles_vehicleDatacard_fin")
        )

        mongoTemplate.indexOps(VehicleUpdateEvent::class.java).createIndex(
            Index()
                .on("isPublished", Sort.Direction.ASC)
                .on("changedAt", Sort.Direction.ASC)
                .partial(PartialIndexFilter.of(Criteria.where("isPublished").`is`(false)))
                .named("idx_vehicle_update_events_unpublished_changedAt")
        )
    }

}
