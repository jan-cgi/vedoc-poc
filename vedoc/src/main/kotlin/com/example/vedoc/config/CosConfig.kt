package com.example.vedoc.config

import com.ibm.cos.v2.auth.credentials.AwsCredentials
import com.ibm.cos.v2.auth.credentials.StaticCredentialsProvider
import com.ibm.cos.v2.auth.credentials.ibmOAuth.BasicIBMOAuthCredentials
import com.ibm.cos.v2.regions.Region
import com.ibm.cos.v2.services.s3.S3Client
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import java.net.URI

@Profile("batch")
@Configuration
@EnableConfigurationProperties(CosProperties::class)
class CosConfig {

    @Bean
    fun cosClient(properties: CosProperties): S3Client {
        val credentials: AwsCredentials = BasicIBMOAuthCredentials(
            properties.apiKey,
            properties.serviceInstanceId
        )

        return S3Client.builder()
            .endpointOverride(URI.create(properties.endpoint))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .region(Region.of(properties.location))
            .build()
    }

}
