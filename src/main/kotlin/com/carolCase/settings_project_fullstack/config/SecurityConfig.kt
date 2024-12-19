package com.carolCase.settings_project_fullstack.config

import com.carolCase.settings_project_fullstack.config.jwt.JwtAuthenticationFilter
import com.carolCase.settings_project_fullstack.model.CustomUserDetails
import com.carolCase.settings_project_fullstack.model.CustomUserDetailsService
import com.carolCase.settings_project_fullstack.model.authority.UserPermission
import com.carolCase.settings_project_fullstack.model.authority.UserRole
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder

import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
class SecurityConfig @Autowired constructor(
    val jwtAuthenticationFilter: JwtAuthenticationFilter,
    val passwordEncoder: PasswordEncoder,
    val customUserDetailsService: CustomUserDetailsService

) {


    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() } // Disables CSRF (Cross-Site Request Forgery) protection
            .cors { it.configurationSource { request ->
                // CORS configuration specifically for Spring Security
                org.springframework.web.cors.CorsConfiguration().apply {
                    allowedOrigins = listOf("http://localhost:3000") // Frontend origin
                    allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH")
                    allowedHeaders = listOf("*")
                    allowCredentials = (true)
                }
            }}
        http
            .authorizeHttpRequests { it
              //  .anyRequest().permitAll()
                .requestMatchers("/", "/login", "/logout", "/who-am-i").permitAll()
                .requestMatchers("/users/admin").hasAuthority(UserRole.ADMIN.name)
                .requestMatchers("/users/user").hasRole(UserRole.USER.name)
                .requestMatchers("/users/read").hasAnyAuthority(UserPermission.READ.getContent())
                .anyRequest().authenticated() // Require authentication for all other requests
            }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) } // Stateless session
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java) // Add JWT filter before UsernamePasswordAuthenticationFilter

        return http.build()
    }



    @Bean
    @Throws(Exception::class)
    fun authenticationManager(http: HttpSecurity): AuthenticationManager {
        val daoAuthenticationProvider = DaoAuthenticationProvider()
        daoAuthenticationProvider.setUserDetailsService(customUserDetailsService)
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder)

        val authenticationManagerBuilder = http.getSharedObject(
            AuthenticationManagerBuilder::class.java
        )
        authenticationManagerBuilder.authenticationProvider(daoAuthenticationProvider)

        return authenticationManagerBuilder.build()
    }
}


