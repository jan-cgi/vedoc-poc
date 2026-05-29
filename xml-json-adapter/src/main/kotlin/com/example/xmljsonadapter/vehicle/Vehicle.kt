package com.example.xmljsonadapter.vehicle

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
