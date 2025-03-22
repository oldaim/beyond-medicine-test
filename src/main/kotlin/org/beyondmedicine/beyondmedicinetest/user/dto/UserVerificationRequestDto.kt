package org.beyondmedicine.beyondmedicinetest.user.dto

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.UUID
import java.util.*

data class UserVerificationRequestDto(

    @UUID
    val userId: String,

    @field:NotBlank(message = "version cannot be blank")
    val version: String,

    @field:NotBlank(message = "os cannot be blank")
    val os: String,

    @field:NotBlank(message = "mode cannot be blank")
    val mode: String,

    @field:NotBlank(message = "hash cannot be blank")
    val hash: String

){

    fun isDtoValid(): Boolean {

        val version: String = this.version
        val os: String = this.os
        val mode: String = this.mode

        return version.isValidVersionFormat() && os.isValidOsFormat() && mode.isValidModeFormat()
    }

    private fun String.isValidVersionFormat(): Boolean {

        val pattern = """^\d+\.\d+\.\d+(-alpha\.\d+|-beta\.\d+)?$"""

        return this.matches(Regex(pattern))
    }

    private fun String.isValidOsFormat(): Boolean {
        val lowerCase = this.lowercase(Locale.getDefault())

        return lowerCase == "android" || lowerCase == "ios"
    }

    private fun String.isValidModeFormat(): Boolean {
        val lowerCase = this.lowercase(Locale.getDefault())

        return lowerCase == "debug" || lowerCase == "release"
    }
}
