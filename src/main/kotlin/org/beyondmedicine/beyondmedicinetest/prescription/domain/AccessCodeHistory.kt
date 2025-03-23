package org.beyondmedicine.beyondmedicinetest.prescription.domain

import jakarta.persistence.*
import org.beyondmedicine.beyondmedicinetest.prescription.dto.AccessCodeHistoryDto
import org.beyondmedicine.beyondmedicinetest.prescription.dto.CreateAccessCodeResponseDto
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
        // 새로운 처방코드 생성 팩터리 메서드
        fun createNewAccessCodeHistory(hospitalId: String, accessCode: String): AccessCodeHistory {
            return AccessCodeHistory(
                hospitalId = hospitalId,
                accessCode = accessCode
            )
        }
        
        fun fromDto(dto: AccessCodeHistoryDto): AccessCodeHistory {
            return AccessCodeHistory(
                id = dto.id,
                hospitalId = dto.hospitalId,
                accessCode = dto.accessCode,
                createdAt = dto.createdAt
            )
        }

        fun toResponseDto(entity: AccessCodeHistory): CreateAccessCodeResponseDto {
            return CreateAccessCodeResponseDto.fromEntity(entity)
        }

        fun toInfoDto(entity: AccessCodeHistory): AccessCodeHistoryDto {
            return AccessCodeHistoryDto.fromEntity(entity)
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
    
    fun toResponseDto(): CreateAccessCodeResponseDto {
        return CreateAccessCodeResponseDto(
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