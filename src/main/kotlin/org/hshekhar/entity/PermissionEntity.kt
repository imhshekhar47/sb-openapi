package org.hshekhar.entity

import org.hibernate.annotations.GenericGenerator
import org.hshekhar.api.error.NotFoundException
import org.hshekhar.model.Permission
import org.hshekhar.model.PermissionRef
import org.hshekhar.model.PermissionUpdate
import org.mapstruct.*
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct
import javax.persistence.*

/**
 * @created 1/5/2023'T'1:55 PM
 * @author Himanshu Shekhar (609080540)
 **/

@Entity
@Table(name = "permissions")
data class PermissionEntity(
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    var id: String? = null,

    @Column(nullable = false, unique = true)
    var code: String? = null,
    var description: String? = null,
    var isDisabled: Boolean = false
)

@Repository
interface PermissionRepository : JpaRepository<PermissionEntity, String>


@Mapper(
    componentModel = "spring",
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE,
    uses = [
    ]
)
interface PermissionMapper {

    companion object {
        fun mapToRef(model: Permission): PermissionRef {
            return with(model) {
                PermissionRef(id = this.id, code = this.code)
            }
        }

        fun mapToRef(entity: PermissionEntity): PermissionRef {
            return with(entity) {
                PermissionRef(id = this.id, code = this.code)
            }
        }
    }

    @Mappings(
        Mapping(target = "id", expression = "java(model.getId().isEmpty() ? null : model.getId())")
    )
    fun map(model: Permission): PermissionEntity

    @InheritInverseConfiguration
    fun map(entity: PermissionEntity): Permission

    fun map(list: List<PermissionEntity>): List<PermissionRef>
}

@Service
class PermissionRepoService(
    private val mapper: PermissionMapper,
    private val repository: PermissionRepository
) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(PermissionRepoService::class.java)
    }

    fun list(offset: Int, limit: Int): List<PermissionRef> {
        LOGGER.debug("entry: list(offset=$offset, limit=$limit)")
        val items = repository.findAll(PageRequest.of(offset, limit))
            .map { mapper.map(it) }
            .map { PermissionMapper.mapToRef(it) }
            .toList()
        LOGGER.debug("exit: list()")
        return items
    }

    fun save(model: Permission): Permission {
        return handleException {
            LOGGER.debug("entry: save(model=$model)")
            val saved = repository.save(mapper.map(model))
            LOGGER.debug("exit: save()")
            mapper.map(saved)
        }
    }

    fun patchById(id: String, model: PermissionUpdate): PermissionRef {
        val existing = findById(id) ?: throw NotFoundException()

        val modified = with(existing) {
            existing.copy(
                code = ifNotEmptyOrElse(model.code, this.code),
                description = ifNotEmptyOrElse(model.description, this.description)
            )
        }
        return PermissionMapper.mapToRef(save(modified))
    }

    fun findById(id: String): Permission? {
        LOGGER.debug("entry: find(id=$id)")
        val item = repository.findById(id).orElse(null)?.let { mapper.map(it) }
        LOGGER.debug("exit: find()")
        return item
    }

    @PostConstruct
    fun initialize() {
        LOGGER.info("initializing permission repository")
        val isEmpty = list(0, 1).isEmpty()
        if (isEmpty) {
            val defaultPermissions = listOf(

                Permission(
                    id = "",
                    code = "admin:read",
                    description = "can read admin"
                ),
                Permission(
                    id = "",
                    code = "admin:edit",
                    description = "can edit admin"
                ),
                Permission(
                    id = "",
                    code = "permission:read",
                    description = "can read permissions"
                ),
                Permission(
                    id = "",
                    code = "permission:write",
                    description = "can edit permissions"
                ),
                Permission(
                    id = "",
                    code = "role:read",
                    description = "can read role"
                ),
                Permission(
                    id = "",
                    code = "role:write",
                    description = "can edit role"
                ),
            )

            defaultPermissions.forEach {
                val saved = save(it)
                LOGGER.info("Permission ${saved.code}[id=${saved.id}] created")
            }
        }
    }
}