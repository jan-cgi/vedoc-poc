package com.example.vedoc.vehicle

import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Profile("web")
@Service
class VehicleService(
    private val vehicleRepository: VehicleRepository,
    private val vehicleUpdateEventRepository: VehicleUpdateEventRepository
) {

    fun getVehicle(fin: String): Vehicle? {
        return vehicleRepository.findVehicleByVehicleDatacardFin(fin)
    }

    fun createVehicle(vehicle: Vehicle): Vehicle {
        return vehicleRepository.save(vehicle)
    }

    @Transactional
    fun updateVehicle(fin: String, vehicleUpdate: VehicleDTO): Vehicle? {
        val vehicle = vehicleRepository.findVehicleByVehicleDatacardFin(fin) ?: return null

        val mergedVehicle = vehicle.merge(vehicleUpdate)
        val changes = vehicle.changesTo(mergedVehicle)

        vehicleRepository.save(mergedVehicle)

        vehicleUpdateEventRepository.save(
            VehicleUpdateEvent(
                fin = fin,
                vehicleId = mergedVehicle.id!!,
                changes = changes
            )
        )

        return mergedVehicle
    }

    private fun Vehicle.merge(update: VehicleDTO) = copy(
        vehicleDatacard = vehicleDatacard.merge(update.vehicleDatacard),
        reference = reference.merge(update.reference)
    )

    private fun Vehicle.changesTo(updatedVehicle: Vehicle): List<FieldChange> = buildList {
        addChange(
            "vehicleDatacard.checkDigit",
            vehicleDatacard?.checkDigit,
            updatedVehicle.vehicleDatacard?.checkDigit
        )
        addChange(
            "vehicleDatacard.fixingPartsAvailable",
            vehicleDatacard?.fixingPartsAvailable,
            updatedVehicle.vehicleDatacard?.fixingPartsAvailable
        )
        addChange(
            "vehicleDatacard.prodOrderTextAvailable",
            vehicleDatacard?.prodOrderTextAvailable,
            updatedVehicle.vehicleDatacard?.prodOrderTextAvailable
        )
        addChange(
            "vehicleDatacard.productGroupIndication",
            vehicleDatacard?.productGroupIndication,
            updatedVehicle.vehicleDatacard?.productGroupIndication
        )
        addChange(
            "vehicleDatacard.productSeries",
            vehicleDatacard?.productSeries,
            updatedVehicle.vehicleDatacard?.productSeries
        )
        addChange(
            "vehicleDatacard.productSeriesBrand",
            vehicleDatacard?.productSeriesBrand,
            updatedVehicle.vehicleDatacard?.productSeriesBrand
        )
        addChange(
            "vehicleDatacard.productSeriesDesignation",
            vehicleDatacard?.productSeriesDesignation,
            updatedVehicle.vehicleDatacard?.productSeriesDesignation
        )
        addChange(
            "vehicleDatacard.vehicleModelDescription",
            vehicleDatacard?.vehicleModelDescription,
            updatedVehicle.vehicleDatacard?.vehicleModelDescription
        )
        addChange(
            "vehicleDatacard.vehicleModelDesignation",
            vehicleDatacard?.vehicleModelDesignation,
            updatedVehicle.vehicleDatacard?.vehicleModelDesignation
        )
        addChange(
            "vehicleDatacard.activeAssignedFpd",
            vehicleDatacard?.activeAssignedFpd,
            updatedVehicle.vehicleDatacard?.activeAssignedFpd
        )
        addChange(
            "vehicleDatacard.activeCustomerServiceData",
            vehicleDatacard?.activeCustomerServiceData,
            updatedVehicle.vehicleDatacard?.activeCustomerServiceData
        )
        addChange(
            "vehicleDatacard.activeModelPlate",
            vehicleDatacard?.activeModelPlate,
            updatedVehicle.vehicleDatacard?.activeModelPlate
        )
        addChange(
            "vehicleDatacard.activeProductDate",
            vehicleDatacard?.activeProductDate,
            updatedVehicle.vehicleDatacard?.activeProductDate
        )
        addChange(
            "vehicleDatacard.activeProductionInfo",
            vehicleDatacard?.activeProductionInfo,
            updatedVehicle.vehicleDatacard?.activeProductionInfo
        )
        addChange(
            "vehicleDatacard.activeState",
            vehicleDatacard?.activeState,
            updatedVehicle.vehicleDatacard?.activeState
        )
        addChange(
            "reference.productgroup",
            reference?.productgroup,
            updatedVehicle.reference?.productgroup
        )
        addChange(
            "reference.company",
            reference?.company,
            updatedVehicle.reference?.company
        )
    }

    private fun MutableList<FieldChange>.addChange(path: String, oldValue: Any?, newValue: Any?) {
        if (oldValue != newValue) {
            add(FieldChange(path, oldValue, newValue))
        }
    }

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
