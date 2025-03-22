package org.beyondmedicine.beyondmedicinetest.repository.accesscode

import org.beyondmedicine.beyondmedicinetest.domain.accesscode.AccessCodeHistory
import org.springframework.data.jpa.repository.JpaRepository

interface AccessCodeRepository: JpaRepository<AccessCodeHistory, Long> {

    fun existsByAccessCode(accessCode: String): Boolean
    fun findByAccessCode(accessCode: String): AccessCodeHistory?
}