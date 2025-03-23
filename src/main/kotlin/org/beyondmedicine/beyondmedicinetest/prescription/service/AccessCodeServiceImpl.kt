package org.beyondmedicine.beyondmedicinetest.prescription.service

import org.beyondmedicine.beyondmedicinetest.common.exception.AccessCodeAlreadyActivatedException
import org.beyondmedicine.beyondmedicinetest.common.exception.AccessCodeRetryFailException
import org.beyondmedicine.beyondmedicinetest.prescription.domain.constant.AccessCodeStatus
import org.beyondmedicine.beyondmedicinetest.prescription.dto.*
import org.beyondmedicine.beyondmedicinetest.prescription.repository.AccessCodeRepository
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom
import kotlin.random.Random

@Service
class AccessCodeServiceImpl(
    private val accessCodeRepository: AccessCodeRepository,
    private val random: SecureRandom
) : AccessCodeService {

    companion object{
        private val logger = LoggerFactory.getLogger(AccessCodeServiceImpl::class.java)
    }

    // 처방코드 생성 로직
    @Transactional
    override fun createAccessCodeHistory(requestDto: CreateAccessCodeRequestDto): CreateAccessCodeResponseDto {
        val hospitalId: String = requestDto.hospitalId
        var attempt = 0
        val maxAttempt = 10

        while (attempt < maxAttempt) {
            try {
                // 처방코드 생성
                val accessCode: String = generateRandomAccessCode()

                // 중복 검사
                if (accessCodeRepository.existsByAccessCode(accessCode)) {
                    attempt++
                    continue
                }

                val accessCodeHistoryDto: AccessCodeHistoryDto = AccessCodeHistoryDto.create(
                    hospitalId = hospitalId,
                    accessCode = accessCode
                )

                val result: AccessCodeHistoryDto = accessCodeRepository.saveAccessCodeHistory(accessCodeHistoryDto)

                return CreateAccessCodeResponseDto(
                    accessCode = result.accessCode,
                    createdAt = result.createdAt
                )
            } catch (e: DataIntegrityViolationException) {
                // DB 수준에서 중복키 예외가 발생한 경우
                attempt++

                if (attempt >= maxAttempt) {
                    logger.error("Failed to generate new access code after $maxAttempt attempts")
                    throw AccessCodeRetryFailException("Failed to generate new access code after $maxAttempt attempts")
                }

                // 로그 남기기
                logger.warn("Access code collision detected, retrying (attempt $attempt of $maxAttempt)")
            }
        }

        logger.error("Failed to generate new access code after $maxAttempt attempts")
        throw AccessCodeRetryFailException("Failed to generate new access code after $maxAttempt attempts")
    }

    @Transactional
    override fun activateAccessCode(requestDto: ActivateAccessCodeRequestDto): ActivateAccessCodeResponseDto {
        // Validation 과정 요약
        // 1. accessCode 가 존재하는지 확인
        // 2. userId 가 존재하는지 확인
        //    - 존재하지 않는경우 userAccessCode 생성
        //    - 존재 하는 경우 active 상태의 userAccessCode 가 있는지 확인
        //       - 있을 경우 만료 상태 확인
        //       - 만료 상태일 경우 EXPIRED 상태로 UPDATE 후 userAccessCode 생성
        //       - 만료 상태가 아닐 경우 이미 active 상태임으로 에러 처리
        //    - 없을 경우 userAccessCode 생성
        
        val userId: String = requestDto.userId

        val accessCode: String = requestDto.accessCode
        
        validateAccessCode(accessCode)

        val existingUserAccessCodeDto: UserAccessCodeDto? = getExistingUserAccessCode(userId)
        
        if (existingUserAccessCodeDto != null) {
            if (existingUserAccessCodeDto.isExpired()) {

                val expiredDto: UserAccessCodeDto = existingUserAccessCodeDto.expire()

                accessCodeRepository.saveUserAccessCode(expiredDto)

                return createAndSaveUserAccessCode(userId, accessCode)

            } else {
                logger.error("User $userId already has active access code")
                throw AccessCodeAlreadyActivatedException("User already has active access code")
            }
        } else {
            return createAndSaveUserAccessCode(userId, accessCode)
        }
    }

    @Transactional(readOnly = true)
    override fun isUserAccessCodeActivated(userId: String): Boolean {
        val existingUserAccessCodeDto: UserAccessCodeDto? = getExistingUserAccessCode(userId)
        return existingUserAccessCodeDto != null
    }

    private fun getExistingUserAccessCode(userId: String): UserAccessCodeDto? {
        return accessCodeRepository.findUserAccessCodeByUserIdAndStatus(userId, AccessCodeStatus.ACTIVE)
    }

    private fun createAndSaveUserAccessCode(userId: String, accessCode: String): ActivateAccessCodeResponseDto  {

        val userAccessCodeDto: UserAccessCodeDto = UserAccessCodeDto.activateAccessCode(userId, accessCode)

        val savedUserAccessCodeDto: UserAccessCodeDto = accessCodeRepository.saveUserAccessCode(userAccessCodeDto)

        return ActivateAccessCodeResponseDto.create(
                    userId = savedUserAccessCodeDto.userId,
                    accessCode = savedUserAccessCodeDto.accessCode,
                    createdAt = savedUserAccessCodeDto.activatedAt,
                    expiredAt = savedUserAccessCodeDto.expiresAt
                )
    }

    private fun validateAccessCode(accessCode: String): AccessCodeHistoryDto {
        return accessCodeRepository.findByAccessCode(accessCode)
            ?: throw NoSuchElementException("Access code not found")
    }

    private fun generateRandomAccessCode(): String {
        // 난수 생성 요소
        val timeNanos: Long = System.nanoTime()
        val randomLong: Long = random.nextLong()
        // 난수 생성
        val randomSeed: Long = timeNanos xor randomLong

        val random = Random(randomSeed)

        // 4자리 문자열 생성
        val letters: String = (1..4).map { ('A' .. 'Z').random(random) }.joinToString { "" }
        // 4자리 숫자열 생성
        val numbers: String = (1..4).map { ('0' .. '9').random(random) }.joinToString { "" }

        // 조합 후 섞기
        val combined: CharArray = ("$letters$numbers").toCharArray()
        combined.shuffle(random)

        return String(combined)
    }
}