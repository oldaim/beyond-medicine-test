package org.beyondmedicine.beyondmedicinetest.prescription.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "처방코드 생성 응답 DTO")
data class CreateAccessCodeResponseDto(
    @Schema(description = "생성된 처방코드", example = "Y6U8Y8U8")
    val accessCode: String,
    
    @Schema(description = "처방코드 생성 시간", example = "2025-03-23T15:30:45.123")
    val createdAt: LocalDateTime,
) {
    companion object {

        fun create(accessCode: String, createdAt: LocalDateTime): CreateAccessCodeResponseDto {
            return CreateAccessCodeResponseDto(
                accessCode = accessCode,
                createdAt = createdAt
            )
        }
    }
}