package org.beyondmedicine.beyondmedicinetest.prescription.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "처방코드 생성 요청 DTO")
data class CreateAccessCodeRequestDto(
    @field:NotBlank(message = "hospitalId cannot be blank")
    @Schema(description = "병원 아이디", example = "JDQ4MTg4MSM1MSMkMSMkMCMkNzIkNTgxOTYxIzMxIyQxIyQ3IyQ4OSQyNjEwMDIjNTEjJDEjJDIjJDgz")
    val hospitalId: String,
)