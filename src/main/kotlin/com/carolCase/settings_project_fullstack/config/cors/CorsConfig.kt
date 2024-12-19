package com.carolCase.settings_project_fullstack.config.cors

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@Configuration
class CorsConfig {
    @Bean
        fun corsConfigurationSource(): CorsConfigurationSource {
            val corsConfiguration = CorsConfiguration()

            corsConfiguration.allowedOrigins = listOf("http://localhost:3000", "http://172.0.0.1:3000")
            corsConfiguration.allowedMethods = listOf("GET", "POST", "DELETE", "PUT")
            corsConfiguration.allowedHeaders = listOf("Content-Type", "Authorization", "X-Requested-With")
            corsConfiguration.allowCredentials = true

            val source = UrlBasedCorsConfigurationSource()


            source.registerCorsConfiguration("/users", corsConfiguration)
            source.registerCorsConfiguration("/login", corsConfiguration)

            return source
        }

    }
