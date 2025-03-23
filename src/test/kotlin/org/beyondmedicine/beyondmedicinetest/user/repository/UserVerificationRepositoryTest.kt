package org.beyondmedicine.beyondmedicinetest.user.repository

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.beyondmedicine.beyondmedicinetest.config.JpaRepositoryTest
import org.beyondmedicine.beyondmedicinetest.user.domain.AppVersion
import org.beyondmedicine.beyondmedicinetest.user.dto.AppVersionDto
import org.beyondmedicine.beyondmedicinetest.user.dto.UserVerificationLogDto
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource

@JpaRepositoryTest
@Import(UserVerificationRepositoryImpl::class)
@TestPropertySource(properties = ["spring.config.location=classpath:application-test.yml"])
class UserVerificationRepositoryTest {

    @Autowired
    private lateinit var userVerificationRepository: UserVerificationRepository

    @Autowired
    private lateinit var appVersionRepository: AppVersionRepository

    @Autowired
    private lateinit var userVerificationLogRepository: UserVerificationLogRepository

    private val testUserId = "e4e3ecbd-2208-4905-8120-426473d0eae9"
    private val testVersion = "1.0.0"
    private val testOs = "Android"
    private val testMode = "release"
    private val testHash = "370901f93faca101b6a15d64325bb0d93de06cad06cbfd41ca196891c4edb145"

    @BeforeEach
    fun setup() {
        userVerificationLogRepository.deleteAll()
        appVersionRepository.deleteAll()
    }

    @Test
    @DisplayName("AppVersion 조회 - 성공")
    fun findAppVersionByOsAndMode_success() {
        // given
        val appVersion = AppVersion(
            latestVersion = "1.0.1",
            minimumVersion = "0.9.0",
            os = testOs.lowercase(),
            mode = testMode.lowercase(),
            hash = testHash
        )

        appVersionRepository.save(appVersion)

        // when
        val result: AppVersionDto? = userVerificationRepository.findAppVersionByOsAndMode(testOs.lowercase(), testMode.lowercase())

        // then
        result shouldNotBe null
        result?.latestVersion shouldBe "1.0.1"
        result?.minimumVersion shouldBe "0.9.0"
        result?.os shouldBe testOs.lowercase()
        result?.mode shouldBe testMode.lowercase()
        result?.hash shouldBe testHash
    }

    @Test
    @DisplayName("AppVersion 조회 - 성공 (존재하지않는 OS, Mode)")
    fun findAppVersionByOsAndMode_notFound() {
        // when
        val result = userVerificationRepository.findAppVersionByOsAndMode("unknown", "unknown")

        // then
        result shouldBe null
    }

    @Test
    @DisplayName("UserVerificationLog 저장 성공")
    fun saveUserVerificationLog_success() {
        // given
        val userVerificationLogDto = UserVerificationLogDto.create(
            userId = testUserId,
            version = testVersion,
            os = testOs,
            mode = testMode,
            hash = testHash
        )

        // when
        // userVerificationLog 저장
        val savedDto: UserVerificationLogDto = userVerificationRepository.saveUserVerificationLog(userVerificationLogDto)

        // then
        savedDto.id shouldNotBe null
        savedDto.userId shouldBe testUserId
        savedDto.version shouldBe testVersion
        savedDto.os shouldBe testOs
        savedDto.mode shouldBe testMode
        savedDto.hash shouldBe testHash
        savedDto.requestedAt shouldNotBe null
    }
}
