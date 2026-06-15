package com.example.vedoc.vehicle

import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Profile("web")
@Service
class VehicleService(
    private val vehicleRepository: VehicleRepository,
    private val vehicleUpdateEventRepository: VehicleUpdateEventRepository,
    private val mongoTemplate: MongoTemplate
) {

    fun getVehicle(fin: String): Vehicle? {
        return vehicleRepository.findVehicleByVehicleDatacardFin(fin)
    }

    fun createVehicle(vehicle: Vehicle): Vehicle {
        return vehicleRepository.save(vehicle)
    }

    @Transactional
    fun updateVehicle(fin: String, vehicleUpdate: VehicleDTO) {
        val fieldUpdates = vehicleUpdate.toFieldUpdates()
        if (fieldUpdates.isEmpty()) return

        val vehicle = findVehicleForUpdate(fin, fieldUpdates)
            ?: throw RuntimeException("No vehicle found with fin: $fin")

        val mongoUpdate = Update()
        fieldUpdates.forEach {
            mongoUpdate.set(it.path, it.value)
        }

        mongoTemplate.updateFirst(queryByFin(fin), mongoUpdate, Vehicle::class.java)

        vehicleUpdateEventRepository.save(
            VehicleUpdateEvent(
                fin = fin,
                vehicleId = vehicle.id!!,
                changes = vehicle.fieldChangesFor(fieldUpdates)
            )
        )
    }

    private fun findVehicleForUpdate(fin: String, fieldUpdates: List<FieldUpdate>): Vehicle? {
        val query = queryByFin(fin)
        query.fields()
            .include("_id")
            .include("vehicleDatacard.fin")

        fieldUpdates.forEach {
            query.fields().include(it.path)
        }

        return mongoTemplate.findOne(query, Vehicle::class.java)
    }

    private fun queryByFin(fin: String): Query {
        return Query.query(Criteria.where("vehicleDatacard.fin").`is`(fin))
    }

    private fun Vehicle.fieldChangesFor(fieldUpdates: List<FieldUpdate>): List<FieldChange> {
        return fieldUpdates.map { FieldChange(it.path, oldValue = valueAt(it.path), newValue = it.value) }
    }

    private fun Vehicle.valueAt(path: String): Any? {
        return when (path) {
            "vehicleDatacard.checkDigit" -> vehicleDatacard?.checkDigit
            "vehicleDatacard.fixingPartsAvailable" -> vehicleDatacard?.fixingPartsAvailable
            "vehicleDatacard.prodOrderTextAvailable" -> vehicleDatacard?.prodOrderTextAvailable
            "vehicleDatacard.productGroupIndication" -> vehicleDatacard?.productGroupIndication
            "vehicleDatacard.productSeries" -> vehicleDatacard?.productSeries
            "vehicleDatacard.productSeriesBrand" -> vehicleDatacard?.productSeriesBrand
            "vehicleDatacard.productSeriesDesignation" -> vehicleDatacard?.productSeriesDesignation
            "vehicleDatacard.vehicleModelDescription" -> vehicleDatacard?.vehicleModelDescription
            "vehicleDatacard.vehicleModelDesignation" -> vehicleDatacard?.vehicleModelDesignation
            "vehicleDatacard.activeAssignedFpd" -> vehicleDatacard?.activeAssignedFpd
            "vehicleDatacard.activeCustomerServiceData" -> vehicleDatacard?.activeCustomerServiceData
            "vehicleDatacard.activeModelPlate" -> vehicleDatacard?.activeModelPlate
            "vehicleDatacard.activeProductDate" -> vehicleDatacard?.activeProductDate
            "vehicleDatacard.activeProductionInfo" -> vehicleDatacard?.activeProductionInfo
            "vehicleDatacard.activeState" -> vehicleDatacard?.activeState
            "reference.productgroup" -> reference?.productgroup
            "reference.company" -> reference?.company
            else -> error("Unsupported update path: $path")
        }
    }

    private fun VehicleDTO.toFieldUpdates(): List<FieldUpdate> = buildList {
        vehicleDatacard?.let {
            addUpdate("vehicleDatacard.checkDigit", it.checkDigit)
            addUpdate("vehicleDatacard.fixingPartsAvailable", it.fixingPartsAvailable)
            addUpdate("vehicleDatacard.prodOrderTextAvailable", it.prodOrderTextAvailable)
            addUpdate("vehicleDatacard.productGroupIndication", it.productGroupIndication)
            addUpdate("vehicleDatacard.productSeries", it.productSeries)
            addUpdate("vehicleDatacard.productSeriesBrand", it.productSeriesBrand)
            addUpdate("vehicleDatacard.productSeriesDesignation", it.productSeriesDesignation)
            addUpdate("vehicleDatacard.vehicleModelDescription", it.vehicleModelDescription)
            addUpdate("vehicleDatacard.vehicleModelDesignation", it.vehicleModelDesignation)
            addUpdate("vehicleDatacard.activeAssignedFpd", it.activeAssignedFpd)
            addUpdate("vehicleDatacard.activeCustomerServiceData", it.activeCustomerServiceData)
            addUpdate("vehicleDatacard.activeModelPlate", it.activeModelPlate)
            addUpdate("vehicleDatacard.activeProductDate", it.activeProductDate)
            addUpdate("vehicleDatacard.activeProductionInfo", it.activeProductionInfo)
            addUpdate("vehicleDatacard.activeState", it.activeState)
        }

        reference?.let {
            addUpdate("reference.productgroup", it.productgroup)
            addUpdate("reference.company", it.company)
        }
    }

    private fun MutableList<FieldUpdate>.addUpdate(path: String, value: Any?) {
        if (value != null) {
            add(FieldUpdate(path, value))
        }
    }

    private data class FieldUpdate(
        val path: String,
        val value: Any
    )

}
