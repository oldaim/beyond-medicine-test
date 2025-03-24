package org.beyondmedicine.beyondmedicinetest.user.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.util.*

@Schema(description = "사용자 검증 요청 DTO")
data class UserVerificationRequestDto(
    @Schema(description = "사용자 ID (UUID 형식)", example = "e4e3ecbd-2208-4905-8120-426473d0eae9")
    val userId: String,
    
    @Schema(description = "앱 버전 (Major.Minor.Patch 형식, alpha/beta 버전 포함)", example = "0.1.2-beta.1")
    val version: String,
    
    @Schema(description = "운영체제 (Android 또는 iOS)", example = "android")
    val os: String,
    
    @Schema(description = "모드 (debug 또는 release)", example = "debug")
    val mode: String,
    
    @Schema(description = "OS와 모드에 따른 해시값", example = "Y95ULTuEF0uXNq7fSNa1EEzP0FU=")
    val hash: String

){

    companion object{
        fun create(userId: String, version: String, os: String, mode: String, hash: String): UserVerificationRequestDto {

            val osLowerCase = os.lowercase(Locale.getDefault())
            val modeLowerCase = mode.lowercase(Locale.getDefault())

            return UserVerificationRequestDto(userId, version, osLowerCase, modeLowerCase, hash)
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
