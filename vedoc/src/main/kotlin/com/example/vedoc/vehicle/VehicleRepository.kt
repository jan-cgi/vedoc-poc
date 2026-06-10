package com.example.vedoc.vehicle

import org.springframework.data.mongodb.repository.MongoRepository

interface VehicleRepository : MongoRepository<Vehicle, String> {

    fun findVehicleByVehicleDatacardFin(fin: String): Vehicle?

}
