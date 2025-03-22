package org.beyondmedicine.beyondmedicinetest.service.accesscode

import org.beyondmedicine.beyondmedicinetest.dto.ActivateAccessCodeRequestDto
import org.beyondmedicine.beyondmedicinetest.dto.CreateAccessCodeRequestDto
import org.beyondmedicine.beyondmedicinetest.dto.CreateAccessCodeResponseDto

interface AccessCodeService {

    fun createAccessCodeHistory(requestDto: CreateAccessCodeRequestDto): CreateAccessCodeResponseDto

    fun activateAccessCode(requestDto: ActivateAccessCodeRequestDto)

}