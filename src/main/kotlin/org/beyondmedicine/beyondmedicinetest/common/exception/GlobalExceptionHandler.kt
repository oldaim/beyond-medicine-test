package org.beyondmedicine.beyondmedicinetest.common.exception

import org.beyondmedicine.beyondmedicinetest.common.dto.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    companion object{
        private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    }

    @ExceptionHandler(AccessCodeRetryFailException::class, Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleInternalServerErrorException(e: Exception): ResponseEntity<ApiResponse<Void>> {
        logger.error("Internal Server Error", e)
        return ApiResponse.internalServerError()
    }

    @ExceptionHandler(UserAccessCodeNotActivatedException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleForbiddenException(e: Exception): ResponseEntity<ApiResponse<Void>> {
        logger.error("Forbidden", e)
        return ApiResponse.forbidden()
    }

    @ExceptionHandler(AccessCodeAlreadyActivatedException::class, DataIntegrityViolationException::class)
    @ResponseStatus(HttpStatus.CONFLICT)
    fun handleConflictException(e: Exception): ResponseEntity<ApiResponse<Void>> {
        logger.error("Conflict", e)
        return ApiResponse.conflict()
    }

    @ExceptionHandler(IllegalArgumentException::class, BindException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleBadRequestException(e: Exception): ResponseEntity<ApiResponse<Void>> {
        logger.error("${e.message}")
        return ApiResponse.badRequest()
    }

    @ExceptionHandler(NoSuchElementException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFoundException(e: Exception): ResponseEntity<ApiResponse<Void>> {
        logger.error("Not Found", e)
        return ApiResponse.notFound()
    }

}