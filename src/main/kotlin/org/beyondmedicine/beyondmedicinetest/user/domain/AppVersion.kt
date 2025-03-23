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

    @Column(nullable = false, length = 20)
    val os: String,

    @Column(nullable = false, length = 10)
    val mode: String,

    @Column(nullable = false)
    val hash: String
){

    companion object{

        fun toDto(entity: AppVersion): AppVersionDto {
            return AppVersionDto(
                latestVersion = entity.latestVersion,
                minimumVersion = entity.minimumVersion,
                hash = entity.hash
            )
        }

    }


}