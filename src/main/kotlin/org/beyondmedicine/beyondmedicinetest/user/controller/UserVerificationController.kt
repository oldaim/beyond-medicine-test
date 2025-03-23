package org.beyondmedicine.beyondmedicinetest.user.controller

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.beyondmedicine.beyondmedicinetest.common.dto.ApiResponse
import org.beyondmedicine.beyondmedicinetest.user.constants.UpdateStatus
import org.beyondmedicine.beyondmedicinetest.user.dto.UserVerificationRequestDto
import org.beyondmedicine.beyondmedicinetest.user.service.UserVerificationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/user/verification")
class UserVerificationController(
    private val userVerificationService: UserVerificationService
) {


    @GetMapping("/request")
    fun verifyUserRequest(
        @RequestParam("userId", required = true) userId: String,
        @RequestParam("version", required = true) version: String,
        @RequestParam("os", required = true) os: String,
        @RequestParam("mode", required = true) mode: String,
        @RequestParam("hash", required = true) hash: String
    ): ResponseEntity<ApiResponse<UpdateStatus>> {

        val requestDto = UserVerificationRequestDto.create(
            userId = userId,
            version = version,
            os = os,
            mode = mode,
            hash = hash
        )

        userVerificationService.saveUserVerificationLog(requestDto) // 사용자 검증과 상관 없이 로그 저장

        return when(val result: UpdateStatus = userVerificationService.verifyUserRequest(requestDto)) {
            UpdateStatus.FORCE_UPDATE_REQUIRED -> ApiResponse.upgradeRequired(result)
            UpdateStatus.UPDATE_REQUIRED -> ApiResponse.ok(result)
            UpdateStatus.NO_UPDATE_REQUIRED -> ApiResponse.ok(result)
        }

    }

}