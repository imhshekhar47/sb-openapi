package org.hshekhar.entity

import org.hshekhar.model.Permission
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * @created 1/6/2023'T'3:07 PM
 * @author Himanshu Shekhar (609080540)
 **/

@SpringBootTest
internal class PermissionMapperTest {

    @Autowired
    private lateinit var mapper: PermissionMapper

    private val permissionMap = hashMapOf(
        "tp_customer_read" to PermissionEntity(
            id = "tp_customer_read",
            code = "customer:read",
            description = "Can read customer",
        ),
        "tp_customer_write" to PermissionEntity(
            id = "tp_customer_read",
            code = "customer:write",
            description = "Can write customer",
        ),
        "tp_customer_type_read" to PermissionEntity(
            id = "tp_customer_type_read",
            code = "customertype:read",
            description = "Can read customer type",
        ),
        "tp_customer_type_write" to PermissionEntity(
            id = "tp_customer_type_write",
            code = "customertype:write",
            description = "Can write customer type",
        )
    )

    private fun getPermission(code: String): PermissionEntity {
        return permissionMap[code]!!
    }

    private fun shouldMatch(entity: PermissionEntity, model: Permission) {
        assertEquals(
            message = "Id should match",
            expected = entity.id, actual = model.id
        )

        assertEquals(
            message = "Code should match",
            expected = entity.code, actual = model.code
        )

        assertEquals(
            message = "Description should match",
            expected = entity.description, actual = model.description
        )
    }

    @Test
    fun `test list mapping`() {
        val entities = permissionMap.values.sortedBy { it.id }
        val models = mapper.map(entities).sortedBy { it.id }

        (models.indices).forEach { idx ->
            assertEquals(
                message = "Id should match",
                expected = entities[idx].id, actual = models[idx].id
            )

            assertEquals(
                message = "Code should match",
                expected = entities[idx].code, actual = models[idx].code
            )
        }
    }

    @Test
    fun `test entity mapping`() {
        val entity = getPermission("tp_customer_read")
        val model = mapper.map(entity)

        shouldMatch(entity = entity, model = model)

    }

    @Test
    fun `test model mapping`() {
        val model = Permission(
            id = "tp_customer_read",
            code = "customer:read",
            description = "Can read customer"
        )

        val entity = mapper.map(model)

        shouldMatch(entity = entity, model = model)
    }
}