package com.github.scroogemcfawk.csinvest.repository

import com.github.scroogemcfawk.csinvest.domain.Rarity
import com.github.scroogemcfawk.csinvest.entity.ItemEntity
import jakarta.annotation.PostConstruct
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

    private val rowMapper = RowMapper { rs: ResultSet, _: Int ->
        return@RowMapper ItemEntity(
            rs.getLong(1), rs.getString(2), Rarity.valueOf(rs.getString(3) ?: Rarity.UNDEFINED.toString())
        )
    }

    @PostConstruct
    private fun createTable() {
        createRarityType()

        jdbcTemplate.execute(
            "CREATE TABLE IF NOT EXISTS csi_item (" +
            "id BIGINT PRIMARY KEY, " +
            "name VARCHAR(128), " +
            "rarity csi_rarity_enum" +
            ")"
        )

        createItemSequence()
    }

    private fun createRarityType() {
        // lgtm, because it's enum propagation
        try {
            jdbcTemplate.execute(
                "CREATE TYPE csi_rarity_enum AS ENUM (${Rarity.entries.joinToString(", ") { "'$it'" }})"
            )
        } catch (e: Exception) {
            log.debug("Error creating csi_rarity_enum type: {}", e.message)
        }
    }

    private fun createItemSequence() {
        jdbcTemplate.execute("CREATE SEQUENCE IF NOT EXISTS csi_item_id_seq START WITH 1 OWNED BY csi_item.id")
    }

    override fun save(item: ItemEntity) {
        if (item.id != 0L) {
            jdbcTemplate.update(
                "UPDATE csi_item SET name = ?, rarity = CAST(? as csi_rarity_enum) WHERE id = ?",
                item.name, item.rarity.toString(), item.id
            )
            return
        }

        jdbcTemplate.query("SELECT nextval('csi_item_id_seq')") { rs ->
            item.overrideId(rs.getLong(1))
        }
        jdbcTemplate.update(
            "INSERT INTO csi_item (id, name, rarity) VALUES (?, ?, CAST(? as csi_rarity_enum))",
            item.id, item.name, item.rarity.toString()
        )
    }

    override fun findAll(): Iterable<ItemEntity> {
        return jdbcTemplate.query("SELECT * FROM csi_item", rowMapper)
    }

}
