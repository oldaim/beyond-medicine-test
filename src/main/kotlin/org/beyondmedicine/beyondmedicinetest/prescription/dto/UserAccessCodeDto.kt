package org.beyondmedicine.beyondmedicinetest.prescription.dto

import org.beyondmedicine.beyondmedicinetest.prescription.domain.UserAccessCode
import org.beyondmedicine.beyondmedicinetest.prescription.domain.constant.AccessCodeStatus
import java.time.LocalDateTime

data class UserAccessCodeDto(
    val id: Long? = null,
    val userId: String,
    val accessCode: String,
    val status: AccessCodeStatus = AccessCodeStatus.ACTIVE,
    val activatedAt: LocalDateTime = LocalDateTime.now(),
    val expiresAt: LocalDateTime
) {
    companion object {
        private const val EXPIRE_DAYS = 43L
        
        fun activateAccessCode(userId: String, accessCode: String): UserAccessCodeDto {
            val activatedDate = LocalDateTime.now()
            // 활성화 시점으로부터 6주 후 자정에 만료
            val expirationDate = activatedDate.plusDays(EXPIRE_DAYS)
                .withHour(0)
                .withMinute(0)
                .withSecond(0)
                .withNano(0)
                
            return UserAccessCodeDto(
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
    
    fun expire(): UserAccessCodeDto {
        return this.copy(status = AccessCodeStatus.EXPIRED)
    }
}