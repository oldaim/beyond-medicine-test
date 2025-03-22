package org.beyondmedicine.beyondmedicinetest.user.domain

import jakarta.persistence.*
import org.beyondmedicine.beyondmedicinetest.user.dto.AppVersionDto

@Entity
@Table(name = "app_version")
class AppVersion (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val os: String,

    @Column(nullable = false)
    val mode: String,

    @Column(name = "latest_version", nullable = false)
    val latestVersion: String,

    @Column(name = "minimum_version", nullable = false)
    val minimumVersion: String,

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