package org.beyondmedicine.beyondmedicinetest.user.domain

import jakarta.persistence.*
import org.beyondmedicine.beyondmedicinetest.prescription.domain.AccessCodeHistory
import org.hibernate.annotations.CreationTimestamp
import java.time.LocalDateTime

@Entity
@Table(name = "user_verification_log")
class UserVerificationLog (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, length = 36)
    val userId: String,

    @Column(nullable = false, length = 30)
    val version: String,

    @Column(nullable = false, length = 20)
    val os: String,

    @Column(nullable = false, length = 10)
    val mode: String,

    @Column(nullable = false, length = 100)
    val hash: String,

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    val requestedAt: LocalDateTime = LocalDateTime.now()

){
    companion object{

        fun createUserLog(userId: String, version: String, os: String, mode: String, hash: String): UserVerificationLog{
            return UserVerificationLog(
                userId = userId,
                version = version,
                os = os,
                mode = mode,
                hash = hash
            )
        }

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