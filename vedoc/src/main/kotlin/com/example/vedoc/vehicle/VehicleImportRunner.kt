package com.example.vedoc.vehicle

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class VehicleImportRunner(
    private val vehicleRepository: VehicleRepository,
) : CommandLineRunner {
    override fun run(vararg args: String) {
        println("Starting Vehicle...")

        val vehicle = Vehicle(
            vin = "9BM9581341H041434",
            make = "Mercedes-Benz",
            model = "Actros",
            year = 2017,
        )

        vehicleRepository.save(vehicle)
    }
}
