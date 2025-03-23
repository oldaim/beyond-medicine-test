package org.beyondmedicine.beyondmedicinetest.user.repository

import org.beyondmedicine.beyondmedicinetest.user.domain.AppVersion
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AppVersionRepository : JpaRepository<AppVersion, Long> {

    fun findAppVersionByOsAndMode(os: String, mode: String): AppVersion?

}