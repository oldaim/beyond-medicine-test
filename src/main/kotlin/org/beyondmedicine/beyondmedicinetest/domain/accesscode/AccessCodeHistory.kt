package org.beyondmedicine.beyondmedicinetest.domain.accesscode

import jakarta.persistence.*
import org.beyondmedicine.beyondmedicinetest.domain.constant.AccessCodeStatus
import org.beyondmedicine.beyondmedicinetest.dto.AccessCodeInfoDto
import org.beyondmedicine.beyondmedicinetest.dto.AccessCodeResponseDto
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
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val expiredAt: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: AccessCodeStatus = AccessCodeStatus.ACTIVE
) {

    companion object {
        // 새로운 처방코드 생성 팩터리 메서드
        fun createNewAccessCodeHistory(hospitalId: String, accessCode: String): AccessCodeHistory {

            val createdDate = LocalDateTime.now()

            val expirationDate = createdDate
                .plusDays(43)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)

            return AccessCodeHistory(
                hospitalId = hospitalId,
                accessCode = accessCode,
                createdAt = createdDate,
                expiredAt = expirationDate,
                status = AccessCodeStatus.ACTIVE
            )
        }

        fun toResponseDto(entity: AccessCodeHistory): AccessCodeResponseDto {
            return AccessCodeResponseDto(
                accessCode = entity.accessCode,
                createdAt = entity.createdAt
            )
        }

        fun toInfoDto(entity: AccessCodeHistory): AccessCodeInfoDto {
            return AccessCodeInfoDto(
                id = entity.id?: -1,
                hospitalId = entity.hospitalId,
                accessCode = entity.accessCode,
                createdAt = entity.createdAt,
                expiredAt = entity.expiredAt,
                status = entity.status
            )
        }
    }


    fun expire() { // 처방코드 이력의 상태 변경 메서드
        this.status = AccessCodeStatus.EXPIRED
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
