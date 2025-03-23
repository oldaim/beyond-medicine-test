package org.beyondmedicine.beyondmedicinetest.user.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
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
@Tag(name = "사용자용 검증 API", description = "사용자 검증 및 업데이트 상태 확인 API")
class UserVerificationController(
    private val userVerificationService: UserVerificationService
) {

    @Operation(
        summary = "사용자 검증 API",
        description = "사용자 ID와 버전 정보, 운영체제, 모드 및 해시를 입력받아 사용자 검증과 업데이트 상태를 확인."
    )
    @ApiResponses(
        value = [
            SwaggerResponse(
                responseCode = "200",
                description = "사용자 검증 성공 - UpdateStatus.NO_UPDATE_REQUIRED: 업데이트 필요 없음, UpdateStatus.UPDATE_REQUIRED: 업데이트 필요",
                content = [Content(schema = Schema(implementation = UpdateStatus::class))]

            ),
            SwaggerResponse(
                responseCode = "426",
                description = "UpdateStatus.FORCE_UPDATE_REQUIRED 강제 업데이트 필요",
                content = [Content(schema = Schema(implementation = UpdateStatus::class))]
            ),
            SwaggerResponse(
                responseCode = "400",
                description = "잘못된 요청 형식 또는 유효하지 않은 요청 해시"
            ),
            SwaggerResponse(
                responseCode = "403",
                description = "처방 코드가 등록되지 않았거나 만료된 상태"
            )
        ]
    )
    @GetMapping("/request")
    fun verifyUserRequest(
        @Parameter(description = "UUID 형식의 사용자 ID", required = true) 
        @RequestParam("userId", required = true) userId: String,
        
        @Parameter(description = "Major.Minor.Patch 버전 (ex: 0.1.1, 0.1.2-alpha.1, 0.1.2-beta.1)", required = true)
        @RequestParam("version", required = true) version: String,
        
        @Parameter(description = "운영체제 (Android 또는 iOS, 대소문자 구분 없음)", required = true)
        @RequestParam("os", required = true) os: String,
        
        @Parameter(description = "모드 (debug 또는 release, 대소문자 구분 없음)", required = true)
        @RequestParam("mode", required = true) mode: String,
        
        @Parameter(description = "OS와 모드에 따른 해시값", required = true)
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