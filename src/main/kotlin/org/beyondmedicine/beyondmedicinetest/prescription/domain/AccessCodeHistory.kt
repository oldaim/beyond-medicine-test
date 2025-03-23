package org.beyondmedicine.beyondmedicinetest.prescription.domain

import jakarta.persistence.*
import org.beyondmedicine.beyondmedicinetest.prescription.dto.AccessCodeHistoryDto
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "access_code_history")
class AccessCodeHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val hospitalId: String,

    @Column(nullable = false, length = 8, unique = true)
    val accessCode: String,

    @CreationTimestamp
    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
) {

    companion object {
        
        fun fromDto(dto: AccessCodeHistoryDto): AccessCodeHistory {
            return AccessCodeHistory(
                id = dto.id,
                hospitalId = dto.hospitalId,
                accessCode = dto.accessCode,
                createdAt = dto.createdAt
            )
        }
    }
    
    fun toDto(): AccessCodeHistoryDto {
        return AccessCodeHistoryDto(
            id = this.id,
            hospitalId = this.hospitalId,
            accessCode = this.accessCode,
            createdAt = this.createdAt
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AccessCodeHistory

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}