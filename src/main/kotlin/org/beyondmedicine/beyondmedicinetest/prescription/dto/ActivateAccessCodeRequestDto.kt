package org.beyondmedicine.beyondmedicinetest.prescription.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Pattern
import org.hibernate.validator.constraints.Length
import java.time.LocalDateTime

@Schema(description = "처방코드 활성화 요청 DTO")
data class ActivateAccessCodeRequestDto(
    @field:Pattern(
        regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
        message = "유효한 UUID 형식이 아닙니다"
    )
    @Schema(description = "사용자 ID (UUID 형식)", example = "e4e3ecbd-2208-4905-8120-426473d0eae9")
    val userId: String,

    @field:Length(min = 8, max = 8, message = "accessCode length must be 8")
    @Schema(description = "처방코드 (8자리)", example = "Y1Z2R3T4")
    val accessCode: String
)

@Schema(description = "처방코드 활성화 응답 DTO")
data class ActivateAccessCodeResponseDto(
    @Schema(description = "사용자 ID", example = "e4e3ecbd-2208-4905-8120-426473d0eae9")
    val userId: String,
    
    @Schema(description = "활성화된 처방코드", example = "ZXCV1357")
    val accessCode: String,
    
    @Schema(description = "처방코드 활성화 시간", example = "2025-03-23T15:30:45.123")
    val activatedAt: LocalDateTime,
    
    @Schema(description = "처방코드 만료 시간 (6주 후 자정)", example = "2025-05-04T23:59:59.999")
    val expiresAt: LocalDateTime
){
    companion object {
        fun create(userId: String, accessCode: String, createdAt: LocalDateTime, expiredAt: LocalDateTime): ActivateAccessCodeResponseDto {

            return ActivateAccessCodeResponseDto(
                userId = userId,
                accessCode = accessCode,
                activatedAt = createdAt,
                expiresAt = expiredAt
            )
        }
    }
}


