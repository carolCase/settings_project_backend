package com.carolCase.settings_project_fullstack.config.jwt

import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import javax.crypto.SecretKey
import java.util.Base64
import java.util.Date
import java.util.concurrent.TimeUnit
import org.slf4j.LoggerFactory

@Component
class JwtUtil {

    private val logger = LoggerFactory.getLogger(JwtUtil::class.java)

    // Replace this with your actual base64 encoded secret key (use a configuration or environment variable in production)
    private val base64EncodedSecretKey =
        "U2VjdXJlQXBpX1NlY3JldEtleV9mb3JfSFMyNTYwX3NlY3JldF9wcm9qZWN0X2tleV9leGFtcGxl"
    private val keyBytes: ByteArray = Base64.getDecoder().decode(base64EncodedSecretKey)

    // Generate the secret key for signing
    private val key: SecretKey = Keys.hmacShaKeyFor(keyBytes)

    // JWT expiration time (1 hour in milliseconds)
    private val jwtExpirationMs = TimeUnit.HOURS.toMillis(1).toInt()

    /**
     * Generates a JWT token for the given username and role.
     */
    fun generateJwtToken(username: String, role: String): String {
        return Jwts.builder()
            .setSubject(username) // Set the subject, usually the username
            .claim("role", role) // Custom claim for role
            .setIssuedAt(Date(System.currentTimeMillis())) // Issued time
            .setExpiration(Date(System.currentTimeMillis() + jwtExpirationMs)) // Expiration time
            .signWith(key) // Sign the token with the key
            .compact()
    }

    fun getUsernameFromJwtToken(token: String?): String {
        val claims = Jwts.parserBuilder() // Use parserBuilder() instead of parser()
            .setSigningKey(key) // Use the secret key here
            .build()
            .parseClaimsJws(token) // Parse the signed JWT
            .body

        return claims.subject // Extract the username (subject) from claims
    }



    fun getRoleFromJwtToken(token: String): String? {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body
                .get("role", String::class.java)
        } catch (e: Exception) {
            logger.error("Failed to extract role from token: ${e.message}")
            null
        }
    }

    /**
     * Validates a JWT token to check its signature and expiration.
     */
    fun validateJwtToken(token: String?): Boolean {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(key) // Verify the signature
                .build()
                .parseClaimsJws(token) // Parse and validate the JWT
            true
        } catch (e: Exception) {
            logger.error("Invalid JWT token: ${e.message}")
            false
        }
    }
}
