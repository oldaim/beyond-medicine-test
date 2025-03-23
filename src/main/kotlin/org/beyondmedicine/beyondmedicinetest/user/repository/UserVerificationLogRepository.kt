package org.beyondmedicine.beyondmedicinetest.user.repository

import org.beyondmedicine.beyondmedicinetest.user.domain.UserVerificationLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserVerificationLogRepository: JpaRepository<UserVerificationLog, Long> {
}