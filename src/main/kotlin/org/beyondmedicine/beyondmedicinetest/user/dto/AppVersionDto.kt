package org.beyondmedicine.beyondmedicinetest.user.dto

data class AppVersionDto(
    val latestVersion: String,
    val minimumVersion: String,
    val hash: String
)
