package com.carolCase.settings_project_fullstack.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

@Configuration
class PasswordConfig {

    @Bean
    fun newPasswordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder(10)
    }

}