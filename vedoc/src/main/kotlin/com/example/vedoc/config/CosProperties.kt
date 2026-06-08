package com.example.vedoc.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "ibm.cos")
data class CosProperties(
    val endpoint: String,
    val location: String,
    val bucket: String,
    val apiKey: String,
    val serviceInstanceId: String,
)
