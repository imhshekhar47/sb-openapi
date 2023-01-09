package org.hshekhar.api.config

import org.hshekhar.api.error.ApiException
import org.hshekhar.api.error.BadInputException
import org.hshekhar.api.error.InternalException
import org.hshekhar.api.error.NotFoundException
import org.hshekhar.model.ApiError
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import javax.servlet.http.HttpServletRequest

/**
 * @created 1/5/2023'T'12:18 PM
 * @author Himanshu Shekhar (609080540)
 **/

@ControllerAdvice
class ExceptionHandlerAdvice {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(ExceptionHandlerAdvice::class.java)
    }

    private fun buildErrorResponse(
        httpStatus: HttpStatus,
        message: String,
        httpMethod: String,
        httpUrl: String
    ): ResponseEntity<ApiError> {
        LOGGER.error("failed while $httpMethod $httpUrl, details: $message")
        return ResponseEntity(
            ApiError(
                code = httpStatus.value().toString(),
                message = "Error while $httpMethod $httpUrl, details: $message"
            ),
            httpStatus
        )
    }


    @ExceptionHandler(value = [ApiException::class])
    fun handleApiException(
        ex: ApiException,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> {
        return buildErrorResponse(
            httpStatus = when (ex) {
                is BadInputException -> HttpStatus.BAD_REQUEST
                is NotFoundException -> HttpStatus.NOT_FOUND
                is InternalException -> HttpStatus.INTERNAL_SERVER_ERROR
            },
            message = "${(ex.message ?: "Something went wrong")}, ${ex.cause?.message}",
            httpMethod = request.method,
            httpUrl = request.servletPath
        )
    }

    @ExceptionHandler(value = [NotImplementedError::class])
    fun handleNotImplementedError(
        ex: NotImplementedError,
        request: HttpServletRequest
    ): ResponseEntity<ApiError> {
        return buildErrorResponse(
            httpStatus = HttpStatus.NOT_IMPLEMENTED,
            message = "${(ex.message ?: "Not implemented ")}, ${ex.cause?.message}",
            httpMethod = request.method,
            httpUrl = request.servletPath
        )
    }

}