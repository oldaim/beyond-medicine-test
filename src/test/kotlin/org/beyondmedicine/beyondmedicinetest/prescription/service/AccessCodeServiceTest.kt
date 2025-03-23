package org.beyondmedicine.beyondmedicinetest.prescription.service
/*
import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.just
import io.mockk.runs
import io.mockk.verify
import org.beyondmedicine.beyondmedicinetest.config.ServiceTest
import org.beyondmedicine.beyondmedicinetest.prescription.domain.constant.AccessCodeStatus
import org.beyondmedicine.beyondmedicinetest.prescription.dto.*
import org.beyondmedicine.beyondmedicinetest.prescription.repository.custom.AccessCodeRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
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
    @DisplayName("처방 코드 생성 성공 테스트")
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
        every { 
            accessCodeRepository.saveAccessCodeHistory(any()) 
        } returns savedAccessCodeHistoryDto
        
        // when
        val result = accessCodeService.createAccessCodeHistory(requestDto)
        
        // then
        result.accessCode shouldBe testAccessCode
        result.createdAt shouldNotBe null
        
        verify { accessCodeRepository.existsByAccessCode(any()) }
        verify { accessCodeRepository.saveAccessCodeHistory(any()) }
    }
    
    @Test
    @DisplayName("activateAccessCode - 코드 활성화 성공")
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
        
        // 코드 존재 가정
        every { accessCodeRepository.findByAccessCode(testAccessCode) } returns accessCodeHistoryDto
        
        // 유저코드가 존재하지 않는다고 가정
        every { 
            accessCodeRepository.findUserAccessCodeByUserIdAndStatus(testUserId, AccessCodeStatus.ACTIVE) 
        } returns null
        
        // 저장 성공 가정
        every { accessCodeRepository.saveUserAccessCode(any()) } answers {
            firstArg<UserAccessCodeDto>().copy(id = 1L)
        }
        
        // when & then - 예외가 발생하지 않아야 함
        accessCodeService.activateAccessCode(requestDto)
        
        verify { accessCodeRepository.findByAccessCode(testAccessCode) }
        verify { 
            accessCodeRepository.findUserAccessCodeByUserIdAndStatus(testUserId, AccessCodeStatus.ACTIVE) 
        }
        verify { accessCodeRepository.saveUserAccessCode(any()) }
    }
    
    @Test
    @DisplayName("activateAccessCode - 이미 활성화된 유저 코드가 있는 경우 예외 발생")
    fun activateAccessCode_alreadyActivated_throwsException() {
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
        
        val existingUserCodeDto = UserAccessCodeDto(
            id = 1L,
            userId = testUserId,
            accessCode = "EXISTING",
            status = AccessCodeStatus.ACTIVE,
            expiresAt = LocalDateTime.now().plusDays(30)
        )
        
        // 코드 존재 가정
        every { accessCodeRepository.findByAccessCode(testAccessCode) } returns accessCodeHistoryDto
        
        // 이미 활성화된 유저코드가 있고, 만료되지 않았다고 가정
        every { 
            accessCodeRepository.findUserAccessCodeByUserIdAndStatus(testUserId, AccessCodeStatus.ACTIVE) 
        } returns existingUserCodeDto
        
        every { existingUserCodeDto.isExpired() } returns false
        
        // when & then
        val exception = shouldThrow<IllegalStateException> {
            accessCodeService.activateAccessCode(requestDto)
        }
        
        exception.message shouldBe "User already has active access code"
        
        verify { accessCodeRepository.findByAccessCode(testAccessCode) }
        verify { 
            accessCodeRepository.findUserAccessCodeByUserIdAndStatus(testUserId, AccessCodeStatus.ACTIVE) 
        }
    }
    
    @Test
    @DisplayName("activateAccessCode - 만료된 유저 코드가 있는 경우 성공적으로 새 코드 활성화")
    fun activateAccessCode_withExpiredCode_success() {
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
        
        val existingUserCodeDto = UserAccessCodeDto(
            id = 1L,
            userId = testUserId,
            accessCode = "EXISTING",
            status = AccessCodeStatus.ACTIVE,
            expiresAt = LocalDateTime.now().minusDays(1) // 만료된 상태
        )
        
        val expiredUserCodeDto = existingUserCodeDto.copy(status = AccessCodeStatus.EXPIRED)
        
        // 코드 존재 가정
        every { accessCodeRepository.findByAccessCode(testAccessCode) } returns accessCodeHistoryDto
        
        // 만료된 유저코드가 있다고 가정
        every { 
            accessCodeRepository.findUserAccessCodeByUserIdAndStatus(testUserId, AccessCodeStatus.ACTIVE) 
        } returns existingUserCodeDto
        
        every { existingUserCodeDto.isExpired() } returns true
        every { existingUserCodeDto.expire() } returns expiredUserCodeDto
        
        // 저장 성공 가정
        every { accessCodeRepository.saveUserAccessCode(any()) } answers {
            firstArg<UserAccessCodeDto>().copy(id = if (firstArg<UserAccessCodeDto>().status == AccessCodeStatus.EXPIRED) 1L else 2L)
        }
        
        // when
        accessCodeService.activateAccessCode(requestDto)
        
        // then
        verify { accessCodeRepository.findByAccessCode(testAccessCode) }
        verify { 
            accessCodeRepository.findUserAccessCodeByUserIdAndStatus(testUserId, AccessCodeStatus.ACTIVE) 
        }
        verify { existingUserCodeDto.isExpired() }
        verify { existingUserCodeDto.expire() }
        verify(exactly = 2) { accessCodeRepository.saveUserAccessCode(any()) }
    }
    
    @Test
    @DisplayName("isUserAccessCodeActivated - 유저 코드가 활성화되어 있는 경우 true 반환")
    fun isUserAccessCodeActivated_withActiveCode_returnsTrue() {
        // given
        val userCodeDto = UserAccessCodeDto(
            id = 1L,
            userId = testUserId,
            accessCode = testAccessCode,
            status = AccessCodeStatus.ACTIVE,
            expiresAt = LocalDateTime.now().plusDays(30)
        )
        
        every { 
            accessCodeRepository.findUserAccessCodeByUserIdAndStatus(testUserId, AccessCodeStatus.ACTIVE) 
        } returns userCodeDto
        
        // when
        val result = accessCodeService.isUserAccessCodeActivated(testUserId)
        
        // then
        result shouldBe true
        
        verify { 
            accessCodeRepository.findUserAccessCodeByUserIdAndStatus(testUserId, AccessCodeStatus.ACTIVE) 
        }
    }
    
    @Test
    @DisplayName("isUserAccessCodeActivated - 유저 코드가 없는 경우 false 반환")
    fun isUserAccessCodeActivated_withNoCode_returnsFalse() {
        // given
        every { 
            accessCodeRepository.findUserAccessCodeByUserIdAndStatus(testUserId, AccessCodeStatus.ACTIVE) 
        } returns null
        
        // when
        val result = accessCodeService.isUserAccessCodeActivated(testUserId)
        
        // then
        result shouldBe false
        
        verify { 
            accessCodeRepository.findUserAccessCodeByUserIdAndStatus(testUserId, AccessCodeStatus.ACTIVE) 
        }
    }
}*/