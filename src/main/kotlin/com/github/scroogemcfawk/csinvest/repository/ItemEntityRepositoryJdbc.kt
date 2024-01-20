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
            "CREATE TABLE IF NOT EXISTS Item(" +
            "id BIGINT PRIMARY KEY, " +
            "name VARCHAR(128), " +
            "rarity Rarity" +
            ")"
        )

        createItemSequence()
    }

    private fun createRarityType() {
        // lgtm, because it's enum propagation
        try {
            jdbcTemplate.execute(
                "CREATE TYPE Rarity AS ENUM (${Rarity.entries.joinToString(", ") { "'$it'" }})"
            )
        } catch (e: Exception) {
            log.debug("Error creating Rarity type: {}", e.message)
        }
    }

    private fun createItemSequence() {
        jdbcTemplate.execute("CREATE SEQUENCE IF NOT EXISTS Item_Id_Seq START WITH 1 OWNED BY Item.id")
    }

    override fun save(item: ItemEntity) {
        if (item.id != 0L) {
            jdbcTemplate.update(
                "UPDATE Item SET name = ?, rarity = CAST(? as Rarity) WHERE id = ?",
                item.name, item.rarity.toString(), item.id
            )
            return
        }

        jdbcTemplate.query("SELECT nextval('Item_Id_Seq')") { rs ->
            item.overrideId(rs.getLong(1))
        }
        jdbcTemplate.update(
            "INSERT INTO Item (id, name, rarity) VALUES (?, ?, CAST(? as Rarity))",
            item.id, item.name, item.rarity.toString()
        )
    }

    override fun findAll(): Iterable<ItemEntity> {
        return jdbcTemplate.query("SELECT * FROM Item", rowMapper)
    }

}
