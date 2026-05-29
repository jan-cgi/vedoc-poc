package com.example.xmljsonadapter.config

import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MapperConfig {

    @Bean
    fun jsonMapper() = JsonMapper()

    @Bean
    fun xmlMapper() = XmlMapper()

}
