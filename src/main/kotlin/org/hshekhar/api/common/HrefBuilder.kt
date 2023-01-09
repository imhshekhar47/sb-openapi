package org.hshekhar.api.common

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

/**
 * @created 1/9/2023'T'10:59 AM
 * @author Himanshu Shekhar (609080540)
 **/

@Component
class HrefBuilder {

    @Value("\${server.address:}")
    private val basePath: String? = null

    @Value("\${server.port:8080}")
    private val basePort: String? = null

    @Value("\${server.servlet.context-path:/}")
    private val contextPath: String? = null

    @Value("\${api.base-path:/}")
    private val apiPath: String? = null

    fun build(uuid: String, type: String): String {
        val refUrl = listOf(contextPath, apiPath, type, uuid)
            .joinToString(separator = "/")
            .replace("/+".toRegex(), "/")
        return if (basePath.isNullOrBlank()) refUrl else "$basePath:$basePort$refUrl"
    }
}