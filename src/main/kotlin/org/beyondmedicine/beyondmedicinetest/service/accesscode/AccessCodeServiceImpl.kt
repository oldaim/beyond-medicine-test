package org.beyondmedicine.beyondmedicinetest.service.accesscode


import org.beyondmedicine.beyondmedicinetest.domain.accesscode.AccessCodeHistory
import org.beyondmedicine.beyondmedicinetest.domain.constant.AccessCodeStatus
import org.beyondmedicine.beyondmedicinetest.dto.AccessCodeInfoDto
import org.beyondmedicine.beyondmedicinetest.dto.AccessCodeRequestDto
import org.beyondmedicine.beyondmedicinetest.dto.AccessCodeResponseDto
import org.beyondmedicine.beyondmedicinetest.repository.accesscode.AccessCodeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.SecureRandom
import java.time.LocalDateTime
import kotlin.random.Random

@Service
class AccessCodeServiceImpl(
    private val accessCodeRepository: AccessCodeRepository,
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
    @Transactional
    override fun isAccessCodeValid(accessCode: String): Boolean {

        val accessCodeHistory: AccessCodeHistory = accessCodeRepository.findByAccessCode(accessCode) ?: return false

        val expiredDate: LocalDateTime = accessCodeHistory.expiredAt

        val now: LocalDateTime = LocalDateTime.now()

        val status = accessCodeHistory.status

        // 유효성 확인시 만료작업을 진행하나, 매일 자정에 스케쥴러를 통한 배치 처리도 고려
        when {
            status == AccessCodeStatus.EXPIRED -> return false

            expiredDate.isBefore(now) || expiredDate.isEqual(now) -> { // 현재시간이 만료시간과 같거나 이전인 경우
                accessCodeHistory.expire()
                accessCodeRepository.save(accessCodeHistory) // 만료처리후 저장
                return false
            }

            else -> return true
        }
    }

    // 처방코드 조회 로직
    @Transactional(readOnly = true)
    override fun findHistoryByAccessCode(accessCode: String): AccessCodeInfoDto {

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