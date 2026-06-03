package com.example.vedoc.vehicle

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(value = ["/api/vehicles"])
class VehicleController(
    private val vehicleService: VehicleService,
) {

    @GetMapping("/{fin}")
    fun getVehicle(@PathVariable fin: String): ResponseEntity<VehicleDTO> {
        return vehicleService.getVehicle(fin)
            ?.toDto()
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @PatchMapping("/{fin}")
    fun updateVehicle(
        @PathVariable fin: String,
        @RequestBody vehicleUpdate: VehicleDTO
    ): ResponseEntity<VehicleDTO> {
        return vehicleService.updateVehicle(fin, vehicleUpdate)
            ?.toDto()
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

}
