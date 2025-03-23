package org.beyondmedicine.beyondmedicinetest.prescription.dto

import org.beyondmedicine.beyondmedicinetest.prescription.domain.AccessCodeHistory
import java.time.LocalDateTime

data class AccessCodeHistoryDto(
    val id: Long? = null,
    val hospitalId: String,
    val accessCode: String,
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    companion object {
        fun fromEntity(entity: AccessCodeHistory): AccessCodeHistoryDto {
            return AccessCodeHistoryDto(
                id = entity.id,
                hospitalId = entity.hospitalId,
                accessCode = entity.accessCode,
                createdAt = entity.createdAt
            )
        }
        
        fun create(hospitalId: String, accessCode: String): AccessCodeHistoryDto {
            return AccessCodeHistoryDto(
                hospitalId = hospitalId,
                accessCode = accessCode
            )
        }
    }
    
    fun toEntity(): AccessCodeHistory {
        return AccessCodeHistory(
            id = this.id,
            hospitalId = this.hospitalId,
            accessCode = this.accessCode,
            createdAt = this.createdAt
        )
    }
}