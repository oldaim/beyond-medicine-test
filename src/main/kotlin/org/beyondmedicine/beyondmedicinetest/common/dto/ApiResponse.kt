package org.beyondmedicine.beyondmedicinetest.common.dto

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class ApiResponse<T>(
    val code: Int,
    val message: String,
    val data: T? = null
){
    companion object{

        fun <T> ok(data: T? = null): ResponseEntity<ApiResponse<T>> {
            val code: Int = HttpStatus.OK.value()
            val message: String = HttpStatus.OK.reasonPhrase
            val response = ApiResponse(code, message, data)
            return ResponseEntity.status(HttpStatus.OK).body(response)
        }

        fun <T> created(data: T? = null): ResponseEntity<ApiResponse<T>> {
            val code: Int = HttpStatus.CREATED.value()
            val message: String = HttpStatus.CREATED.reasonPhrase
            val response = ApiResponse(code, message, data)
            return ResponseEntity.status(HttpStatus.CREATED).body(response)
        }

        fun <T> badRequest(message: String, data: T? = null): ResponseEntity<ApiResponse<T>> {
            val code: Int = HttpStatus.BAD_REQUEST.value()
            val response = ApiResponse(code, message, data)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
        }

        fun <T> forbidden(message: String, data: T? = null): ResponseEntity<ApiResponse<T>> {
            val code: Int = HttpStatus.FORBIDDEN.value()
            val response = ApiResponse(code, message, data)
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response)
        }

        fun <T> notFound(message: String, data: T? = null): ResponseEntity<ApiResponse<T>> {
            val code: Int = HttpStatus.NOT_FOUND.value()
            val response = ApiResponse(code, message, data)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
        }

        fun <T> conflict(message: String, data: T? = null): ResponseEntity<ApiResponse<T>> {
            val code: Int = HttpStatus.CONFLICT.value()
            val response = ApiResponse(code, message, data)
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response)
        }

        fun <T> internalServerError(message: String, data: T? = null): ResponseEntity<ApiResponse<T>> {
            val code: Int = HttpStatus.INTERNAL_SERVER_ERROR.value()
            val response = ApiResponse(code, message, data)
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
        }

        fun <T> upgradeRequired(data: T? = null): ResponseEntity<ApiResponse<T>> {
            val code: Int = HttpStatus.UPGRADE_REQUIRED.value()
            val message: String = HttpStatus.UPGRADE_REQUIRED.reasonPhrase
            val response = ApiResponse(code, message, data)
            return ResponseEntity.status(HttpStatus.UPGRADE_REQUIRED).body(response)
        }
    }
}
