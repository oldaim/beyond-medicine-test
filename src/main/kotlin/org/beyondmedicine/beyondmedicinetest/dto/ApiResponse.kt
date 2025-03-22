package org.beyondmedicine.beyondmedicinetest.dto

import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode

data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T? = null
){
    companion object{

        fun <T> ok(data: T): ApiResponse<T> {

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
    }
}
