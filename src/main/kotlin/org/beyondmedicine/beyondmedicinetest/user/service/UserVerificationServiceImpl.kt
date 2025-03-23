package org.beyondmedicine.beyondmedicinetest.user.service

import org.beyondmedicine.beyondmedicinetest.prescription.service.AccessCodeService
import org.beyondmedicine.beyondmedicinetest.user.constants.UpdateStatus
import org.beyondmedicine.beyondmedicinetest.user.dto.AppVersionDto
import org.beyondmedicine.beyondmedicinetest.user.dto.UserVerificationLogDto
import org.beyondmedicine.beyondmedicinetest.user.dto.UserVerificationRequestDto
import org.beyondmedicine.beyondmedicinetest.user.repository.UserVerificationRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.util.*

@Service
class UserVerificationServiceImpl(
    private val userVerificationRepository: UserVerificationRepository,
    private val accessCodeService: AccessCodeService,
    @Value("\${verification.hash.secret}")
    private val requestHashSecret: String,
    private val messageDigest: MessageDigest
) : UserVerificationService {

    @Transactional(readOnly = true)
    override fun verifyUserRequest(requestDto: UserVerificationRequestDto): UpdateStatus {

        if (!requestDto.isDtoValid()) throw IllegalArgumentException("dto parameters are not valid")

        val os: String = requestDto.os
        val mode: String = requestDto.mode
        val userId: String = requestDto.userId
        val version = requestDto.version

        val appVersionDto: AppVersionDto = findAppVersion(requestDto.os, requestDto.mode)

        // hash 검증 과정
        val isHashMatched: Boolean = validateHash(requestHash = requestDto.hash, entityHash = appVersionDto.hash, os = os, mode = mode)

        if (!isHashMatched) throw IllegalArgumentException("hash is not matched")

        // 사용자 검증 과정
        if (!accessCodeService.isUserAccessCodeActivated(userId)) throw IllegalArgumentException("user access code is not activated")

        // 버전 검증 과정
        return validateVersion(version, appVersionDto.latestVersion, appVersionDto.minimumVersion)

    }

    @Transactional
    override fun saveUserVerificationLog(requestDto: UserVerificationRequestDto) {

       val userVerificationLogDto = UserVerificationLogDto.create(
            userId = requestDto.userId,
            version = requestDto.os,
            os = requestDto.mode,
            mode = requestDto.version,
            hash = requestDto.hash
        )

        userVerificationRepository.saveUserVerificationLog(userVerificationLogDto)

    }

    private fun validateVersion(version: String, latestVersion: String, minimumVersion: String): UpdateStatus {
        return when {
            // 현재 버전이 최소 버전보다 낮은 경우
            compareVersion(version, minimumVersion) < 0 -> UpdateStatus.FORCE_UPDATE_REQUIRED
            // 현재 버전이 최소 버전 보단 같거나 높고, 최신 버전보다 낮은 경우
            compareVersion(version, latestVersion) < 0 -> UpdateStatus.UPDATE_REQUIRED
            // 최신버전과 동일 한 경우
            else -> UpdateStatus.NO_UPDATE_REQUIRED
        }
    }

    // 버전 비교 로직 x.y.z-[alpha|beta].n 형식을 최대로 가정하고 비교
    private fun compareVersion(currentVersion: String, compareVersion: String): Int {
        // main, sub 분리
        val currentStList: List<String> = currentVersion.split("-")
        val compareStList: List<String> = compareVersion.split("-")

        //main major, minor, patch 분리
        val currentMainList: List<String> = currentStList[0].split(".")
        val compareMainList: List<String> = compareStList[0].split(".")

        // main 비교
        for (i in 0..2) {
            val currentMain: Int = currentMainList[i].toInt()
            val compareMain: Int = compareMainList[i].toInt()

            if (currentMain != compareMain) return currentMain.compareTo(compareMain)
        }

        // sub 비교 - alpha, beta 유무로 비교 (1.0.0 > 1.0.0-alpha.1)
        if (currentStList.size == 1 && compareStList.size == 1) return 0
        if (currentStList.size == 1) return 1
        if (compareStList.size == 1) return -1

        val currentPreType: String = currentStList[1].split(".")[0]
        val comparePreType: String = compareStList[1].split(".")[0]

        // alpha, beta 프리 릴리스 버전으로 비교
        if (currentPreType != comparePreType) {
            if (currentPreType == "alpha" && comparePreType == "beta") return -1
            if (currentPreType == "beta" && comparePreType == "alpha") return 1
        }

        val currentPreList: List<String> = currentStList[1].split(".")
        val comparePreList: List<String> = compareStList[1].split(".")

        // 프리 릴리스 버전이 같은 경우에 숫자 비교 (ex. alpha.1, alpha.2)
        if (currentPreList[0] == comparePreList[0]) {
            // 하나만 숫자가 있는 경우
            if (currentPreList.size == 1 && comparePreList.size > 1) return -1
            if (currentPreList.size > 1 && comparePreList.size == 1) return 1

            // 둘 다 숫자가 있는 경우
            if (currentPreList.size > 1 && comparePreList.size > 1) {
                val currentPreNum = currentPreList[1].toInt()
                val comparePreNum = comparePreList[1].toInt()
                return currentPreNum.compareTo(comparePreNum)
            }

            // 둘 다 숫자가 없는 경우
            return 0
        }

        return 0
    }

    // hash 검증 로직
    // 요청된 hash와 entity hash, os, mode로 생성한 hash가 모두 일치하는지 확인
    private fun validateHash(
        requestHash: String,
        entityHash: String,
        os: String,
        mode: String
    ): Boolean {
        val hashString: String = getHashString(os, mode)

        return hashString == requestHash && hashString == entityHash
    }

    // os와 mode로 생성한 hash를 반환
    private fun getHashString(os: String, mode: String): String {
        val hashString = "$os$mode$requestHashSecret"
        val hashBytes: ByteArray = messageDigest.digest(hashString.toByteArray(Charsets.UTF_8))
        return Base64.getEncoder().encodeToString(hashBytes)
    }

    // os와 mode에 해당하는 AppVersion을 찾아서 AppVersionDto로 변환
    private fun findAppVersion(os: String, mode: String): AppVersionDto {
        return userVerificationRepository.findAppVersionByOsAndMode(os, mode)
            ?: throw IllegalArgumentException("os and mode not found or database error")
    }
}