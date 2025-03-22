package org.beyondmedicine.beyondmedicinetest.domain.accesscode

import jakarta.persistence.*
import org.beyondmedicine.beyondmedicinetest.domain.constant.AccessCodeStatus
import java.time.LocalDateTime

@Entity
@Table(name = "access_code_history")
class AccessCodeHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val hospitalId: String,

    @Column(nullable = false, length = 8, unique = true)
    val accessCode: String,

    @Column(nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: AccessCodeStatus = AccessCodeStatus.ACTIVE
) {


    fun expire() { // 처방코드 이력의 상태 변경 메서드
        this.status = AccessCodeStatus.EXPIRED
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AccessCodeHistory

        return id == other.id
    }

    override fun hashCode(): Int {
        return id?.hashCode() ?: 0
    }
}
