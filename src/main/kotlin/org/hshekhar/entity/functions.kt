package org.hshekhar.entity

import org.hshekhar.api.error.BadInputException
import org.hshekhar.api.error.InternalException
import org.springframework.dao.DataIntegrityViolationException

/**
 * @created 1/5/2023'T'3:13 PM
 * @author Himanshu Shekhar (609080540)
 **/

fun ifNotEmptyOrElse(value: String?, or: String?): String {
    return when (value) {
        null, "" -> or ?: ""
        else -> value
    }
}

fun <T> handleException(block: () -> T): T {
    return try {
        block()
    } catch (ex: Exception) {
        when (ex) {
            is DataIntegrityViolationException -> throw BadInputException(cause = ex)
            else -> throw InternalException(message = ex.message ?: "Execution error", cause = ex)
        }
    }
}