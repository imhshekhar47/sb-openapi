package org.hshekhar.api.error

import org.springframework.http.HttpStatus

/**
 * @created 1/5/2023'T'12:11 PM
 * @author Himanshu Shekhar (609080540)
 **/

sealed class ApiException(
    message: String,
    cause: Throwable? = null): Exception(message, cause)


class BadInputException(
    message: String = "Bad request",
    cause: Throwable? = null): ApiException(message = message, cause = cause)

class NotFoundException(
    message: String = "No such element found",
    cause: Throwable? = null): ApiException(message = message, cause = cause)

class InternalException(
    message: String = "Something went wrong",
    cause: Throwable? = null): ApiException(message = message, cause = cause)

