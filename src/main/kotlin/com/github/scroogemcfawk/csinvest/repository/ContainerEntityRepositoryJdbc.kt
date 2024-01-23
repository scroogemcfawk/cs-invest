package com.github.scroogemcfawk.csinvest.repository

import com.github.scroogemcfawk.csinvest.domain.ContainerType
import com.github.scroogemcfawk.csinvest.domain.Rarity
import com.github.scroogemcfawk.csinvest.entity.ContainerEntity
import com.github.scroogemcfawk.csinvest.entity.ItemEntity
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet


@Repository
open class ContainerEntityRepositoryJdbc : ContainerEntityRepository {

    private val log = LoggerFactory.getLogger(ContainerEntityRepositoryJdbc::class.java)

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    lateinit var itemRepo: ItemEntityRepository

    override val rowMapper = RowMapper { rs: ResultSet, _: Int ->
        return@RowMapper ContainerEntity(
            rs.getLong(1),
            rs.getString(2),
            Rarity.valueOf(rs.getString(3) ?: Rarity.UNDEFINED.toString()),
            ContainerType.valueOf(rs.getString(4) ?: ContainerType.UNDEFINED.toString())
        )
    }

    override val tableName = "csi_container"
    private val containerEnum = "csi_container_enum"


    override fun setup() {
        createContainerTypeType()

        jdbcTemplate.execute(
            """
            CREATE TABLE IF NOT EXISTS 
            $tableName (
            
                id BIGINT PRIMARY KEY,
                type $containerEnum,
                
                CONSTRAINT fk_item 
                    FOREIGN KEY (id)
                        REFERENCES ${itemRepo.tableName} (id)
                            ON DELETE CASCADE
            )
            """.trimIndent()
        )

    }

    private fun createContainerTypeType() {
        try {
            jdbcTemplate.execute(
                "CREATE TYPE $containerEnum AS ENUM (${ContainerType.entries.joinToString(", ") { "'$it'" }})"
            )
        } catch (e: Exception) {
            log.debug("Error creating $containerEnum type: {}", e.message)
        }
    }


    override fun save(item: ContainerEntity) {

        if (item.id == 0L) {
            itemRepo.save(item as ItemEntity)
            jdbcTemplate.update(
                "INSERT INTO $tableName (id, type) VALUES (?, CAST(? as $containerEnum))",
                item.id, item.type.toString()
            )
        } else {
            itemRepo.save(item as ItemEntity)
            jdbcTemplate.update(
                "UPDATE $tableName SET type = CAST(? as $containerEnum) WHERE id = ?",
                item.type.toString(), item.id
            )
        }

    }

    override fun delete(id: Long) {
        jdbcTemplate.execute("""
            DELETE from $tableName WHERE id = $id
        """.trimIndent())
        jdbcTemplate.execute("""
            DELETE from ${itemRepo.tableName} WHERE id = $id
        """.trimIndent())
    }

    override fun findAll(): Iterable<ContainerEntity> {
        return jdbcTemplate.query(
            "select ${itemRepo.tableName}.id, name, rarity, type from ${itemRepo.tableName} inner join $tableName on ${itemRepo.tableName}.id = $tableName.id"
            , rowMapper
        )
    }

    override fun drop() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS $tableName")
        jdbcTemplate.execute("DROP TYPE IF EXISTS $containerEnum")
    }

}
