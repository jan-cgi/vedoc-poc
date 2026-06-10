package com.example.vedoc.vehicle

import org.springframework.boot.CommandLineRunner
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

@Profile("batch-publish-update")
@Component
class VehicleUpdateEventPublisherRunner(
    private val vehicleUpdateEventPublisherService: VehicleUpdateEventPublisherService,
    private val applicationContext: ConfigurableApplicationContext
) : CommandLineRunner {

    override fun run(args: Array<String>) {
        applicationContext.use { _ ->
            vehicleUpdateEventPublisherService.publishUnpublishedEvents()
        }
    }

}
