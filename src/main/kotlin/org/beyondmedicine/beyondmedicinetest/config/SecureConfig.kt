package org.beyondmedicine.beyondmedicinetest.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.security.SecureRandom

@Configuration
class SecureConfig {

    @Bean
    fun secureRandom(): SecureRandom {
        return SecureRandom()
    }

}