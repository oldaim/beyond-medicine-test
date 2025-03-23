package org.beyondmedicine.beyondmedicinetest.prescription.service


import org.beyondmedicine.beyondmedicinetest.prescription.domain.AccessCodeHistory
import org.beyondmedicine.beyondmedicinetest.prescription.domain.UserAccessCode
import org.beyondmedicine.beyondmedicinetest.prescription.domain.constant.AccessCodeStatus
import org.beyondmedicine.beyondmedicinetest.prescription.dto.ActivateAccessCodeRequestDto
import org.beyondmedicine.beyondmedicinetest.prescription.dto.CreateAccessCodeRequestDto
import org.beyondmedicine.beyondmedicinetest.prescription.dto.CreateAccessCodeResponseDto
import org.beyondmedicine.beyondmedicinetest.prescription.repository.AccessCodeHistoryRepository
import org.beyondmedicine.beyondmedicinetest.prescription.repository.UserAccessCodeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom
import kotlin.random.Random

@Service
class AccessCodeServiceImpl(
    private val accessCodeRepository: AccessCodeHistoryRepository,
    private val userAccessCodeRepository: UserAccessCodeRepository,
    private val random: SecureRandom
) : AccessCodeService {

    // 처방코드 생성 로직
    @Transactional
    override fun createAccessCodeHistory(requestDto: CreateAccessCodeRequestDto): CreateAccessCodeResponseDto {

        val hospitalId: String = requestDto.hospitalId
        // 처방코드 생성
        val accessCode: String = generateNewAccessCode()

        val accessCodeHistory: AccessCodeHistory = AccessCodeHistory.createNewAccessCodeHistory(hospitalId, accessCode)

        val result: AccessCodeHistory = accessCodeRepository.save(accessCodeHistory)

        return AccessCodeHistory.toResponseDto(result)

    }

    @Transactional
    override fun activateAccessCode(requestDto: ActivateAccessCodeRequestDto) {

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

        val existingUserAccessCode: UserAccessCode? = getExistingUserAccessCode(userId)
        
        if (existingUserAccessCode != null) {

            if (existingUserAccessCode.isExpired()) {

                existingUserAccessCode.expire()

                userAccessCodeRepository.save(existingUserAccessCode)

                createAndSaveUserAccessCode(userId, accessCode)

            } else throw IllegalStateException("User already has active access code")

        }else{
            createAndSaveUserAccessCode(userId, accessCode)
        }

    }

    @Transactional
    override fun isUserAccessCodeActivated(userId: String): Boolean {

        val existingUserAccessCode: UserAccessCode? = getExistingUserAccessCode(userId)

        return existingUserAccessCode != null
    }

    private fun getExistingUserAccessCode(userId: String) =
        userAccessCodeRepository.findByUserIdAndStatus(userId, AccessCodeStatus.ACTIVE)

    private fun createAndSaveUserAccessCode(userId: String, accessCode: String) {

        val userAccessCode: UserAccessCode = UserAccessCode.activateAccessCode(userId, accessCode)

        userAccessCodeRepository.save(userAccessCode)
    }

    private fun validateAccessCode(accessCode: String) = accessCodeRepository.findByAccessCode(accessCode)
        ?: throw IllegalArgumentException("Access code not found")

    private fun generateNewAccessCode(): String {

        var attempt = 0
        val maxAttempt = 5

        while (attempt < maxAttempt) {

            val accessCode: String = generateRandomAccessCode()

            if (!accessCodeRepository.existsByAccessCode(accessCode)) {
                return accessCode
            }

            attempt++
        }

        throw RuntimeException("Failed to generate new access code")
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