package org.beyondmedicine.beyondmedicinetest.prescription.repository

import org.beyondmedicine.beyondmedicinetest.prescription.domain.AccessCodeHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AccessCodeHistoryRepository: JpaRepository<AccessCodeHistory, Long> {

    fun existsByAccessCode(accessCode: String): Boolean
    fun findByAccessCode(accessCode: String): AccessCodeHistory?
}