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

    // AccessCodeHistory 저장
    override fun saveAccessCodeHistory(accessCodeHistoryDto: AccessCodeHistoryDto): AccessCodeHistoryDto {
        val entity = AccessCodeHistory.fromDto(accessCodeHistoryDto)
        val savedEntity: AccessCodeHistory = accessCodeHistoryRepository.save(entity)
        return savedEntity.toDto()
    }

    //AccessCode 로 존재 유무 파악
    override fun existsByAccessCode(accessCode: String): Boolean {
        return accessCodeHistoryRepository.existsByAccessCode(accessCode)
    }

    // AccessCode 로 AccessCodeHistory 조회
    override fun findByAccessCode(accessCode: String): AccessCodeHistoryDto? {
        val entity: AccessCodeHistory? = accessCodeHistoryRepository.findByAccessCode(accessCode)
        return entity?.toDto()
    }

    // userId 와 status 로 UserAccessCode 조회
    override fun findUserAccessCodeByUserIdAndStatus(userId: String, status: AccessCodeStatus): UserAccessCodeDto? {
        val entity: UserAccessCode? = userAccessCodeRepository.findByUserIdAndStatus(userId, status)
        return entity?.toDto()
    }

    // UserAccessCode 저장
    override fun saveUserAccessCode(userAccessCodeDto: UserAccessCodeDto): UserAccessCodeDto {
        val entity = UserAccessCode.fromDto(userAccessCodeDto)
        val savedEntity: UserAccessCode = userAccessCodeRepository.save(entity)
        return savedEntity.toDto()
    }
}