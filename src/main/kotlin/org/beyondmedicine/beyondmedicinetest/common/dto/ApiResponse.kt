package org.beyondmedicine.beyondmedicinetest.common.dto

import org.springframework.http.HttpStatus

data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T? = null
){
    companion object{

        fun <T> ok(data: T? = null): ApiResponse<T> {

            val code: Int = HttpStatus.OK.value()

            val message: String = HttpStatus.OK.reasonPhrase

            return ApiResponse(code, message, data)
        }

        fun <T> badRequest(message: String): ApiResponse<T> {

            val code: Int = HttpStatus.BAD_REQUEST.value()

            return ApiResponse(code, message)
        }

        fun <T> internalServerError(message: String): ApiResponse<T> {

            val code: Int = HttpStatus.INTERNAL_SERVER_ERROR.value()

            return ApiResponse(code, message)
        }

        fun <T> upgradeRequired(data: T? = null): ApiResponse<T> {

            val code: Int = HttpStatus.UPGRADE_REQUIRED.value()

            val message: String = HttpStatus.UPGRADE_REQUIRED.reasonPhrase

            return ApiResponse(code, message, data)
        }

    }
}
