package org.beyondmedicine.beyondmedicinetest.prescription.dto

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.Length

data class ActivateAccessCodeRequestDto(

    @field:NotBlank(message = "userId cannot be blank")
    val userId: String,

    @field:Length(min = 8, max = 8, message = "accessCode length must be 8")
    val accessCode: String

)


