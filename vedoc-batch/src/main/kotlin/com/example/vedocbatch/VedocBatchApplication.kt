package com.example.vedocbatch

import com.example.vedocbatch.vehicle.VehicleService
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class VedocBatchApplication(private val vehicleService: VehicleService) {

    @Bean
    fun run() = CommandLineRunner {
        vehicleService.importVehiclesFromBucket()
    }

}

fun main(args: Array<String>) {
    runApplication<VedocBatchApplication>(*args)
}
