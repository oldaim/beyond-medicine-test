package org.beyondmedicine.beyondmedicinetest.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.security.MessageDigest
import java.security.SecureRandom

@Configuration
class SecureConfig {

    companion object{
        const val HASH_ALGORITHM = "SHA-256"
    }

    @Bean
    fun secureRandom(): SecureRandom {
        return SecureRandom()
    }

    @Bean
    fun messageDigest(): MessageDigest {
        return MessageDigest.getInstance(HASH_ALGORITHM)
    }
}