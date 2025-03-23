package org.beyondmedicine.beyondmedicinetest.user.service

import org.beyondmedicine.beyondmedicinetest.user.constants.UpdateStatus
import org.beyondmedicine.beyondmedicinetest.user.dto.UserVerificationRequestDto

interface UserVerificationService {

    fun verifyUserRequest(requestDto: UserVerificationRequestDto): UpdateStatus

}