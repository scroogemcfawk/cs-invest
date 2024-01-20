package com.github.scroogemcfawk.csinvest.repository

import com.github.scroogemcfawk.csinvest.domain.ContainerType
import com.github.scroogemcfawk.csinvest.domain.Rarity
import com.github.scroogemcfawk.csinvest.entity.ContainerEntity
import com.github.scroogemcfawk.csinvest.entity.ItemEntity
import jakarta.annotation.PostConstruct
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

    @PostConstruct
    private fun createTable() {
        createContainerTypeType()

        jdbcTemplate.execute(
            "CREATE TABLE IF NOT EXISTS csi_container (" +
                    "id BIGINT PRIMARY KEY, " +
                    "type csi_container_enum" +
                    ")"
        )

    }

    private fun createContainerTypeType() {
        try {
            jdbcTemplate.execute(
                "CREATE TYPE csi_container_enum AS ENUM (${ContainerType.entries.joinToString(", ") { "'$it'" }})"
            )
        } catch (e: Exception) {
            log.debug("Error creating csi_container_enum type: {}", e.message)
        }
    }


    override fun save(item: ContainerEntity) {

        if (item.id == 0L) {
            itemRepo.save(item as ItemEntity)
            jdbcTemplate.update(
                "INSERT INTO csi_container (id, type) VALUES (?, CAST(? as csi_container_enum))",
                item.id, item.type.toString()
            )
        } else {
            itemRepo.save(item as ItemEntity)
            jdbcTemplate.update(
                "UPDATE csi_container SET type = CAST(? as csi_container_enum) WHERE id = ?",
                item.type.toString(), item.id
            )
        }

    }

    override fun findAll(): Iterable<ContainerEntity> {
        return jdbcTemplate.query(
            "select csi_item.id, name, rarity, type from csi_item inner join csi_container on csi_item.id = csi_container.id"
            , rowMapper
        )
    }
}
