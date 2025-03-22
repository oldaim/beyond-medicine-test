package org.beyondmedicine.beyondmedicinetest.domain.accesscode

import jakarta.persistence.*
import org.beyondmedicine.beyondmedicinetest.dto.AccessCodeHistoryDto
import org.beyondmedicine.beyondmedicinetest.dto.CreateAccessCodeResponseDto
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

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
) {

    companion object {
        // 새로운 처방코드 생성 팩터리 메서드
        fun createNewAccessCodeHistory(hospitalId: String, accessCode: String): AccessCodeHistory {

            val createdDate = LocalDateTime.now()

            return AccessCodeHistory(
                hospitalId = hospitalId,
                accessCode = accessCode,
                createdAt = createdDate
            )
        }

        fun toResponseDto(entity: AccessCodeHistory): CreateAccessCodeResponseDto {
            return CreateAccessCodeResponseDto(
                accessCode = entity.accessCode,
                createdAt = entity.createdAt
            )
        }

        fun toInfoDto(entity: AccessCodeHistory): AccessCodeHistoryDto {
            return AccessCodeHistoryDto(
                id = entity.id?: -1,
                hospitalId = entity.hospitalId,
                accessCode = entity.accessCode,
                createdAt = entity.createdAt
            )
        }
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
