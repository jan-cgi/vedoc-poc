package com.example.vedoc.vehicle

import com.example.vedoc.config.RabbitMQConfig.Companion.VEHICLE_EXCHANGE
import com.example.vedoc.config.RabbitMQConfig.Companion.VEHICLE_UPDATE_KEY
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import tools.jackson.databind.json.JsonMapper

@Service
class VehicleService(
    private val vehicleRepository: VehicleRepository,
    private val rabbitTemplate: RabbitTemplate,
    private val jsonMapper: JsonMapper
) {

    fun getVehicle(fin: String): Vehicle? {
        return vehicleRepository.findByVehicleDatacardFin(fin)
    }

    fun updateVehicle(fin: String, vehicleUpdate: VehicleDTO): Vehicle? {
        val vehicle = vehicleRepository.findByVehicleDatacardFin(fin) ?: return null

        val mergedVehicle = vehicle.merge(vehicleUpdate)
        val updatedVehicle = vehicleRepository.save(mergedVehicle)

        rabbitTemplate.convertAndSend(
            VEHICLE_EXCHANGE,
            VEHICLE_UPDATE_KEY,
            jsonMapper.writeValueAsString(updatedVehicle.toDto())
        )

        return updatedVehicle
    }

    private fun Vehicle.merge(update: VehicleDTO) = copy(
        vehicleDatacard = vehicleDatacard.merge(update.vehicleDatacard),
        reference = reference.merge(update.reference)
    )

    private fun VehicleDatacard?.merge(update: VehicleDatacard?) = update?.let {
        VehicleDatacard(
            checkDigit = it.checkDigit ?: this?.checkDigit,
            fin = it.fin ?: this?.fin,
            fixingPartsAvailable = it.fixingPartsAvailable ?: this?.fixingPartsAvailable,
            prodOrderTextAvailable = it.prodOrderTextAvailable ?: this?.prodOrderTextAvailable,
            productGroupIndication = it.productGroupIndication ?: this?.productGroupIndication,
            productSeries = it.productSeries ?: this?.productSeries,
            productSeriesBrand = it.productSeriesBrand ?: this?.productSeriesBrand,
            productSeriesDesignation = it.productSeriesDesignation ?: this?.productSeriesDesignation,
            vehicleModelDescription = it.vehicleModelDescription ?: this?.vehicleModelDescription,
            vehicleModelDesignation = it.vehicleModelDesignation ?: this?.vehicleModelDesignation,
            activeAssignedFpd = it.activeAssignedFpd ?: this?.activeAssignedFpd,
            activeCustomerServiceData = it.activeCustomerServiceData ?: this?.activeCustomerServiceData,
            activeModelPlate = it.activeModelPlate ?: this?.activeModelPlate,
            activeProductDate = it.activeProductDate ?: this?.activeProductDate,
            activeProductionInfo = it.activeProductionInfo ?: this?.activeProductionInfo,
            activeState = it.activeState ?: this?.activeState
        )
    } ?: this

    private fun Reference?.merge(update: Reference?) = update?.let {
        Reference(
            productgroup = it.productgroup ?: this?.productgroup,
            company = it.company ?: this?.company
        )
    } ?: this

}
