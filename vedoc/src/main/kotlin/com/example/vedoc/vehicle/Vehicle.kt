package com.example.vedoc.vehicle

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document("vehicles")
data class Vehicle(
    @Id
    val id: String? = null,
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
    val activeAssignedFpd: AssignedFpd? = null,
    val activeCustomerServiceData: CustomerServiceData? = null,
    val activeModelPlate: ModelPlate? = null,
    val activeProductDate: ProductDate? = null,
    val activeProductionInfo: ProductionInfo? = null,
    val activeState: VehicleState? = null,
)

data class AssignedFpd(
    val equipmentCodes: List<EquipmentCode> = emptyList(),
    val saa: List<Saa> = emptyList(),
)

data class EquipmentCode(
    val code: String? = null,
    val codeType: String? = null,
    val designation: String? = null,
    val productGroupIndication: String? = null,
)

data class Saa(
    val amountPerSaa: Int? = null,
    val description: String? = null,
    val id: String? = null,
    val designGroup: List<String> = emptyList(),
)

data class CustomerServiceData(
    val archiveIndicator: Boolean? = null,
    val objectId: Long? = null,
    val validSince: String? = null,
    val xmlId: String? = null,
)

data class ModelPlate(
    val xmlId: String? = null,
)

data class ProductDate(
    val dateOfFirstRegistration: String? = null,
    val shipmentDate: String? = null,
    val technicalApprovalDate: String? = null,
    val validSince: String? = null,
    val objectId: Long? = null,
    val xmlId: String? = null,
)

data class ProductionInfo(
    val originAxleFront1Id: String? = null,
    val originAxleFront1ObjectVarNo: String? = null,
    val originAxleRear1Id: String? = null,
    val originAxleRear1ObjectVarNo: String? = null,
    val originCabId: String? = null,
    val originCabObjectVarNo: String? = null,
    val originEngineId: String? = null,
    val originEngineObjectNo: String? = null,
    val originEngineObjectVarNo: String? = null,
    val originTransmissionId: String? = null,
    val originTransmissionObjectVarNo: String? = null,
    val validSince: String? = null,
    val xmlId: String? = null,
    val objectId: Long? = null,
)

data class VehicleState(
    val additionalEquipmentInfo: String? = null,
    val axle: List<VehicleComponent> = emptyList(),
    val cab: VehicleComponent? = null,
    val checkDigitProductionNumber: String? = null,
    val cocData: CocData? = null,
    val cvBodyType: String? = null,
    val cvRims: String? = null,
    val electroVariant: List<ElectroVariant> = emptyList(),
    val engine: VehicleComponent? = null,
    val orderNumber: String? = null,
    val paint: List<Paint> = emptyList(),
    val plant: String? = null,
    val productionNumber: String? = null,
    val status: String? = null,
    val steeringInfoId: String? = null,
    val technicalData: TechnicalData? = null,
    val tireData: List<TireData> = emptyList(),
    val transmission: VehicleComponent? = null,
    val vin: String? = null,
)

data class VehicleComponent(
    val dataCardAvailable: Boolean? = null,
    val id: String? = null,
    val typeOf: String? = null,
    val modelDesignation: String? = null,
    val objectNumber: String? = null,
    val objectNumberVariant: String? = null,
    val activeAssignedFpd: AssignedFpd? = null,
    val activeProductDate: ProductDate? = null,
    val activeState: ComponentState? = null,
)

data class ComponentState(
    val plant: String? = null,
    val productionNumber: String? = null,
    val status: String? = null,
    val type: String? = null,
    val wheel: String? = null,
    val transmissionModel: String? = null,
)

data class CocData(
    val cocNumber: String? = null,
)

data class ElectroVariant(
    val electroVariantDesignation: String? = null,
    val number: String? = null,
    val objectNumber: String? = null,
    val usage: String? = null,
)

data class Paint(
    val code: String? = null,
    val designation: String? = null,
    val usage: String? = null,
)

data class TechnicalData(
    val correction: String? = null,
    val impulsValue: String? = null,
    val serviceNumber: String? = null,
    val switchSettings: String? = null,
    val objectId: Long? = null,
    val kvalue: String? = null,
)

data class TireData(
    val content: String? = null,
    val typeOf: String? = null,
    val objectId: Long? = null,
)

data class Reference(
    val productgroup: ReferenceItem? = null,
    val company: ReferenceItem? = null,
)

data class ReferenceItem(
    val id: String? = null,
    val designation: String? = null,
    val type: String? = null,
)
