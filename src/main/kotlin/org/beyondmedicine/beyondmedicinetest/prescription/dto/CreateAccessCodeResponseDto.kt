package org.beyondmedicine.beyondmedicinetest.prescription.dto

import org.beyondmedicine.beyondmedicinetest.prescription.domain.AccessCodeHistory
import java.time.LocalDateTime

// 처방코드 생성 응답 DTO
data class CreateAccessCodeResponseDto(
    val accessCode: String,
    val createdAt: LocalDateTime,
) {
    companion object {
        fun fromEntity(entity: AccessCodeHistory): CreateAccessCodeResponseDto {
            return CreateAccessCodeResponseDto(
                accessCode = entity.accessCode,
                createdAt = entity.createdAt
            )
        }
    }
}