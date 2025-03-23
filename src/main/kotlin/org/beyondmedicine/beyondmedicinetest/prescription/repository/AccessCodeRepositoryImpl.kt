package org.beyondmedicine.beyondmedicinetest.prescription.repository

import org.beyondmedicine.beyondmedicinetest.prescription.domain.AccessCodeHistory
import org.beyondmedicine.beyondmedicinetest.prescription.domain.UserAccessCode
import org.beyondmedicine.beyondmedicinetest.prescription.domain.constant.AccessCodeStatus
import org.beyondmedicine.beyondmedicinetest.prescription.dto.AccessCodeHistoryDto
import org.beyondmedicine.beyondmedicinetest.prescription.dto.UserAccessCodeDto
import org.springframework.stereotype.Repository

@Repository
class AccessCodeRepositoryImpl(
    private val accessCodeHistoryRepository: AccessCodeHistoryRepository,
    private val userAccessCodeRepository: UserAccessCodeRepository
) : AccessCodeRepository {
    
    override fun saveAccessCodeHistory(accessCodeHistoryDto: AccessCodeHistoryDto): AccessCodeHistoryDto {
        val entity = AccessCodeHistory.fromDto(accessCodeHistoryDto)
        val savedEntity = accessCodeHistoryRepository.save(entity)
        return savedEntity.toDto()
    }
    
    override fun existsByAccessCode(accessCode: String): Boolean {
        return accessCodeHistoryRepository.existsByAccessCode(accessCode)
    }
    
    override fun findByAccessCode(accessCode: String): AccessCodeHistoryDto? {
        val entity = accessCodeHistoryRepository.findByAccessCode(accessCode)
        return entity?.toDto()
    }
    
    override fun findUserAccessCodeByUserIdAndStatus(userId: String, status: AccessCodeStatus): UserAccessCodeDto? {
        val entity = userAccessCodeRepository.findByUserIdAndStatus(userId, status)
        return entity?.toDto()
    }
    
    override fun saveUserAccessCode(userAccessCodeDto: UserAccessCodeDto): UserAccessCodeDto {
        val entity = UserAccessCode.fromDto(userAccessCodeDto)
        val savedEntity = userAccessCodeRepository.save(entity)
        return savedEntity.toDto()
    }
}