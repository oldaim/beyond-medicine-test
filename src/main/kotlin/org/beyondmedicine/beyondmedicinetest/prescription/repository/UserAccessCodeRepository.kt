package org.beyondmedicine.beyondmedicinetest.prescription.repository

import org.beyondmedicine.beyondmedicinetest.prescription.domain.UserAccessCode
import org.beyondmedicine.beyondmedicinetest.prescription.domain.constant.AccessCodeStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserAccessCodeRepository: JpaRepository<UserAccessCode, Long> {

    fun findByUserIdAndStatus(userId: String, status: AccessCodeStatus): UserAccessCode?

}