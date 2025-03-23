package org.beyondmedicine.beyondmedicinetest.prescription.repository

import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.beyondmedicine.beyondmedicinetest.config.JpaRepositoryTest
import org.beyondmedicine.beyondmedicinetest.prescription.domain.constant.AccessCodeStatus
import org.beyondmedicine.beyondmedicinetest.prescription.dto.AccessCodeHistoryDto
import org.beyondmedicine.beyondmedicinetest.prescription.dto.UserAccessCodeDto
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource
import java.time.LocalDateTime

@JpaRepositoryTest
@Import(AccessCodeRepositoryImpl::class)
@TestPropertySource(properties = ["spring.config.location=classpath:application-test.yml"])
class AccessCodeRepositoryTest {

    @Autowired
    private lateinit var accessCodeRepository: AccessCodeRepository

    @Autowired
    private lateinit var accessCodeHistoryRepository: AccessCodeHistoryRepository

    @Autowired
    private lateinit var userAccessCodeRepository: UserAccessCodeRepository

    private val testAccessCode = "TEST1234"
    private val testUserId = "test-user"
    private val testHospitalId = "test-hospital"

    @BeforeEach
    fun setup() {
        accessCodeHistoryRepository.deleteAll()
        userAccessCodeRepository.deleteAll()
    }

    @Test
    @DisplayName("AccessCodeHistory 저장 성공")
    fun saveAccessCodeHistory() {
        // given
        val accessCodeHistoryDto = AccessCodeHistoryDto.create(
            hospitalId = testHospitalId,
            accessCode = testAccessCode
        )

        // when
        val savedDto = accessCodeRepository.saveAccessCodeHistory(accessCodeHistoryDto)

        // then
        savedDto.id shouldNotBe null
        savedDto.hospitalId shouldBe testHospitalId
        savedDto.accessCode shouldBe testAccessCode

    }

    @Test
    @DisplayName("AccessCodeHistory 조회 성공")
    fun findAccessCodeHistory() {

        //given
        val accessCodeHistoryDto = AccessCodeHistoryDto.create(
            hospitalId = testHospitalId,
            accessCode = testAccessCode
        )

        //when
        accessCodeRepository.saveAccessCodeHistory(accessCodeHistoryDto)

        val foundDto = accessCodeRepository.findByAccessCode(testAccessCode)

        //then
        foundDto shouldNotBe null
        foundDto?.id shouldNotBe null
        foundDto?.hospitalId shouldBe testHospitalId
        foundDto?.accessCode shouldBe testAccessCode

    }


    @Test
    @DisplayName("AccessCode 가 존재하는지 유무 파악 성공")
    fun existsByAccessCode() {
        // given
        val accessCodeHistoryDto = AccessCodeHistoryDto.create(
            hospitalId = testHospitalId,
            accessCode = testAccessCode
        )

        //when
        accessCodeRepository.saveAccessCodeHistory(accessCodeHistoryDto)

        //then
        accessCodeRepository.existsByAccessCode(testAccessCode) shouldBe true
        accessCodeRepository.existsByAccessCode("1221111") shouldBe false
    }

    @Test
    @DisplayName("UserAccessCode 저장 성공")
    fun saveUserAccessCode() {
        // given
        val userAccessCodeDto = UserAccessCodeDto(
            userId = testUserId,
            accessCode = testAccessCode,
            status = AccessCodeStatus.ACTIVE,
            expiresAt = LocalDateTime.now().plusDays(43)
        )

        // when
        val savedDto: UserAccessCodeDto = accessCodeRepository.saveUserAccessCode(userAccessCodeDto)

        // then
        savedDto.id shouldNotBe null
        savedDto.userId shouldBe testUserId
        savedDto.accessCode shouldBe testAccessCode
        savedDto.status shouldBe AccessCodeStatus.ACTIVE
    }

    @Test
    @DisplayName("UserAccessCode 에서 활성화 코드 조회 성공")
    fun findUserAccessCodeStatus() {
        // given
        val userAccessCode = UserAccessCodeDto.activateAccessCode(testUserId, testAccessCode)

        // when
        accessCodeRepository.saveUserAccessCode(userAccessCode)

        val savedUserAccessCode: UserAccessCodeDto? = accessCodeRepository.findUserAccessCodeByUserIdAndStatus(testUserId, AccessCodeStatus.ACTIVE)

        // then
        savedUserAccessCode?.userId shouldBe testUserId
        savedUserAccessCode?.accessCode shouldBe testAccessCode
        savedUserAccessCode?.status shouldBe AccessCodeStatus.ACTIVE
        savedUserAccessCode?.activatedAt shouldNotBe null

        val testExpires = savedUserAccessCode!!.activatedAt
            .plusDays(43)
            .withHour(0)
            .withMinute(0)
            .withSecond(0)
            .withNano(0)

        savedUserAccessCode.expiresAt shouldBe testExpires
    }

    @Test
    @DisplayName("UserAccessCode 에서 활성화 코드 조회 - 활성화 코드가 없는 경우")
    fun findUserAccessCodeStatusFail() {
        // given
        val userAccessCode = UserAccessCodeDto.activateAccessCode(testUserId, testAccessCode)

        // when
        accessCodeRepository.saveUserAccessCode(userAccessCode)

        val savedUserAccessCode: UserAccessCodeDto? = accessCodeRepository.findUserAccessCodeByUserIdAndStatus("12133", AccessCodeStatus.ACTIVE)

        // then
        savedUserAccessCode shouldBe null

    }
}