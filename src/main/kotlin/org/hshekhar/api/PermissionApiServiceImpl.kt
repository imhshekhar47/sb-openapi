package org.hshekhar.api

import org.hshekhar.api.common.EMPTY
import org.hshekhar.api.error.NotFoundException
import org.hshekhar.entity.PermissionRepoService
import org.hshekhar.model.Permission
import org.hshekhar.model.PermissionCreate
import org.hshekhar.model.PermissionRef
import org.hshekhar.model.PermissionUpdate
import org.springframework.stereotype.Service

/**
 * @created 1/5/2023'T'1:19 PM
 * @author Himanshu Shekhar (609080540)
 **/

@Service
class PermissionApiServiceImpl(private val service: PermissionRepoService) : PermissionApiService {

    override fun createPermission(permissionCreate: PermissionCreate): Permission {
        val permission = with(permissionCreate) {
            Permission(
                id = EMPTY,
                code = this.code,
                description = this.description
            )
        }

        return service.save(permission)
    }

    override fun listPermission(offset: Int, limit: Int): List<PermissionRef> {
        return service.list(offset, limit)
    }

    override fun patchPermission(id: String, permissionUpdate: PermissionUpdate): PermissionRef {
        return service.patchById(id, permissionUpdate)?: throw NotFoundException()
    }

    override fun retrievePermission(id: String): Permission {
        return service.findById(id)?: throw NotFoundException()
    }
}