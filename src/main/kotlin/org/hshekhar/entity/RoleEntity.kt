package org.hshekhar.entity

import org.hibernate.annotations.GenericGenerator
import org.hshekhar.model.Role
import org.hshekhar.model.RoleRef
import org.mapstruct.*
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import javax.annotation.PostConstruct
import javax.persistence.*

/**
 * @created 1/5/2023'T'3:41 PM
 * @author Himanshu Shekhar (609080540)
 **/

@Entity
@Table(name = "role")
data class RoleEntity(
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    var id: String? = null,

    @Column(nullable = false, unique = true)
    var code: String? = null,

    var description: String? = null,

    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.MERGE])
    var roles: Set<RoleEntity> = mutableSetOf(),

    @OneToMany(fetch = FetchType.EAGER, cascade = [CascadeType.MERGE])
    var permissions: Set<PermissionEntity> = mutableSetOf()
)

@Repository
interface RoleRepository : JpaRepository<RoleEntity, String>

@Mapper(
    componentModel = "spring",
    nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS,
    collectionMappingStrategy = CollectionMappingStrategy.TARGET_IMMUTABLE,
    uses = [
        PermissionMapper::class
    ]
)
interface RoleMapper {

    companion object {
        fun mapToRef(model: Role): RoleRef {
            return with(model) {
                return RoleRef(id = this.id, code = this.code)
            }
        }

        fun mapToRef(model: RoleEntity): RoleRef {
            return with(model) {
                return RoleRef(id = this.id!!, code = this.code!!)
            }
        }
    }

    @Mappings(
        Mapping(target = "id", expression = "java(model.getId().isEmpty() ? null : model.getId())")
    )
    fun map(model: Role): RoleEntity

    @InheritInverseConfiguration
    fun map(entity: RoleEntity): Role

    fun map(list: List<RoleEntity>): List<RoleRef>

}


@Service
class RoleRepoService(
    private val mapper: RoleMapper,
    private val repository: RoleRepository
) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(RoleRepoService::class.java)
    }

    fun list(offset: Int, limit: Int): List<RoleRef> {
        LOGGER.debug("entry: list(offset=$offset, limit=$limit)")
        val items = repository.findAll(PageRequest.of(offset, limit))
            .map { mapper.map(it) }
            .map { RoleMapper.mapToRef(it) }

            .toList()
        LOGGER.debug("exit: list()")

        return items
    }

    fun findById(id: String): Role? {
        LOGGER.debug("entry: findById(id=$id)")
        val item = repository.findByIdOrNull(id)?.let { mapper.map(it) }
        LOGGER.debug("exit: findBId()")

        return item
    }

    fun save(model: Role): Role {
        return handleException {
            LOGGER.debug("entry: save(model=$model)")
            val role = mapper.map(model)
            val saved = repository.save(role)
            LOGGER.debug("exit: save()")

            mapper.map(saved)
        }
    }

    @PostConstruct
    fun initialize() {
        LOGGER.info("initializing role repository")

    }
}