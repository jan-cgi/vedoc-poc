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
            vehicleDatacard = VehicleDatacard(
                fin = "9BM9581341H041434",
                productSeries = "958",
                productSeriesBrand = "mb",
                productSeriesDesignation = "Baureihe 958",
                vehicleModelDesignation = "958134",
            ),
            reference = Reference(
                productgroup = ReferenceItem(
                    id = "3",
                    designation = "TRUCK",
                ),
                company = ReferenceItem(
                    id = "1",
                    designation = "Daimler Truck AG",
                ),
            ),
        )

        vehicleRepository.save(vehicle)
    }
}
