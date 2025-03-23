package org.beyondmedicine.beyondmedicinetest.prescription.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.beyondmedicine.beyondmedicinetest.common.dto.ApiResponse
import org.beyondmedicine.beyondmedicinetest.prescription.dto.ActivateAccessCodeRequestDto
import org.beyondmedicine.beyondmedicinetest.prescription.dto.ActivateAccessCodeResponseDto
import org.beyondmedicine.beyondmedicinetest.prescription.dto.CreateAccessCodeRequestDto
import org.beyondmedicine.beyondmedicinetest.prescription.dto.CreateAccessCodeResponseDto
import org.beyondmedicine.beyondmedicinetest.prescription.service.AccessCodeService
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
@Tag(name = "의료진용 처방코드 API", description = "처방코드 생성 및 활성화 관련 API")
class AccessCodeController(
    private val accessCodeService: AccessCodeService
) {

    @Operation(
        summary = "처방코드 생성 API",
        description = "새로운 처방코드 생성 및 처방코드 생성 이력 저장."
    )
    @ApiResponses(
        value = [
            SwaggerResponse(
                responseCode = "201",
                description = "처방코드 생성 성공",
                content = [Content(schema = Schema(implementation = CreateAccessCodeResponseDto::class))]
            ),
            SwaggerResponse(
                responseCode = "400",
                description = "잘못된 요청 형식"
            ),
            SwaggerResponse(
                responseCode = "500",
                description = "처방코드 재시도 로직 횟수 초과"
            )
        ]
    )
    @PostMapping("/access-code")
    fun createAccessCode(
        @Parameter(description = "병원 ID", required = true)
        @Validated @RequestBody requestDto: CreateAccessCodeRequestDto
    ): ResponseEntity<ApiResponse<CreateAccessCodeResponseDto>> {

        val result: CreateAccessCodeResponseDto = accessCodeService.createAccessCodeHistory(requestDto)

        return ApiResponse.created(result)
    }

    @Operation(
        summary = "처방코드 활성화 API",
        description = "사용자 ID와 처방코드를 이용하여 처방코드를 활성화."
    )
    @ApiResponses(
        value = [
            SwaggerResponse(
                responseCode = "201",
                description = "처방코드 활성화 성공",
                content = [Content(schema = Schema(implementation = ActivateAccessCodeResponseDto::class))]
            ),
            SwaggerResponse(
                responseCode = "400",
                description = "잘못된 요청 파라미터 또는 이미 활성화된 처방코드가 있음"
            ),
            SwaggerResponse(
                responseCode = "409",
                description = "이미 활성화된 처방코드가 있음"
            ),
            SwaggerResponse(
                responseCode = "404",
                description = "존재하지 않는 처방코드"
            )
        ]
    )
    @PostMapping("/access-code-activations")
    fun activateAccessCode(
        @Parameter(description = "사용자 ID 및 처방코드 정보", required = true)
        @Validated @RequestBody requestDto: ActivateAccessCodeRequestDto
    ): ResponseEntity<ApiResponse<ActivateAccessCodeResponseDto>> {

        val result: ActivateAccessCodeResponseDto = accessCodeService.activateAccessCode(requestDto)

        return ApiResponse.created(result)
    }
}