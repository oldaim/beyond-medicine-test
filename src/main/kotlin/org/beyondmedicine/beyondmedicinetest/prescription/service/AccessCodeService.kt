package org.beyondmedicine.beyondmedicinetest.prescription.service

import org.beyondmedicine.beyondmedicinetest.prescription.dto.ActivateAccessCodeRequestDto
import org.beyondmedicine.beyondmedicinetest.prescription.dto.CreateAccessCodeRequestDto
import org.beyondmedicine.beyondmedicinetest.prescription.dto.CreateAccessCodeResponseDto

interface AccessCodeService {

    fun createAccessCodeHistory(requestDto: CreateAccessCodeRequestDto): CreateAccessCodeResponseDto

    fun activateAccessCode(requestDto: ActivateAccessCodeRequestDto)

}