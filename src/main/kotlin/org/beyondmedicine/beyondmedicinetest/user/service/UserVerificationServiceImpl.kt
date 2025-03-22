package org.beyondmedicine.beyondmedicinetest.user.service

import org.beyondmedicine.beyondmedicinetest.user.constants.RequestStatus
import org.beyondmedicine.beyondmedicinetest.user.dto.UserVerificationRequestDto
import org.beyondmedicine.beyondmedicinetest.user.repository.UserVerificationLogRepository
import java.util.*

class UserVerificationServiceImpl(
    private val userVerificationLogRepository: UserVerificationLogRepository

) : UserVerificationService {

    override fun verifyUserRequest(requestDto: UserVerificationRequestDto): RequestStatus {

        if (!requestDto.isDtoValid()) throw IllegalArgumentException("dto parameters are not valid")

        TODO()


    }


}