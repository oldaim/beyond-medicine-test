package org.beyondmedicine.beyondmedicinetest.common.exception

import org.beyondmedicine.beyondmedicinetest.common.dto.ApiResponse
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.lang.Exception

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(AccessCodeRetryFailException::class, Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleInternalServerErrorException(e: AccessCodeRetryFailException): ResponseEntity<ApiResponse<Void>> {
        return ApiResponse.internalServerError(e.message?:"")
    }

    @ExceptionHandler(UserAccessCodeNotActivatedException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleForbiddenException(e: UserAccessCodeNotActivatedException): ResponseEntity<ApiResponse<Void>> {
        return ApiResponse.forbidden(e.message?:"")
    }

    @ExceptionHandler(AccessCodeAlreadyActivatedException::class, DataIntegrityViolationException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleConflictException(e: AccessCodeAlreadyActivatedException): ResponseEntity<ApiResponse<Void>> {
        return ApiResponse.conflict(e.message?:"")
    }

    @ExceptionHandler(IllegalArgumentException::class, BindException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequestException(e: IllegalArgumentException): ResponseEntity<ApiResponse<Void>> {
        return ApiResponse.badRequest(e.message?:"")
    }

    @ExceptionHandler(NoSuchElementException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFoundException(e: NoSuchElementException): ResponseEntity<ApiResponse<Void>> {
        return ApiResponse.notFound(e.message?:"")
    }

}