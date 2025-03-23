package org.beyondmedicine.beyondmedicinetest.user.dto

import org.beyondmedicine.beyondmedicinetest.user.domain.UserVerificationLog
import java.time.LocalDateTime

data class UserVerificationLogDto(
    val id: Long? = null,
    val userId: String,
    val version: String,
    val os: String,
    val mode: String,
    val hash: String,
    val requestedAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun fromEntity(entity: UserVerificationLog): UserVerificationLogDto {
            return UserVerificationLogDto(
                id = entity.id,
                userId = entity.userId,
                version = entity.version,
                os = entity.os,
                mode = entity.mode,
                hash = entity.hash,
                requestedAt = entity.requestedAt
            )
        }
        
        fun create(userId: String, version: String, os: String, mode: String, hash: String): UserVerificationLogDto {
            return UserVerificationLogDto(
                userId = userId,
                version = version,
                os = os,
                mode = mode,
                hash = hash
            )
        }
    }
    
    fun toEntity(): UserVerificationLog {
        return UserVerificationLog(
            id = this.id,
            userId = this.userId,
            version = this.version,
            os = this.os,
            mode = this.mode,
            hash = this.hash,
            requestedAt = this.requestedAt
        )
    }
}