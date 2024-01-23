package com.github.scroogemcfawk.csinvest.repository

import com.github.scroogemcfawk.csinvest.domain.ConsumableType
import com.github.scroogemcfawk.csinvest.domain.Rarity
import com.github.scroogemcfawk.csinvest.entity.ConsumableEntity
import com.github.scroogemcfawk.csinvest.entity.ItemEntity
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet


@Repository
open class ConsumableEntityRepositoryJdbc : ConsumableEntityRepository {

    private val log = LoggerFactory.getLogger(ConsumableEntityRepositoryJdbc::class.java)

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    lateinit var itemRepo: ItemEntityRepository

    override val rowMapper = RowMapper { rs: ResultSet, _: Int ->
        return@RowMapper ConsumableEntity(
            rs.getLong(1),
            rs.getString(2),
            Rarity.valueOf(rs.getString(3) ?: Rarity.UNDEFINED.toString()),
            ConsumableType.valueOf(rs.getString(4) ?: ConsumableType.UNDEFINED.toString())
        )
    }

    override val tableName = "csi_consumable"
    private val consumableEnum = "csi_consumable_enum"


    override fun setup() {
        createConsumableTypeType()

        jdbcTemplate.execute(
            """
            CREATE TABLE IF NOT EXISTS 
            $tableName (
            
                id BIGINT PRIMARY KEY, 
                type $consumableEnum,

                CONSTRAINT ${tableName}_${itemRepo.tableName}_fkey
                   FOREIGN KEY (id)
                        REFERENCES ${itemRepo.tableName} (id) 
                            ON DELETE CASCADE
            )
            """.trimMargin()
        )

    }

    private fun createConsumableTypeType() {
        try {
            jdbcTemplate.execute(
                "CREATE TYPE $consumableEnum AS ENUM (${ConsumableType.entries.joinToString(", ") { "'$it'" }})"
            )
        } catch (e: Exception) {
            log.debug("Error creating $consumableEnum type: {}", e.message)
        }
    }


    override fun save(item: ConsumableEntity) {

        if (item.id == 0L) {
            itemRepo.save(item as ItemEntity)
            jdbcTemplate.update(
                "INSERT INTO $tableName (id, type) VALUES (?, CAST(? as $consumableEnum))",
                item.id, item.type.toString()
            )
        } else {
            itemRepo.save(item as ItemEntity)
            jdbcTemplate.update(
                "UPDATE $tableName SET type = CAST(? as $consumableEnum) WHERE id = ?",
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

    override fun findAll(): Iterable<ConsumableEntity> {
        return jdbcTemplate.query(
            "select ${itemRepo.tableName}.id, name, rarity, type from ${itemRepo.tableName} inner join $tableName on ${itemRepo.tableName}.id = $tableName.id"
            , rowMapper
        )
    }

    override fun drop() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS $tableName")
        jdbcTemplate.execute("DROP TYPE IF EXISTS $consumableEnum")
    }

}
