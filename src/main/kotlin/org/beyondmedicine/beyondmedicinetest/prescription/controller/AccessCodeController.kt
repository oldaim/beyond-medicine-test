package org.beyondmedicine.beyondmedicinetest.prescription.controller

import org.beyondmedicine.beyondmedicinetest.common.dto.ApiResponse
import org.beyondmedicine.beyondmedicinetest.prescription.dto.ActivateAccessCodeRequestDto
import org.beyondmedicine.beyondmedicinetest.prescription.dto.ActivateAccessCodeResponseDto
import org.beyondmedicine.beyondmedicinetest.prescription.dto.CreateAccessCodeRequestDto
import org.beyondmedicine.beyondmedicinetest.prescription.dto.CreateAccessCodeResponseDto
import org.beyondmedicine.beyondmedicinetest.prescription.service.AccessCodeService
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1")
class AccessCodeController(
    private val accessCodeService: AccessCodeService
) {

    @PostMapping("/access-code")
    fun createAccessCode(
        @Validated @RequestBody requestDto: CreateAccessCodeRequestDto
    ): ApiResponse<CreateAccessCodeResponseDto> {

        val result: CreateAccessCodeResponseDto = accessCodeService.createAccessCodeHistory(requestDto)

        return ApiResponse.ok(result)
    }

    @PostMapping("/access-code-activations")
    fun activateAccessCode(
        @Validated @RequestBody requestDto: ActivateAccessCodeRequestDto
    ): ApiResponse<ActivateAccessCodeResponseDto> {

        val result: ActivateAccessCodeResponseDto = accessCodeService.activateAccessCode(requestDto)

        return ApiResponse.ok(result)
    }
}