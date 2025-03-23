package org.beyondmedicine.beyondmedicinetest.prescription.service

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.verify
import org.beyondmedicine.beyondmedicinetest.common.exception.AccessCodeAlreadyActivatedException
import org.beyondmedicine.beyondmedicinetest.common.exception.AccessCodeRetryFailException
import org.beyondmedicine.beyondmedicinetest.prescription.domain.constant.AccessCodeStatus
import org.beyondmedicine.beyondmedicinetest.prescription.dto.*
import org.beyondmedicine.beyondmedicinetest.prescription.repository.AccessCodeRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.security.SecureRandom
import java.time.LocalDateTime

@ExtendWith(SpringExtension::class)
@SpringBootTest
@ActiveProfiles("test")
class AccessCodeServiceTest {

    @MockkBean
    private lateinit var accessCodeRepository: AccessCodeRepository
    
    @MockkBean
    private lateinit var secureRandom: SecureRandom

    private lateinit var accessCodeService: AccessCodeService

    private val testAccessCode = "TEST1234"
    private val testUserId = "test-user"
    private val testHospitalId = "test-hospital"

    @BeforeEach
    fun setup() {
        // 테스트용 랜덤 코드 생성을 위한 설정
        every { secureRandom.nextLong() } returns 12345L
        
        accessCodeService = AccessCodeServiceImpl(accessCodeRepository, secureRandom)
    }

    @Test
    @DisplayName("처방 코드 생성 - 성공")
    fun createAccessCodeHistory_success() {
        // given
        val requestDto = CreateAccessCodeRequestDto(hospitalId = testHospitalId)
        
        val savedAccessCodeHistoryDto = AccessCodeHistoryDto(
            id = 1L,
            hospitalId = testHospitalId,
            accessCode = testAccessCode
        )
        
        // 존재하지 않는 accessCode로 가정
        every { accessCodeRepository.existsByAccessCode(any()) } returns false
        
        // DTO 저장 성공 가정
        every { accessCodeRepository.saveAccessCodeHistory(any()) } returns savedAccessCodeHistoryDto
        
        // when
        val result: CreateAccessCodeResponseDto = accessCodeService.createAccessCodeHistory(requestDto)
        
        // then
        result.accessCode shouldBe testAccessCode
        result.createdAt shouldNotBe null
        
        verify(exactly = 1) { accessCodeRepository.existsByAccessCode(any()) }
        verify(exactly = 1) { accessCodeRepository.saveAccessCodeHistory(any()) }
    }

    @Test
    @DisplayName("처방 코드 생성 - access code 재시도 10번 시도후 실패")
    fun createAccessCodeHistory_retryExceeded_throwsException() {
        // given
        val requestDto = CreateAccessCodeRequestDto(hospitalId = testHospitalId)

        // 존재하는 accessCode로 가정
        every { accessCodeRepository.existsByAccessCode(any()) } returns true

        // when & then
        val exception = shouldThrow<AccessCodeRetryFailException> {
            accessCodeService.createAccessCodeHistory(requestDto)
        }

        exception.message shouldBe "Failed to generate new access code after 10 attempts"

        verify(exactly = 10) { accessCodeRepository.existsByAccessCode(any()) }
    }
    
    @Test
    @DisplayName("activateAccessCode - 코드 활성화 성공 (활성화 된 코드가 없는 경우)")
    fun activateAccessCode_success() {
        // given
        val requestDto = ActivateAccessCodeRequestDto(
            userId = testUserId,
            accessCode = testAccessCode
        )
        
        val accessCodeHistoryDto = AccessCodeHistoryDto(
            id = 1L,
            hospitalId = testHospitalId,
            accessCode = testAccessCode
        )
        
        // AccessCode 존재 가정
        every { accessCodeRepository.findByAccessCode(testAccessCode) } returns accessCodeHistoryDto
        
        // 활성화 된 코드가 없는 경우
        every {
            accessCodeRepository.findUserAccessCodeByUserIdAndStatus(testUserId, AccessCodeStatus.ACTIVE)
        } returns null
        
        // 저장 성공 가정
        every { accessCodeRepository.saveUserAccessCode(any()) } answers {
            firstArg<UserAccessCodeDto>().copy(id = 1L)
        }
        
        // when & then
        val responseDto: ActivateAccessCodeResponseDto = accessCodeService.activateAccessCode(requestDto)

        //응답 검증
        responseDto.userId shouldBe testUserId
        responseDto.accessCode shouldBe testAccessCode

        //시간관련 검증
        val expiresDate = responseDto.activatedAt.plusDays(43).withHour(0).withMinute(0).withSecond(0).withNano(0)
        responseDto.expiresAt shouldBe expiresDate


        verify(exactly = 1) { accessCodeRepository.findByAccessCode(testAccessCode) }
        verify(exactly = 1) { accessCodeRepository.findUserAccessCodeByUserIdAndStatus(testUserId, AccessCodeStatus.ACTIVE) }
        verify(exactly = 1) { accessCodeRepository.saveUserAccessCode(any()) }
    }
    
    @Test
    @DisplayName("activateAccessCode - 코드 비활성화 테스트 (활성화 된 코드의 만료일이 지났을 경우)")
    fun activateAccessCode_alreadyActivated_Expired() {
        // given expiredAt 이 1년 경과
        val existingUserAccessCodeDto = UserAccessCodeDto(
            id = 1L,
            userId = testUserId,
            accessCode = "EXISTING",
            status = AccessCodeStatus.ACTIVE,
            expiresAt = LocalDateTime.now().minusYears(1)
        )

        //when expiredAt 상태 변경
        val expiredUserAccessCodeDto = existingUserAccessCodeDto.expire()

        //then
        expiredUserAccessCodeDto.isExpired() shouldBe true

        expiredUserAccessCodeDto.status shouldBe AccessCodeStatus.EXPIRED

    }

    @Test
    @DisplayName("activateAccessCode - 코드 활성화 실패 (유효하지 않은 access code)")
    fun activateAccessCode_invalidAccessCode_throwsException() {
        // given
        val requestDto = ActivateAccessCodeRequestDto(
            userId = testUserId,
            accessCode = testAccessCode
        )

        // AccessCode 존재하지 않는 경우
        every { accessCodeRepository.findByAccessCode(testAccessCode) } returns null

        // when & then
        val exception = shouldThrow<NoSuchElementException> {
            accessCodeService.activateAccessCode(requestDto)
        }

        exception.message shouldBe "Access code not found"

        verify(exactly = 1) { accessCodeRepository.findByAccessCode(testAccessCode) }
    }

    @Test
    @DisplayName("activateAccessCode - 코드 활성화 실패 (이미 활성화 된 코드가 있는 경우)")
    fun activateAccessCode_alreadyActivated_throwsException() {
        // given
        val requestDto = ActivateAccessCodeRequestDto(
            userId = testUserId,
            accessCode = testAccessCode
        )

        val existingUserAccessCodeDto = UserAccessCodeDto(
            id = 1L,
            userId = testUserId,
            accessCode = testAccessCode,
            status = AccessCodeStatus.ACTIVE,
            activatedAt = LocalDateTime.now(),
            expiresAt = LocalDateTime.now().plusDays(43)
        )

        val accessCodeHistoryDto = AccessCodeHistoryDto(
            id = 1L,
            hospitalId = testHospitalId,
            accessCode = testAccessCode
        )

        // AccessCode 존재 가정
        every { accessCodeRepository.findByAccessCode(testAccessCode) } returns accessCodeHistoryDto

        // 활성화 된 코드가 있는 경우
        every {
            accessCodeRepository.findUserAccessCodeByUserIdAndStatus(testUserId, AccessCodeStatus.ACTIVE)
        } returns existingUserAccessCodeDto

        // when & then
        val exception = shouldThrow<AccessCodeAlreadyActivatedException> {
            accessCodeService.activateAccessCode(requestDto)
        }

        exception.message shouldBe "User already has active access code"

        verify(exactly = 1) { accessCodeRepository.findByAccessCode(testAccessCode) }
        verify(exactly = 1) { accessCodeRepository.findUserAccessCodeByUserIdAndStatus(testUserId, AccessCodeStatus.ACTIVE) }
    }

    @Test
    @DisplayName("isUserAccessCodeActivated - 성공 (활성화 된 코드가 있는 경우)")
    fun isUserAccessCodeActivated_success_true() {
        // given
        val existingUserAccessCodeDto = UserAccessCodeDto(
            id = 1L,
            userId = testUserId,
            accessCode = testAccessCode,
            status = AccessCodeStatus.ACTIVE,
            activatedAt = LocalDateTime.now(),
            expiresAt = LocalDateTime.now().plusDays(43)
        )

        // 활성화 된 코드가 있는 경우
        every {
            accessCodeRepository.findUserAccessCodeByUserIdAndStatus(testUserId, AccessCodeStatus.ACTIVE)
        } returns existingUserAccessCodeDto

        // when
        val result: Boolean = accessCodeService.isUserAccessCodeActivated(testUserId)

        // then
        result shouldBe true

        verify(exactly = 1) { accessCodeRepository.findUserAccessCodeByUserIdAndStatus(testUserId, AccessCodeStatus.ACTIVE) }
    }

    @Test
    @DisplayName("isUserAccessCodeActivated - 성공 (활성화 된 코드가 없는 경우)")
    fun isUserAccessCodeActivated_success_false() {
        // 활성화 된 코드가 없는 경우
        every {
            accessCodeRepository.findUserAccessCodeByUserIdAndStatus(testUserId, AccessCodeStatus.ACTIVE)
        } returns null

        // when
        val result: Boolean = accessCodeService.isUserAccessCodeActivated(testUserId)

        // then
        result shouldBe false

        verify(exactly = 1) { accessCodeRepository.findUserAccessCodeByUserIdAndStatus(testUserId, AccessCodeStatus.ACTIVE) }
    }




}