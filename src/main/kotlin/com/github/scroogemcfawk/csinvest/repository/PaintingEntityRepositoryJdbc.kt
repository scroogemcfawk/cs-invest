package com.github.scroogemcfawk.csinvest.repository

import com.github.scroogemcfawk.csinvest.domain.*
import com.github.scroogemcfawk.csinvest.entity.ItemEntity
import com.github.scroogemcfawk.csinvest.entity.PaintingEntity
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.jdbc.core.RowMapper
import org.springframework.stereotype.Repository
import java.sql.ResultSet


@Repository
open class PaintingEntityRepositoryJdbc: PaintingEntityRepository {

    private val log = LoggerFactory.getLogger(PaintingEntityRepositoryJdbc::class.java)

    @Autowired
    private lateinit var jdbcTemplate: JdbcTemplate

    @Autowired
    lateinit var itemRepo: ItemEntityRepository

    override val rowMapper = RowMapper { rs: ResultSet, _: Int ->
        return@RowMapper PaintingEntity(
            rs.getLong(1),
            rs.getString(2),
            Rarity.valueOf(rs.getString(3) ?: Rarity.UNDEFINED.toString()),
            Base.valueOf(rs.getString(4) ?: UndefinedBase.UNDEFINED.toString()),
            Category.valueOf(rs.getString(5) ?: Category.UNDEFINED.toString()),
            Exterior.valueOf(rs.getString(6) ?: Exterior.UNDEFINED.toString())
        )
    }

    @PostConstruct
    private fun createTable() {

        createCategoryTypeType()
        createExteriorTypeType()
        createBaseTypeType()

        jdbcTemplate.execute(
            "CREATE TABLE IF NOT EXISTS csi_painting (" +
                    "id BIGINT PRIMARY KEY, " +
                    "base csi_base_enum, " +
                    "category csi_category_enum, " +
                    "exterior csi_exterior_enum" +
                    ")"
        )

    }

    private fun createCategoryTypeType() {
        try {
            jdbcTemplate.execute(
                "CREATE TYPE csi_category_enum AS ENUM (${Category.entries.joinToString(", ") { "'$it'" }})"
            )
        } catch (e: Exception) {
            log.debug("Error creating csi_category_enum type: {}", e.message)
        }
    }

    private fun createExteriorTypeType() {
        try {
            jdbcTemplate.execute(
                "CREATE TYPE csi_exterior_enum AS ENUM (${Exterior.entries.joinToString(", ") { "'$it'" }})"
            )
        } catch (e: Exception) {
            log.debug("Error creating csi_exterior_enum type: {}", e.message)
        }
    }

    private fun createBaseTypeType() {
        try {

            val undefinedBase = UndefinedBase.entries
            val weaponBase = Weapon.entries
            val knifeBase = Knife.entries
            val gloveBase = Gloves.entries
            // I DON'T FUCKING KNOOOOOOOWWWWWWWWWWWW
            val baseAggregation = (undefinedBase + weaponBase + knifeBase + gloveBase) as List<Base>

            jdbcTemplate.execute(
                "CREATE TYPE csi_base_enum AS ENUM (${baseAggregation.joinToString(", ") { "'$it'" }})"
            )
        } catch (e: Exception) {
            log.debug("Error creating csi_base_enum type: {}", e.message)
        }
    }

    override fun save(item: PaintingEntity) {
        if (item.id == 0L) {
            itemRepo.save(item as ItemEntity)
            jdbcTemplate.update(
                "INSERT INTO csi_painting (id, base, category, exterior) " +
                "VALUES (?, CAST(? as csi_base_enum), CAST(? as csi_category_enum), CAST(? as csi_exterior_enum))",
                item.id, item.base.toString(), item.category.toString(), item.exterior.toString()
            )
        } else {
            itemRepo.save(item as ItemEntity)
            jdbcTemplate.update(
                "UPDATE csi_painting SET " +
                "base = CAST(? as csi_base_enum), " +
                "category = CAST(? as csi_category_enum), " +
                "exterior = CAST(? as csi_exterior_enum) " +
                "WHERE id = ?",
                item.base.toString(),
                item.category.toString(),
                item.exterior.toString(),
                item.id
            )
        }
    }

    override fun findAll(): Iterable<PaintingEntity> {
        return jdbcTemplate.query(
            "select csi_item.id, name, rarity, base, category, exterior from csi_item inner join csi_painting on csi_item.id = csi_painting.id",
            rowMapper
        )
    }
}
