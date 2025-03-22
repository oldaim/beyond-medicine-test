package org.beyondmedicine.beyondmedicinetest.repository.accesscode

import org.beyondmedicine.beyondmedicinetest.domain.accesscode.UserAccessCode
import org.beyondmedicine.beyondmedicinetest.domain.constant.AccessCodeStatus
import org.springframework.data.jpa.repository.JpaRepository

interface UserAccessCodeRepository: JpaRepository<UserAccessCode, Long> {

    fun findByUserIdAndStatus(userId: String, status: AccessCodeStatus): UserAccessCode?

}