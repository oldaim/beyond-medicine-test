package org.beyondmedicine.beyondmedicinetest.dto

data class ActivateAccessCodeRequestDto(
    val userId: String,
    val accessCode: String,
)


