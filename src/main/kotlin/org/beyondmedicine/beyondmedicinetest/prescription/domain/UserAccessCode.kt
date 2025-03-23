package org.beyondmedicine.beyondmedicinetest.prescription.domain

import jakarta.persistence.*
import org.beyondmedicine.beyondmedicinetest.prescription.domain.constant.AccessCodeStatus
import org.beyondmedicine.beyondmedicinetest.prescription.dto.UserAccessCodeDto
import java.time.LocalDateTime

@Entity
@Table(name = "user_access_code")
class UserAccessCode(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 40)
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
        
        fun fromDto(dto: UserAccessCodeDto): UserAccessCode {
            return UserAccessCode(
                id = dto.id,
                userId = dto.userId,
                accessCode = dto.accessCode,
                status = dto.status,
                activatedAt = dto.activatedAt,
                expiresAt = dto.expiresAt
            )
        }
    }
    
    fun toDto(): UserAccessCodeDto {
        return UserAccessCodeDto(
            id = this.id,
            userId = this.userId,
            accessCode = this.accessCode,
            status = this.status,
            activatedAt = this.activatedAt,
            expiresAt = this.expiresAt
        )
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