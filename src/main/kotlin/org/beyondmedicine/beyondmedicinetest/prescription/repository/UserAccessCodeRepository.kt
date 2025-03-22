package org.beyondmedicine.beyondmedicinetest.prescription.repository

import org.beyondmedicine.beyondmedicinetest.prescription.domain.UserAccessCode
import org.beyondmedicine.beyondmedicinetest.prescription.domain.constant.AccessCodeStatus
import org.springframework.data.jpa.repository.JpaRepository

interface UserAccessCodeRepository: JpaRepository<UserAccessCode, Long> {

    fun findByUserIdAndStatus(userId: String, status: AccessCodeStatus): UserAccessCode?

}