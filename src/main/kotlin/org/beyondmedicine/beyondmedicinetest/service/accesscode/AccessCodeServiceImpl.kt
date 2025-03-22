package org.beyondmedicine.beyondmedicinetest.service.accesscode


import org.beyondmedicine.beyondmedicinetest.domain.accesscode.AccessCodeHistory
import org.beyondmedicine.beyondmedicinetest.dto.AccessCodeHistoryDto
import org.beyondmedicine.beyondmedicinetest.dto.AccessCodeRequestDto
import org.beyondmedicine.beyondmedicinetest.dto.AccessCodeResponseDto
import org.beyondmedicine.beyondmedicinetest.repository.accesscode.AccessCodeHistoryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom
import kotlin.random.Random

@Service
class AccessCodeServiceImpl(
    private val accessCodeRepository: AccessCodeHistoryRepository,
    private val random: SecureRandom
) : AccessCodeService {

    // 처방코드 생성 로직
    @Transactional
    override fun createAccessCodeHistory(requestDto: AccessCodeRequestDto): AccessCodeResponseDto {

        val hospitalId: String = requestDto.hospitalId
        // 처방코드 생성
        val accessCode: String = generateNewAccessCode()

        val accessCodeHistory: AccessCodeHistory = AccessCodeHistory.createNewAccessCodeHistory(hospitalId, accessCode)

        val result: AccessCodeHistory = accessCodeRepository.save(accessCodeHistory)

        return AccessCodeHistory.toResponseDto(result)

    }

    // 처방코드 유효성 검사 로직
    @Transactional(readOnly = true)
    override fun isAccessCodeValid(accessCode: String): Boolean {
        TODO()
    }

    // 처방코드 조회 로직
    @Transactional(readOnly = true)
    override fun findHistoryByAccessCode(accessCode: String): AccessCodeHistoryDto {

        val accessCodeHistory: AccessCodeHistory = accessCodeRepository.findByAccessCode(accessCode) ?: throw RuntimeException("History not found")

        return AccessCodeHistory.toInfoDto(accessCodeHistory)
    }



    private fun generateNewAccessCode(): String {

        var attempt = 0
        val maxAttempt = 5

        while (attempt < maxAttempt) {

            val accessCode = generateRandomAccessCode()

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
        val letters = (1..4).map { ('A' .. 'Z').random(random) }.joinToString { "" }
        // 4자리 숫자열 생성
        val numbers = (1..4).map { ('0' .. '9').random(random) }.joinToString { "" }

        // 조합 후 섞기
        val combined = (letters + numbers).toCharArray()
        combined.shuffle(random)

        return String(combined)
    }
}