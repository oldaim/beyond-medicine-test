package org.beyondmedicine.beyondmedicinetest.prescription.dto

import jakarta.validation.constraints.NotBlank

// 처방코드 생성 요청 DTO
data class CreateAccessCodeRequestDto(
    @field:NotBlank(message = "hospitalId cannot be blank")
    val hospitalId: String,
)