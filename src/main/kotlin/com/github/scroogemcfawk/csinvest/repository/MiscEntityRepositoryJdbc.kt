package com.github.scroogemcfawk.csinvest.repository

import com.github.scroogemcfawk.csinvest.domain.MiscType
import com.github.scroogemcfawk.csinvest.domain.Rarity
import com.github.scroogemcfawk.csinvest.entity.ItemEntity
import com.github.scroogemcfawk.csinvest.entity.MiscEntity
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet


@Repository
open class MiscEntityRepositoryJdbc : MiscEntityRepository {

    private val log = LoggerFactory.getLogger(MiscEntityRepositoryJdbc::class.java)

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    lateinit var itemRepo: ItemEntityRepository

    override val rowMapper = RowMapper { rs: ResultSet, _: Int ->
        return@RowMapper MiscEntity(
            rs.getLong(1),
            rs.getString(2),
            Rarity.valueOf(rs.getString(3) ?: Rarity.UNDEFINED.toString()),
            MiscType.valueOf(rs.getString(4) ?: MiscType.UNDEFINED.toString())
        )

    }

    @PostConstruct
    private fun createTable() {
        createMiscTypeType()

        jdbcTemplate.execute(
            "CREATE TABLE IF NOT EXISTS csi_misc (" +
                    "id BIGINT PRIMARY KEY, " +
                    "type csi_misc_enum" +
                    ")"
        )

    }

    private fun createMiscTypeType() {
        try {
            jdbcTemplate.execute(
                "CREATE TYPE csi_misc_enum AS ENUM (${MiscType.entries.joinToString(", ") { "'$it'" }})"
            )
        } catch (e: Exception) {
            log.debug("Error creating csi_misc_enum type: {}", e.message)
        }
    }

    override fun save(item: MiscEntity) {

        if (item.id == 0L) {
            itemRepo.save(item as ItemEntity)
            jdbcTemplate.update(
                "INSERT INTO csi_misc (id, type) VALUES (?, CAST(? as csi_misc_enum))",
                item.id, item.type.toString()
            )
        } else {
            itemRepo.save(item as ItemEntity)
            jdbcTemplate.update(
                "UPDATE csi_misc SET type = CAST(? as csi_misc_enum) WHERE id = ?",
                item.type.toString(), item.id
            )
        }

    }

    override fun findAll(): Iterable<MiscEntity> {
        return jdbcTemplate.query(
            "select csi_item.id, name, rarity, type from csi_item inner join csi_misc on csi_item.id = csi_misc.id",
            rowMapper
        )
    }
}
