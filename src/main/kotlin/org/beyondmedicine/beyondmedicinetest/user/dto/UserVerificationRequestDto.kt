package org.beyondmedicine.beyondmedicinetest.user.dto

import java.util.*

data class UserVerificationRequestDto(
    val userId: String,
    val version: String,
    val os: String,
    val mode: String,
    val hash: String

){

    companion object{
        fun create(userId: String, version: String, os: String, mode: String, hash: String): UserVerificationRequestDto {
            return UserVerificationRequestDto(userId, version, os, mode, hash)
        }
    }

    fun isDtoValid(): Boolean {

        val version: String = this.version
        val os: String = this.os
        val mode: String = this.mode

        return version.isValidVersionFormat() && os.isValidOsFormat() && mode.isValidModeFormat() && userId.isValidUserIdFormat()
    }

    private fun String.isValidUserIdFormat(): Boolean {

        val pattern = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$"

        return this.matches(Regex(pattern))
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
