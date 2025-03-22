package org.beyondmedicine.beyondmedicinetest.user.repository

import org.beyondmedicine.beyondmedicinetest.user.domain.AppVersion
import org.springframework.stereotype.Repository

@Repository
interface AppVersionRepository {

    fun findAppVersionByOsAndMode(os: String, mode: String): AppVersion?

}