package com.example.testclient

import org.springframework.jms.annotation.JmsListener
import org.springframework.stereotype.Component

@Component
class IBMMQConsumer {

    @JmsListener(destination = "DEV.QUEUE.VEHICLE.UPDATE")
    fun updateVehicle(vehicleXML: String) {
        println(vehicleXML)
    }

}
