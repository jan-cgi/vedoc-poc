package com.example.vedocbatch.vehicle

import com.example.vedocbatch.config.CosProperties
import com.ibm.cos.v2.services.s3.S3Client
import com.ibm.cos.v2.services.s3.model.GetObjectRequest
import com.ibm.cos.v2.services.s3.model.ListObjectsV2Request
import org.springframework.stereotype.Service
import tools.jackson.dataformat.xml.XmlMapper
import tools.jackson.module.kotlin.readValue

@Service
class VehicleService(
    private val s3Client: S3Client,
    private val cosProperties: CosProperties,
    private val vehicleRepository: VehicleRepository,
    private val xmlMapper: XmlMapper
) {

    private companion object {
        const val IMPORT_BATCH_SIZE = 1_000
    }

    fun importVehiclesFromBucket(): Long {
        var importedCount = 0L
        val vehicleBatch = ArrayList<Vehicle>(IMPORT_BATCH_SIZE)
        var continuationToken: String? = null

        do {
            val listRequest = ListObjectsV2Request.builder()
                .bucket(cosProperties.bucket)
                .continuationToken(continuationToken)
                .build()
            val listResponse = s3Client.listObjectsV2(listRequest)

            for (bucketObject in listResponse.contents()) {
                val key = bucketObject.key()

                if (key.isNullOrBlank() || key.endsWith("/")) {
                    continue
                }

                vehicleBatch += readVehicleFromBucketObject(key)

                if (vehicleBatch.size >= IMPORT_BATCH_SIZE) {
                    vehicleRepository.insert(vehicleBatch)
                    importedCount += vehicleBatch.size
                    vehicleBatch.clear()
                }
            }

            continuationToken = listResponse.nextContinuationToken()
        } while (listResponse.isTruncated)

        if (vehicleBatch.isNotEmpty()) {
            vehicleRepository.insert(vehicleBatch)
            importedCount += vehicleBatch.size
        }

        return importedCount
    }

    private fun readVehicleFromBucketObject(key: String): Vehicle {
        val request = GetObjectRequest.builder()
            .bucket(cosProperties.bucket)
            .key(key)
            .build()

        s3Client.getObject(request).use { objectStream ->
            return xmlMapper.readValue<Vehicle>(objectStream)
        }
    }

}
