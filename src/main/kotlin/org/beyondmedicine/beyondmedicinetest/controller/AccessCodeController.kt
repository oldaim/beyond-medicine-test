package org.beyondmedicine.beyondmedicinetest.controller

import org.beyondmedicine.beyondmedicinetest.dto.CreateAccessCodeRequestDto
import org.beyondmedicine.beyondmedicinetest.dto.CreateAccessCodeResponseDto
import org.beyondmedicine.beyondmedicinetest.dto.ApiResponse
import org.beyondmedicine.beyondmedicinetest.service.accesscode.AccessCodeService
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
        @RequestBody requestDto: CreateAccessCodeRequestDto
    ): ApiResponse<CreateAccessCodeResponseDto> {

        val result: CreateAccessCodeResponseDto = accessCodeService.createAccessCodeHistory(requestDto)

        return ApiResponse.ok(result)
    }
}