package org.beyondmedicine.beyondmedicinetest.prescription.dto

import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length
import org.hibernate.validator.constraints.UUID
import java.time.LocalDateTime

data class ActivateAccessCodeRequestDto(
    @field:Pattern(
        regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
        message = "유효한 UUID 형식이 아닙니다"
    )
    val userId: String,

    @field:Length(min = 8, max = 8, message = "accessCode length must be 8")
    val accessCode: String
)

data class ActivateAccessCodeResponseDto(
    val userId: String,
    val accessCode: String,
    val createdAt: LocalDateTime,
    val expiresAt: LocalDateTime
){
    companion object {
        fun create(userId: String, accessCode: String, createdAt: LocalDateTime, expiredAt: LocalDateTime): ActivateAccessCodeResponseDto {

            return ActivateAccessCodeResponseDto(
                userId = userId,
                accessCode = accessCode,
                createdAt = createdAt,
                expiresAt = expiredAt
            )
        }
    }
}


