package org.beyondmedicine.beyondmedicinetest.dto

import jakarta.validation.constraints.NotBlank
import org.beyondmedicine.beyondmedicinetest.domain.constant.AccessCodeStatus
import java.time.LocalDateTime

// 처방코드 생성 요청 DTO
data class AccessCodeRequestDto(
    @field:NotBlank(message = "hospitalId cannot be blank")
    val hospitalId: String,
)

// 처방코드 생성 응답 DTO
data class AccessCodeResponseDto(
    val accessCode: String,
    val createdAt: LocalDateTime,
)

// 내부 Service 전달용 처방코드 정보 DTO
data class AccessCodeInfoDto(
    val id: Long,
    val hospitalId: String,
    val accessCode: String,
    val createdAt: LocalDateTime,
    val expiredAt: LocalDateTime,
    val status: AccessCodeStatus
)