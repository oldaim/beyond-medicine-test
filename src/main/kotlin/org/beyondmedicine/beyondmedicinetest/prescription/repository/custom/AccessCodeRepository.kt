package org.beyondmedicine.beyondmedicinetest.prescription.repository.custom

import org.beyondmedicine.beyondmedicinetest.prescription.domain.constant.AccessCodeStatus
import org.beyondmedicine.beyondmedicinetest.prescription.dto.AccessCodeHistoryDto
import org.beyondmedicine.beyondmedicinetest.prescription.dto.UserAccessCodeDto

interface AccessCodeRepository {
    
    fun saveAccessCodeHistory(accessCodeHistoryDto: AccessCodeHistoryDto): AccessCodeHistoryDto
    
    fun existsByAccessCode(accessCode: String): Boolean
    
    fun findByAccessCode(accessCode: String): AccessCodeHistoryDto?
    
    fun findUserAccessCodeByUserIdAndStatus(userId: String, status: AccessCodeStatus): UserAccessCodeDto?
    
    fun saveUserAccessCode(userAccessCodeDto: UserAccessCodeDto): UserAccessCodeDto
    
}