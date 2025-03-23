package org.beyondmedicine.beyondmedicinetest.user.service

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.verify
import io.mockk.mockkStatic
import org.beyondmedicine.beyondmedicinetest.prescription.service.AccessCodeService
import org.beyondmedicine.beyondmedicinetest.user.constants.UpdateStatus
import org.beyondmedicine.beyondmedicinetest.user.dto.AppVersionDto
import org.beyondmedicine.beyondmedicinetest.user.dto.UserVerificationLogDto
import org.beyondmedicine.beyondmedicinetest.user.dto.UserVerificationRequestDto
import org.beyondmedicine.beyondmedicinetest.user.repository.UserVerificationRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.security.MessageDigest
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest
@ActiveProfiles("test")
class UserVerificationServiceTest {

    @MockkBean
    private lateinit var userVerificationRepository: UserVerificationRepository

    @MockkBean
    private lateinit var accessCodeService: AccessCodeService

    @MockkBean
    private lateinit var messageDigest: MessageDigest

    private lateinit var userVerificationService: UserVerificationService

    private val testUserId = "e4e3ecbd-2208-4905-8120-426473d0eae9"
    private val testVersion = "1.0.0"
    private val testOs = "android"
    private val testMode = "debug"
    private val testHash = "Y95ULTuEF0uXNq7fSNa1EEzP0FU="
    private val testRequestHashSecret = "testSecret"

    @BeforeEach
    fun setup() {
        userVerificationService = UserVerificationServiceImpl(
            userVerificationRepository,
            accessCodeService,
            testRequestHashSecret,
            messageDigest
        )

        every { 
            userVerificationRepository.saveUserVerificationLog(any()) 
        } returns UserVerificationLogDto.create(
            userId = testUserId,
            version = testVersion,
            os = testOs,
            mode = testMode,
            hash = testHash
        )
    }

    @Test
    @DisplayName("사용자 검증 성공 - 업데이트 필요 없음")
    fun verifyUserRequest_noUpdateRequired() {
        // given
        val requestDto = UserVerificationRequestDto.create(
            userId = testUserId,
            version = "1.0.1",
            os = testOs,
            mode = testMode,
            hash = testHash
        )

        val appVersionDto = AppVersionDto(
            latestVersion = "1.0.1",
            minimumVersion = "0.9.0",
            os = testOs,
            mode = testMode,
            hash = testHash
        )

        // mocks
        every { userVerificationRepository.findAppVersionByOsAndMode(testOs, testMode) } returns appVersionDto
        every { accessCodeService.isUserAccessCodeActivated(testUserId) } returns true
        mockHashValidation(true)

        // when
        val result = userVerificationService.verifyUserRequest(requestDto)

        // then
        result shouldBe UpdateStatus.NO_UPDATE_REQUIRED

        // verify
        verify { userVerificationRepository.findAppVersionByOsAndMode(testOs, testMode) }
        verify { accessCodeService.isUserAccessCodeActivated(testUserId) }
        verify { userVerificationRepository.saveUserVerificationLog(any()) }
    }

    @Test
    @DisplayName("사용자 검증 성공 - 업데이트 권장")
    fun verifyUserRequest_updateRequired() {
        // given
        val requestDto = UserVerificationRequestDto.create(
            userId = testUserId,
            version = "1.0.0",
            os = testOs,
            mode = testMode,
            hash = testHash
        )

        val appVersionDto = AppVersionDto(
            latestVersion = "1.0.1",
            minimumVersion = "0.9.0",
            os = testOs,
            mode = testMode,
            hash = testHash
        )

        // mocks
        every { userVerificationRepository.findAppVersionByOsAndMode(testOs, testMode) } returns appVersionDto
        every { accessCodeService.isUserAccessCodeActivated(testUserId) } returns true
        mockHashValidation(true)

        // when
        val result = userVerificationService.verifyUserRequest(requestDto)

        // then
        result shouldBe UpdateStatus.UPDATE_REQUIRED

        // verify
        verify { userVerificationRepository.findAppVersionByOsAndMode(testOs, testMode) }
        verify { accessCodeService.isUserAccessCodeActivated(testUserId) }
        verify { userVerificationRepository.saveUserVerificationLog(any()) }
    }

    @Test
    @DisplayName("사용자 검증 성공 - 강제 업데이트 필요")
    fun verifyUserRequest_forceUpdateRequired() {
        // given
        val requestDto = UserVerificationRequestDto.create(
            userId = testUserId,
            version = "0.8.0",
            os = testOs,
            mode = testMode,
            hash = testHash
        )

        val appVersionDto = AppVersionDto(
            latestVersion = "1.0.1",
            minimumVersion = "0.9.0",
            os = testOs,
            mode = testMode,
            hash = testHash
        )

        // mocks
        every { userVerificationRepository.findAppVersionByOsAndMode(testOs, testMode) } returns appVersionDto
        every { accessCodeService.isUserAccessCodeActivated(testUserId) } returns true
        mockHashValidation(true)

        // when
        val result = userVerificationService.verifyUserRequest(requestDto)

        // then
        result shouldBe UpdateStatus.FORCE_UPDATE_REQUIRED

        // verify
        verify { userVerificationRepository.findAppVersionByOsAndMode(testOs, testMode) }
        verify { accessCodeService.isUserAccessCodeActivated(testUserId) }
        verify { userVerificationRepository.saveUserVerificationLog(any()) }
    }

    @Test
    @DisplayName("사용자 검증 실패 - 유효하지 않은, 버전, OS, MODE 형식")
    fun verifyUserRequest_invalidDtoParameters() {
        // given
        val requestDto = UserVerificationRequestDto.create(
            userId = testUserId,
            version = "invalid-version",
            os = testOs,
            mode = testMode,
            hash = testHash
        )

        // when & then
        val exception = shouldThrow<IllegalArgumentException> {
            userVerificationService.verifyUserRequest(requestDto)
        }

        exception.message shouldBe "dto parameters are not valid"

        // verify
        verify(exactly = 0) { userVerificationRepository.findAppVersionByOsAndMode(any(), any()) }
        verify(exactly = 0) { accessCodeService.isUserAccessCodeActivated(any()) }
        verify { userVerificationRepository.saveUserVerificationLog(any()) }
    }

    @Test
    @DisplayName("사용자 검증 실패 - 존재하지 않는 OS와 Mode")
    fun verifyUserRequest_appVersionNotFound() {
        // given
        val requestDto = UserVerificationRequestDto.create(
            userId = testUserId,
            version = testVersion,
            os = testOs,
            mode = testMode,
            hash = testHash
        )

        // mocks
        every { userVerificationRepository.findAppVersionByOsAndMode(testOs, testMode) } returns null

        // when & then
        val exception = shouldThrow<IllegalArgumentException> {
            userVerificationService.verifyUserRequest(requestDto)
        }

        exception.message shouldBe "os and mode not found"

        // verify
        verify { userVerificationRepository.findAppVersionByOsAndMode(testOs, testMode) }
        verify(exactly = 0) { accessCodeService.isUserAccessCodeActivated(any()) }
        verify { userVerificationRepository.saveUserVerificationLog(any()) }
    }

    @Test
    @DisplayName("사용자 검증 실패 - 해시 불일치")
    fun verifyUserRequest_hashMismatch() {
        // given
        val requestDto = UserVerificationRequestDto.create(
            userId = testUserId,
            version = testVersion,
            os = testOs,
            mode = testMode,
            hash = testHash
        )

        val appVersionDto = AppVersionDto(
            latestVersion = "1.0.1",
            minimumVersion = "0.9.0",
            os = testOs,
            mode = testMode,
            hash = "DifferentHash"
        )

        // mocks
        every { userVerificationRepository.findAppVersionByOsAndMode(testOs, testMode) } returns appVersionDto
        mockHashValidation(false)

        // when & then
        val exception = shouldThrow<IllegalArgumentException> {
            userVerificationService.verifyUserRequest(requestDto)
        }

        exception.message shouldBe "hash is not matched"

        // verify
        verify { userVerificationRepository.findAppVersionByOsAndMode(testOs, testMode) }
        verify(exactly = 0) { accessCodeService.isUserAccessCodeActivated(any()) }
        verify { userVerificationRepository.saveUserVerificationLog(any()) }
    }

    @Test
    @DisplayName("사용자 검증 실패 - 활성화된 코드 없음")
    fun verifyUserRequest_accessCodeNotActivated() {
        // given
        val requestDto = UserVerificationRequestDto.create(
            userId = testUserId,
            version = testVersion,
            os = testOs,
            mode = testMode,
            hash = testHash
        )

        val appVersionDto = AppVersionDto(
            latestVersion = "1.0.1",
            minimumVersion = "0.9.0",
            os = testOs,
            mode = testMode,
            hash = testHash
        )

        // mocks
        every { userVerificationRepository.findAppVersionByOsAndMode(testOs, testMode) } returns appVersionDto
        every { accessCodeService.isUserAccessCodeActivated(testUserId) } returns false
        mockHashValidation(true)

        // when & then
        val exception = shouldThrow<IllegalArgumentException> {
            userVerificationService.verifyUserRequest(requestDto)
        }

        exception.message shouldBe "user access code is not activated"

        // verify
        verify { userVerificationRepository.findAppVersionByOsAndMode(testOs, testMode) }
        verify { accessCodeService.isUserAccessCodeActivated(testUserId) }
        verify { userVerificationRepository.saveUserVerificationLog(any()) }
    }

    @Test
    @DisplayName("버전 비교 테스트 - 프리릴리즈 알파 비교")
    fun compareVersion_preReleaseAlpha() {
        // given
        val requestDto = UserVerificationRequestDto.create(
            userId = testUserId,
            version = "1.0.0-alpha.1",
            os = testOs,
            mode = testMode,
            hash = testHash
        )

        val appVersionDto = AppVersionDto(
            latestVersion = "1.0.0",
            minimumVersion = "0.9.0",
            os = testOs,
            mode = testMode,
            hash = testHash
        )

        // mocks
        every { userVerificationRepository.findAppVersionByOsAndMode(testOs, testMode) } returns appVersionDto
        every { accessCodeService.isUserAccessCodeActivated(testUserId) } returns true
        mockHashValidation(true)

        // when
        val result = userVerificationService.verifyUserRequest(requestDto)

        // then
        result shouldBe UpdateStatus.UPDATE_REQUIRED

        // verify
        verify { userVerificationRepository.findAppVersionByOsAndMode(testOs, testMode) }
        verify { accessCodeService.isUserAccessCodeActivated(testUserId) }
        verify { userVerificationRepository.saveUserVerificationLog(any()) }
    }

    @Test
    @DisplayName("버전 비교 테스트 - 프리릴리즈 베타 비교")
    fun compareVersion_preReleaseBeta() {
        // given
        val requestDto = UserVerificationRequestDto.create(
            userId = testUserId,
            version = "1.0.0-beta.1",
            os = testOs,
            mode = testMode,
            hash = testHash
        )

        val appVersionDto = AppVersionDto(
            latestVersion = "1.0.0",
            minimumVersion = "0.9.0",
            os = testOs,
            mode = testMode,
            hash = testHash
        )

        // mocks
        every { userVerificationRepository.findAppVersionByOsAndMode(testOs, testMode) } returns appVersionDto
        every { accessCodeService.isUserAccessCodeActivated(testUserId) } returns true
        mockHashValidation(true)

        // when
        val result = userVerificationService.verifyUserRequest(requestDto)

        // then
        result shouldBe UpdateStatus.UPDATE_REQUIRED

        // verify
        verify { userVerificationRepository.findAppVersionByOsAndMode(testOs, testMode) }
        verify { accessCodeService.isUserAccessCodeActivated(testUserId) }
        verify { userVerificationRepository.saveUserVerificationLog(any()) }
    }

    // Private helper method for mocking hash validation
    private fun mockHashValidation(isValid: Boolean) {
        // Mock MessageDigest
        every { messageDigest.digest(any()) } returns "mockDigest".toByteArray()
        
        // Set up base64 encoder mock to return the test hash or a different hash based on validation flag
        val mockEncoderOutput = if (isValid) testHash else "Different="
        
        // Use reflection to mock the static Base64.getEncoder().encodeToString() call
        mockkStatic(Base64::class)
        every { Base64.getEncoder().encodeToString(any()) } returns mockEncoderOutput
    }
}


