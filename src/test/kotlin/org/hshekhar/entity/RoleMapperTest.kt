package org.hshekhar.entity

import org.hshekhar.model.PermissionRef
import org.hshekhar.model.Role
import org.hshekhar.model.RoleRef
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @created 1/6/2023'T'11:02 AM
 * @author Himanshu Shekhar (609080540)
 **/

@SpringBootTest
internal class RoleMapperTest {

    @Autowired
    private lateinit var mapper: RoleMapper

    private val rolesMap = hashMapOf(
        "visitor" to RoleEntity(
            id = "tr_visitor",
            code = "visitor",
            description = "Role for visitor",
            permissions = mutableSetOf(),
            roles = mutableSetOf()
        ),

        "customer" to RoleEntity(
            id = "tr_customer",
            code = "customer",
            description = "Role for customer",
            permissions = mutableSetOf(
                PermissionEntity(id = "customer:write", code = "customer:write", description = "Permission to manage customer"),
                PermissionEntity(id = "customer:read", code = "customer:read", description = "Permission to read customer"),
            ),
            roles = mutableSetOf()
        ),

        "admin" to RoleEntity(
            id = "tr_admin",
            code = "admin",
            description = "Role for admin",
            permissions = mutableSetOf(
                PermissionEntity(id = "tp_admin", code = "admin", description = "Permission to manage admins")
            ),
            roles = mutableSetOf(
                RoleEntity(id = "tr_role", code = "role", description = "Permission to manage role"),
                RoleEntity(id = "tr_permission", code = "permission", description = "Permission to manage permissions"),
                RoleEntity(id = "tr_customer", code = "customer", description = "Permission to manage customer")
            )
        )
    )

    private fun getRole(code: String? = null): RoleEntity {
        return rolesMap[code ?: "visitor"]!!
    }

    private fun getRoles(): List<RoleEntity> = rolesMap.values.toList()

    private fun shouldMatch(entity: RoleEntity, model: Role) {
        assertEquals(
            message = "code should match",
            expected = model.code, actual = entity.code
        )

        assertEquals(
            message = "No of roles should match",
            expected = model.roles?.size, actual = entity.roles.size
        )

        assertEquals(
            message = "No of permissions should match",
            expected = model.permissions?.size, actual = entity.permissions.size
        )
    }

    @Test
    fun `test list mapping`() {
        val entities = listOf(
            RoleEntity(
                id = "tr_visitor",
                code = "visitor",
                description = "Role for visitor",
                permissions = emptySet(),
                roles = emptySet()
            ),
            RoleEntity(
                id = "tr_customer",
                code = "customer",
                description = "Role for customer",
                permissions = setOf(
                    PermissionEntity(
                        id = "tp_customer_read",
                        code = "customer:read",
                        description = "Can read customer",
                    ),
                    PermissionEntity(
                        id = "tp_customer_write",
                        code = "customer:write",
                        description = "Can write customer",
                    ),
                ),
                roles = emptySet()
            )
        ).sortedBy { it.code }

        val models = mapper.map(list = entities)
            .sortedBy { it.code }

        (entities.indices).forEach { idx ->
            assertEquals(
                message = "id should match",
                expected = models[idx].id, actual = entities[idx].id
            )

            assertEquals(
                message = "code should match",
                expected = models[idx].code, actual = entities[idx].code
            )
        }
    }

    @Test
    fun `test entity mapping`() {
        val entity = getRole(code = "admin")
        shouldMatch(entity = entity, model = mapper.map(entity))
    }

    @Test
    fun `test model mapping`() {
        val model = Role(
            id = "tr_admin",
            code = "admin",
            description = "Role for admin",
            permissions = mutableListOf(
                PermissionRef(id = "tp_admin", code = "admin")
            ),
            roles = mutableListOf(
                RoleRef(id = "tr_role", code = "role"),
                RoleRef(id = "tr_permission", code = "permission"),
                RoleRef(id = "tr_customer", code = "customer")
            )
        )

        shouldMatch(model = model, entity = mapper.map(model))
    }

}