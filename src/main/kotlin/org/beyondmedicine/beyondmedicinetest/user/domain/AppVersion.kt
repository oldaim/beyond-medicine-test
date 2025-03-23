package org.beyondmedicine.beyondmedicinetest.user.domain

import jakarta.persistence.*
import org.beyondmedicine.beyondmedicinetest.user.dto.AppVersionDto

@Entity
@Table(name = "app_version")
class AppVersion (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "latest_version", nullable = false, length = 30)
    val latestVersion: String,

    @Column(name = "minimum_version", nullable = false, length = 30)
    val minimumVersion: String,

    @Column(nullable = false, length = 30)
    val os: String,

    @Column(nullable = false, length = 30)
    val mode: String,

    @Column(nullable = false, length = 100)
    val hash: String
){

    fun toDto(): AppVersionDto {
        return AppVersionDto(
            id = this.id,
            latestVersion = this.latestVersion,
            minimumVersion = this.minimumVersion,
            os = this.os,
            mode = this.mode,
            hash = this.hash
        )
    }
}