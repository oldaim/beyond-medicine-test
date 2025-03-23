package org.beyondmedicine.beyondmedicinetest.user.repository

import org.beyondmedicine.beyondmedicinetest.user.domain.UserVerificationLog
import org.beyondmedicine.beyondmedicinetest.user.dto.AppVersionDto
import org.beyondmedicine.beyondmedicinetest.user.dto.UserVerificationLogDto
import org.springframework.stereotype.Repository

@Repository
class UserVerificationRepositoryImpl(
    private val appVersionRepository: AppVersionRepository,
    private val userVerificationLogRepository: UserVerificationLogRepository
) : UserVerificationRepository {
    
    override fun findAppVersionByOsAndMode(os: String, mode: String): AppVersionDto? {
        val appVersion = appVersionRepository.findAppVersionByOsAndMode(os, mode) ?: return null
        return appVersion.toDto()
    }
    
    override fun saveUserVerificationLog(userVerificationLogDto: UserVerificationLogDto): UserVerificationLogDto {
        val entity = UserVerificationLog.fromDto(userVerificationLogDto)
        val savedEntity = userVerificationLogRepository.save(entity)
        return savedEntity.toDto()
    }
}