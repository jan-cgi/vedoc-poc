package com.example.vedoc.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.MongoTransactionManager

@Configuration
class MongoTransactionConfig {

    @Bean
    fun mongoTransactionManager(dbFactory: MongoDatabaseFactory): MongoTransactionManager {
        return MongoTransactionManager(dbFactory)
    }

}
