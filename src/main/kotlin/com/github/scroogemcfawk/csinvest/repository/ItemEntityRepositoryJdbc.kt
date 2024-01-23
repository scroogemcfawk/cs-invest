package com.github.scroogemcfawk.csinvest.repository

import com.github.scroogemcfawk.csinvest.domain.Rarity
import com.github.scroogemcfawk.csinvest.entity.ItemEntity
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet


@Repository
open class ItemEntityRepositoryJdbc: ItemEntityRepository {

    private val log = LoggerFactory.getLogger(ItemEntityRepositoryJdbc::class.java)

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    override val rowMapper = RowMapper { rs: ResultSet, _: Int ->
        return@RowMapper ItemEntity(
            rs.getLong(1), rs.getString(2), Rarity.valueOf(rs.getString(3) ?: Rarity.UNDEFINED.toString())
        )
    }

    override val tableName = "csi_item"
    private val rarityEnum = "csi_rarity_enum"
    private val sequenceName = "csi_item_id_seq"


    override fun setup() {
        createRarityType()

        jdbcTemplate.execute(
            """
            CREATE TABLE IF NOT EXISTS 
            $tableName (
            
                id BIGINT PRIMARY KEY,
                name VARCHAR(128), 
                rarity $rarityEnum
                
            )
            """.trimIndent()
        )

        createItemSequence()
    }

    private fun createRarityType() {
        // lgtm, because it's enum propagation
        try {
            jdbcTemplate.execute(
                "CREATE TYPE $rarityEnum AS ENUM (${Rarity.entries.joinToString(", ") { "'$it'" }})"
            )
        } catch (e: Exception) {
            log.debug("Error creating $rarityEnum type: {}", e.message)
        }
    }

    private fun createItemSequence() {
        jdbcTemplate.execute("CREATE SEQUENCE IF NOT EXISTS $sequenceName START WITH 1 OWNED BY $tableName.id")
    }

    override fun save(item: ItemEntity) {
        if (item.id != 0L) {
            jdbcTemplate.update(
                "UPDATE $tableName SET name = ?, rarity = CAST(? as $rarityEnum) WHERE id = ?",
                item.name, item.rarity.toString(), item.id
            )
            return
        }

        jdbcTemplate.query("SELECT nextval('$sequenceName')") { rs ->
            item.overrideId(rs.getLong(1))
        }
        jdbcTemplate.update(
            "INSERT INTO $tableName (id, name, rarity) VALUES (?, ?, CAST(? as $rarityEnum))",
            item.id, item.name, item.rarity.toString()
        )
    }

    override fun delete(id: Long) {
        jdbcTemplate.execute(
            """
            DELETE FROM $tableName
            WHERE id = $id
            """.trimIndent()
        )
    }

    override fun findAll(): Iterable<ItemEntity> {
        return jdbcTemplate.query("SELECT * FROM $tableName", rowMapper)
    }


    override fun drop() {

        jdbcTemplate.execute("DROP TABLE IF EXISTS $tableName CASCADE")
        jdbcTemplate.execute("DROP TYPE IF EXISTS $rarityEnum")
        jdbcTemplate.execute("DROP SEQUENCE IF EXISTS $sequenceName")

    }

}
