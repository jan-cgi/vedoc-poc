package com.example.xmljsonadapter.vehicle

data class VehicleGetRequest(
    val fin: String,
    val version: Int,
)

data class Vehicle(
    val vehicleDatacard: VehicleDatacard? = null,
    val reference: Reference? = null,
)

data class VehicleDatacard(
    val checkDigit: String? = null,
    val fin: String? = null,
    val fixingPartsAvailable: Boolean? = null,
    val prodOrderTextAvailable: Boolean? = null,
    val productGroupIndication: String? = null,
    val productSeries: String? = null,
    val productSeriesBrand: String? = null,
    val productSeriesDesignation: String? = null,
    val vehicleModelDescription: String? = null,
    val vehicleModelDesignation: String? = null,
    val activeAssignedFpd: Any? = null,
    val activeCustomerServiceData: Any? = null,
    val activeModelPlate: Any? = null,
    val activeProductDate: Any? = null,
    val activeProductionInfo: Any? = null,
    val activeState: Any? = null,
)

data class Reference(
    val productgroup: Any? = null,
    val company: Any? = null,
)

data class VehicleV1(
    val vehicleDatacard: VehicleDatacardV1? = null,
    val reference: Reference? = null,
)

data class VehicleDatacardV1(
    val check: String? = null,
    val fin: String? = null,
    val fixingPartsAvailable: Boolean? = null,
    val prodOrderTextAvailable: Boolean? = null,
    val productGroupIndication: String? = null,
    val productSeries: String? = null,
    val productSeriesBrand: String? = null,
    val productSeriesDesignation: String? = null,
    val vehicleModelDescription: String? = null,
    val vehicleModelDesignation: String? = null,
    val activeAssignedFpd: Any? = null,
    val activeCustomerServiceData: Any? = null,
    val activeModelPlate: Any? = null,
    val activeProductDate: Any? = null,
    val activeProductionInfo: Any? = null,
    val activeState: Any? = null,
)

fun Vehicle.toV1() = VehicleV1(
    vehicleDatacard = vehicleDatacard?.toV1(),
    reference = reference,
)

private fun VehicleDatacard.toV1() = VehicleDatacardV1(
    check = checkDigit,
    fin = fin,
    fixingPartsAvailable = fixingPartsAvailable,
    prodOrderTextAvailable = prodOrderTextAvailable,
    productGroupIndication = productGroupIndication,
    productSeries = productSeries,
    productSeriesBrand = productSeriesBrand,
    productSeriesDesignation = productSeriesDesignation,
    vehicleModelDescription = vehicleModelDescription,
    vehicleModelDesignation = vehicleModelDesignation,
    activeAssignedFpd = activeAssignedFpd,
    activeCustomerServiceData = activeCustomerServiceData,
    activeModelPlate = activeModelPlate,
    activeProductDate = activeProductDate,
    activeProductionInfo = activeProductionInfo,
    activeState = activeState,
)
