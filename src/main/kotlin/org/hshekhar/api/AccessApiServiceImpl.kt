package org.hshekhar.api

import org.hshekhar.entity.RoleRepoService
import org.hshekhar.model.AccessDetails
import org.hshekhar.model.AccessMapping
import org.hshekhar.model.Role
import org.springframework.stereotype.Service

/**
 * @created 1/17/2023'T'4:51 PM
 * @author Himanshu Shekhar (609080540)
 **/

@Service
class AccessApiServiceImpl(
    private val roleService: RoleRepoService,
) : AccessApiService {

    private fun extractAllRoles(role: String): Set<Role> {
        val setOfRole = mutableSetOf<Role>()
        val curr = roleService.findById(role)
        curr?.let {
            setOfRole.add(it)
            it.roles?.forEach { roleRef ->
                setOfRole.addAll(extractAllRoles(roleRef.code))
            }
        }

        return setOfRole
    }

    override fun getAccessDetails(roles: String): AccessDetails {
        return AccessDetails(
            details = roles.split(",")
                .flatMap { extractAllRoles(it.trim()) }.toSet()
                .map {
                    AccessMapping(
                        role = it.code,
                        permissions = it.permissions?.map { ref -> ref.code!! }?.toMutableList()
                    )
                }
                .toMutableList()
        )
    }
}