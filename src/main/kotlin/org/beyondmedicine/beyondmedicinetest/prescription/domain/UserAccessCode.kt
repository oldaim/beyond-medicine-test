package org.beyondmedicine.beyondmedicinetest.prescription.domain

import jakarta.persistence.*
import org.beyondmedicine.beyondmedicinetest.prescription.domain.constant.AccessCodeStatus
import java.time.LocalDateTime

@Entity
@Table(name = "user_access_code")
class UserAccessCode(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val userId: String,

    @Column(nullable = false, length = 8)
    val accessCode: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: AccessCodeStatus = AccessCodeStatus.ACTIVE,

    @Column(nullable = false)
    val activatedAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    val expiresAt: LocalDateTime
) {

    companion object {

        private const val EXPIRE_DAYS = 43L

        // 사용자 처방코드 활성화 팩터리 메서드
        fun activateAccessCode(userId: String, accessCode: String): UserAccessCode {
            val activatedDate = LocalDateTime.now()
            // 활성화 시점으로부터 6주 후 자정에 만료
            val expirationDate = activatedDate.plusDays(EXPIRE_DAYS)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)

            return UserAccessCode(
                userId = userId,
                accessCode = accessCode,
                status = AccessCodeStatus.ACTIVE,
                activatedAt = activatedDate,
                expiresAt = expirationDate
            )
        }
    }

    fun isExpired(): Boolean {
        return LocalDateTime.now().isAfter(expiresAt) || status == AccessCodeStatus.EXPIRED
    }

    fun expire() {
        this.status = AccessCodeStatus.EXPIRED
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserAccessCode

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}