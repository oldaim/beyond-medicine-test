package org.beyondmedicine.beyondmedicinetest.user.dto

import org.beyondmedicine.beyondmedicinetest.user.domain.AppVersion

data class AppVersionDto(
    val id: Long? = null,
    val latestVersion: String,
    val minimumVersion: String,
    val os: String? = null,
    val mode: String? = null,
    val hash: String
) {
    companion object {
        fun fromEntity(entity: AppVersion): AppVersionDto {
            return AppVersionDto(
                id = entity.id,
                latestVersion = entity.latestVersion,
                minimumVersion = entity.minimumVersion,
                os = entity.os,
                mode = entity.mode,
                hash = entity.hash
            )
        }
    }
    
    fun toEntity(): AppVersion {
        return AppVersion(
            id = this.id,
            latestVersion = this.latestVersion,
            minimumVersion = this.minimumVersion,
            os = this.os ?: "",
            mode = this.mode ?: "",
            hash = this.hash
        )
    }
}