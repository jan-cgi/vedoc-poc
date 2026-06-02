package com.example.vedocbatch.vehicle

import org.springframework.data.mongodb.repository.MongoRepository

interface VehicleRepository : MongoRepository<Vehicle, String>
