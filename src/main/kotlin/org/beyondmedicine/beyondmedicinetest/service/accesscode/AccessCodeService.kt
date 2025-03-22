package org.beyondmedicine.beyondmedicinetest.service.accesscode

import org.beyondmedicine.beyondmedicinetest.dto.AccessCodeInfoDto
import org.beyondmedicine.beyondmedicinetest.dto.AccessCodeRequestDto
import org.beyondmedicine.beyondmedicinetest.dto.AccessCodeResponseDto

interface AccessCodeService {

    fun createAccessCodeHistory(requestDto: AccessCodeRequestDto): AccessCodeResponseDto
    fun isAccessCodeValid(accessCode: String): Boolean
    fun findHistoryByAccessCode(accessCode: String): AccessCodeInfoDto

}