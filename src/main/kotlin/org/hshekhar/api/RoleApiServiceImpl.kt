package org.hshekhar.api

import org.hshekhar.api.common.EMPTY
import org.hshekhar.api.common.HrefBuilder
import org.hshekhar.api.error.NotFoundException
import org.hshekhar.entity.RoleRepoService
import org.hshekhar.model.Role
import org.hshekhar.model.RoleCreate
import org.hshekhar.model.RoleRef
import org.springframework.stereotype.Service

/**
 * @created 1/5/2023'T'1:20 PM
 * @author Himanshu Shekhar (609080540)
 **/

@Service
class RoleApiServiceImpl(
    private val hrefBuilder: HrefBuilder,
    private val service: RoleRepoService): RoleApiService {

    private fun href(uuid: String) = hrefBuilder.build(uuid = uuid, type = "role")

    override fun createRole(roleCreate: RoleCreate): Role {
        val role = with(roleCreate) {
            Role(
                id = EMPTY,
                code = this.code,
                description = this.description,
                roles = this.roles,
                permissions = this.permissions)
        }
        return service.save(role)
    }

    override fun listRole(offset: Int, limit: Int): List<RoleRef> {
        return service.list(offset = offset, limit = limit)
            .map { it.copy(href = href(it.id)) }
    }

    override fun retrieveRole(id: String): Role {
        return service.findById(id)?: throw NotFoundException()
    }
}