package com.example.xmljsonadapter.vehicle

import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class Runner(private val vehicleService: VehicleService) : CommandLineRunner {

    override fun run(vararg args: String) {
        val vehicle = """
        {
          "vehicleDatacard": {
            "fin": "9BM9581341H041434",
            "productSeries": "958",
            "productSeriesBrand": "mb",
            "productSeriesDesignation": "Baureihe 958",
            "vehicleModelDesignation": "958134"
          },
          "reference": {
            "productgroup": {
              "id": "3",
              "designation": "TRUCK"
            },
            "company": {
              "id": "1",
              "designation": "Daimler Truck AG"
            }
          }
        }
        """.trimIndent()

        vehicleService.createVehicle(vehicle)
        Thread.sleep(1000)
        print(vehicleService.getVehicle("9BM9581341H041434"))
    }

}
