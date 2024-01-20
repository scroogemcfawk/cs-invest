package com.github.scroogemcfawk.csinvest.repository

import com.github.scroogemcfawk.csinvest.domain.ConsumableType
import com.github.scroogemcfawk.csinvest.domain.Rarity
import com.github.scroogemcfawk.csinvest.entity.ConsumableEntity
import com.github.scroogemcfawk.csinvest.entity.ItemEntity
import jakarta.annotation.PostConstruct
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

    private val rowMapper = RowMapper { rs: ResultSet, _: Int ->
        return@RowMapper ConsumableEntity(
            rs.getLong(1),
            rs.getString(2),
            Rarity.valueOf(rs.getString(3) ?: Rarity.UNDEFINED.toString()),
            ConsumableType.valueOf(rs.getString(4) ?: ConsumableType.UNDEFINED.toString())
        )
    }

    @PostConstruct
    private fun createTable() {
        createConsumableTypeType()

        jdbcTemplate.execute(
            "CREATE TABLE IF NOT EXISTS Consumable(" +
                    "id BIGINT PRIMARY KEY, " +
                    "type consumable_type" +
                    ")"
        )

    }

    private fun createConsumableTypeType() {
        try {
            jdbcTemplate.execute(
                "CREATE TYPE consumable_type AS ENUM (${ConsumableType.entries.joinToString(", ") { "'$it'" }})"
            )
        } catch (e: Exception) {
            log.debug("Error creating consumable_type type: {}", e.message)
        }
    }


    override fun save(item: ConsumableEntity) {

        if (item.id == 0L) {
            itemRepo.save(item as ItemEntity)
            jdbcTemplate.update(
                "INSERT INTO Consumable (id, type) VALUES (?, CAST(? as consumable_type))",
                item.id, item.type.toString()
            )
        } else {
            itemRepo.save(item as ItemEntity)
            jdbcTemplate.update(
                "UPDATE Consumable SET type = CAST(? as consumable_type) WHERE id = ?",
                item.type.toString(), item.id
            )
        }

    }

    override fun findAll(): Iterable<ConsumableEntity> {
        return jdbcTemplate.query("select item.id, name, rarity, type from item inner join consumable on item.id = consumable.id", rowMapper)
    }
}
