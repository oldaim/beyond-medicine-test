package org.beyondmedicine.beyondmedicinetest.user.repository

import org.beyondmedicine.beyondmedicinetest.user.dto.AppVersionDto
import org.beyondmedicine.beyondmedicinetest.user.dto.UserVerificationLogDto

interface UserVerificationRepository {
    
    fun findAppVersionByOsAndMode(os: String, mode: String): AppVersionDto?
    
    fun saveUserVerificationLog(userVerificationLogDto: UserVerificationLogDto): UserVerificationLogDto
    
}