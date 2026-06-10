package com.example.vedoc.vehicle

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("batch-file-sync")
@Component
class CosImportRunner(
    private val cosVehicleImportService: CosVehicleImportService
) : CommandLineRunner {

    override fun run(args: Array<String>) {
        cosVehicleImportService.importVehiclesFromBucket()
    }

}
