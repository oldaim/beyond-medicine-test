package org.beyondmedicine.beyondmedicinetest.common.exception

class AccessCodeRetryFailException(message: String): RuntimeException(message)

class UserAccessCodeNotActivatedException(message: String) : RuntimeException(message)

class AccessCodeAlreadyActivatedException(message: String) : RuntimeException(message)