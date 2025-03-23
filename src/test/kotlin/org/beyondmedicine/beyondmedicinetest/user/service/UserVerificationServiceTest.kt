package org.beyondmedicine.beyondmedicinetest.user.service

import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.verify
import org.beyondmedicine.beyondmedicinetest.common.exception.UserAccessCodeNotActivatedException
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
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@SpringBootTest
@ActiveProfiles("test")
class UserVerificationServiceTest {

    @MockkBean
    private lateinit var userVerificationRepository: UserVerificationRepository

    @MockkBean
    private lateinit var accessCodeService: AccessCodeService

    private lateinit var userVerificationService: UserVerificationService

    private val testUserId = "e4e3ecbd-2208-4905-8120-426473d0eae9"
    private val testVersion = "1.0.0"
    private val testOs = "android"
    private val testMode = "debug"
    private val testHash = "3f071a178aed4eee8a77e34999ba37d88b62d36a3624e5a0d6292ea8cb2eebdc"
    private val testRequestHashSecret = "testSecret"

    @BeforeEach
    fun setup() {
        userVerificationService = UserVerificationServiceImpl(
            userVerificationRepository,
            accessCodeService,
            testRequestHashSecret
        )
    }

    @Test
    @DisplayName("verifyUserRequest - 사용자 검증 성공 (업데이트 필요 없음)")
    fun verifyUserRequest_success_noUpdateRequired() {
        // given
        // 최신 버전 사용 유저
        val requestDto = UserVerificationRequestDto.create(
            userId = testUserId,
            version = "1.0.1",
            os = testOs,
            mode = testMode,
            hash = testHash
        )

        // 앱
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

        // when
        val result = userVerificationService.verifyUserRequest(requestDto)

        // then
        result shouldBe UpdateStatus.NO_UPDATE_REQUIRED

        // verify
        verify(exactly = 1) { userVerificationRepository.findAppVersionByOsAndMode(testOs, testMode) }
        verify(exactly = 1) { accessCodeService.isUserAccessCodeActivated(testUserId) }

    }

    @Test
    @DisplayName("verifyUserRequest - 사용자 검증 성공 (업데이트 권장)")
    fun verifyUserRequest_success_updateRequired() {

        // given
        // 최신버전보단 낮은 유저 지만, 최소 버전보다는 높은 유저
        val requestDto = UserVerificationRequestDto.create(
            userId = testUserId,
            version = "1.0.0",
            os = testOs,
            mode = testMode,
            hash = testHash
        )
        // 앱
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


        // when
        val result: UpdateStatus = userVerificationService.verifyUserRequest(requestDto)

        // then
        result shouldBe UpdateStatus.UPDATE_REQUIRED

        // verify
        verify(exactly = 1) { userVerificationRepository.findAppVersionByOsAndMode(testOs, testMode) }
        verify(exactly = 1) { accessCodeService.isUserAccessCodeActivated(testUserId) }
    }

    @Test
    @DisplayName("verifyUserRequest - 사용자 검증 성공 (강제 업데이트 필요)")
    fun verifyUserRequest_forceUpdateRequired() {

        // given
        // 최소 버전보다 낮은 유저
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


        // when
        val result: UpdateStatus = userVerificationService.verifyUserRequest(requestDto)

        // then
        result shouldBe UpdateStatus.FORCE_UPDATE_REQUIRED

        // verify
        verify(exactly = 1) { userVerificationRepository.findAppVersionByOsAndMode(testOs, testMode) }
        verify(exactly = 1) { accessCodeService.isUserAccessCodeActivated(testUserId) }
    }

    @ParameterizedTest
    @ValueSource(strings = ["1.1.1-gamma.1", "invalid version ", "111.1", "1.1.1-alpha.1.1"])
    @DisplayName("verifyUserRequest - 사용자 검증 실패 (유효 하지 않은 버전)")
    fun verifyUserRequest_failure_appVersionNotFound(version: String) {

        // given
        // 유효하지 않은 버전 가진 User
        val requestDto = UserVerificationRequestDto.create(
            userId = testUserId,
            version = version,
            os = testOs,
            mode = testMode,
            hash = testHash
        )

        // when & then
        val exception: IllegalArgumentException = shouldThrow<IllegalArgumentException> {
            userVerificationService.verifyUserRequest(requestDto)
        }

        exception.message shouldBe "dto parameters are not valid"

        // verify
        verify(exactly = 0) { userVerificationRepository.findAppVersionByOsAndMode(any(), any()) }
        verify(exactly = 0) { accessCodeService.isUserAccessCodeActivated(any()) }
    }

    @Test
    @DisplayName("verifyUserRequest - 버전 검증 (프리릴리즈 버전 vs 정식 버전)")
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


        // when
        val result = userVerificationService.verifyUserRequest(requestDto)

        // then 정식버전이 항상 상위 버전이므로 업데이트 필요
        result shouldBe UpdateStatus.UPDATE_REQUIRED

    }

    @Test
    @DisplayName("verifyUserRequest - 버전 검증 (알파 버전 vs 베타 버전)")
    fun compareVersion_preReleaseBeta() {
        // given
        // 알파 버전 사용자
        val requestDto = UserVerificationRequestDto.create(
            userId = testUserId,
            version = "1.0.0-alpha.1",
            os = testOs,
            mode = testMode,
            hash = testHash
        )

        // 베타 버전 사용자
        val appVersionDto = AppVersionDto(
            latestVersion = "1.0.0-beta.1",
            minimumVersion = "0.9.0",
            os = testOs,
            mode = testMode,
            hash = testHash
        )

        // mocks
        every { userVerificationRepository.findAppVersionByOsAndMode(testOs, testMode) } returns appVersionDto
        every { accessCodeService.isUserAccessCodeActivated(testUserId) } returns true


        // when
        val result = userVerificationService.verifyUserRequest(requestDto)

        // then alpha 버전이 beta 버전보다 낮으므로 업데이트 필요
        result shouldBe UpdateStatus.UPDATE_REQUIRED

    }

    @Test
    @DisplayName("verifyUserRequest - 사용자 검증 실패 (유효하지 않은 OS)")
    fun verifyUserRequest_failure_osNotFound() {

        // given
        val requestDto = UserVerificationRequestDto.create(
            userId = testUserId,
            version = testVersion,
            os = "invalid_os",
            mode = testMode,
            hash = testHash
        )

        //when
        val exception: IllegalArgumentException = shouldThrow<IllegalArgumentException> {
            userVerificationService.verifyUserRequest(requestDto)
        }

        //then
        exception.message shouldBe "dto parameters are not valid"

        // verify
        verify(exactly = 0) { userVerificationRepository.findAppVersionByOsAndMode(any(), any()) }
        verify(exactly = 0) { accessCodeService.isUserAccessCodeActivated(any()) }

    }

    @Test
    @DisplayName("verifyUserRequest - 사용자 검증 실패 (유효하지 않은 mode)")
    fun verifyUserRequest_failure_modeNotFound() {

        // given
        val requestDto = UserVerificationRequestDto.create(
            userId = testUserId,
            version = testVersion,
            os = testOs,
            mode = "invalid_mode",
            hash = testHash
        )

        //when
        val exception: IllegalArgumentException = shouldThrow<IllegalArgumentException> {
            userVerificationService.verifyUserRequest(requestDto)
        }

        //then
        exception.message shouldBe "dto parameters are not valid"

        // verify
        verify(exactly = 0) { userVerificationRepository.findAppVersionByOsAndMode(any(), any()) }
        verify(exactly = 0) { accessCodeService.isUserAccessCodeActivated(any()) }

    }

    @Test
    @DisplayName("verifyUserRequest - 사용자 검증 실패 (OS, Mode 존재하나 DB 조회 실패)")
    fun verifyUserRequest_failure_dbDataNotFound() {

        // given
        val requestDto = UserVerificationRequestDto.create(
            userId = testUserId,
            version = testVersion,
            os = testOs,
            mode = testMode,
            hash = testHash
        )

        // mocks null 반환 하도록 설정
        every { userVerificationRepository.findAppVersionByOsAndMode(any(), any()) } returns null

        //when
        val exception: IllegalArgumentException = shouldThrow<IllegalArgumentException> {
            userVerificationService.verifyUserRequest(requestDto)
        }

        //then
        exception.message shouldBe "os and mode not found or database error"

        // verify
        verify(exactly = 1) { userVerificationRepository.findAppVersionByOsAndMode(any(), any()) }
        verify(exactly = 0) { accessCodeService.isUserAccessCodeActivated(any()) }

    }

    @Test
    @DisplayName("verifyUserRequest - 사용자 검증 실패 (Hash 검증 실패)")
    fun verifyUserRequest_hashMismatch() {
        // given
        val requestDto = UserVerificationRequestDto.create(
            userId = testUserId,
            version = testVersion,
            os = testOs,
            mode = testMode,
            hash = "DifferentHash"
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


        // when & then
        val exception = shouldThrow<IllegalArgumentException> {
            userVerificationService.verifyUserRequest(requestDto)
        }

        exception.message shouldBe "hash is not matched"

        // verify
        verify { userVerificationRepository.findAppVersionByOsAndMode(testOs, testMode) }
        verify(exactly = 0) { accessCodeService.isUserAccessCodeActivated(any()) }
    }

    @Test
    @DisplayName("verifyUserRequest - 사용자 검증 실패 (AccessCode 미활성화)")
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

        // when & then
        val exception: UserAccessCodeNotActivatedException = shouldThrow<UserAccessCodeNotActivatedException> {
            userVerificationService.verifyUserRequest(requestDto)
        }

        exception.message shouldBe "user access code is not activated"
    }

    @Test
    @DisplayName("saveUserVerificationLog - 사용자 로그 저장 성공")
    fun saveUserVerificationLog_success() {
        // given
        val requestDto = UserVerificationRequestDto.create(
            userId = testUserId,
            version = testVersion,
            os = testOs,
            mode = testMode,
            hash = testHash
        )

        val expectedLogDto = UserVerificationLogDto.create(
            userId = testUserId,
            version = testOs,
            os = testMode,
            mode = testVersion,
            hash = testHash
        )

        // mocks
        every { userVerificationRepository.saveUserVerificationLog(any()) } returns expectedLogDto.copy(id = 1L)

        // when & then
        userVerificationService.saveUserVerificationLog(requestDto)

        // verify
        verify(exactly = 1) { userVerificationRepository.saveUserVerificationLog(any()) }
    }

}


