package org.beyondmedicine.beyondmedicinetest.user.domain

import jakarta.persistence.*
import org.beyondmedicine.beyondmedicinetest.user.dto.UserVerificationLogDto
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "user_verification_log")
class UserVerificationLog (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 40)
    val userId: String,

    @Column(nullable = false, length = 30)
    val version: String,

    @Column(nullable = false, length = 30)
    val os: String,

    @Column(nullable = false, length = 30)
    val mode: String,

    @Column(nullable = false, length = 100)
    val hash: String,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val requestedAt: LocalDateTime = LocalDateTime.now()

){
    companion object{
        
        fun fromDto(dto: UserVerificationLogDto): UserVerificationLog {
            return UserVerificationLog(
                id = dto.id,
                userId = dto.userId,
                version = dto.version,
                os = dto.os,
                mode = dto.mode,
                hash = dto.hash,
                requestedAt = dto.requestedAt
            )
        }
    }
    
    fun toDto(): UserVerificationLogDto {
        return UserVerificationLogDto(
            id = this.id,
            userId = this.userId,
            version = this.version,
            os = this.os,
            mode = this.mode,
            hash = this.hash,
            requestedAt = this.requestedAt
        )
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserVerificationLog

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}