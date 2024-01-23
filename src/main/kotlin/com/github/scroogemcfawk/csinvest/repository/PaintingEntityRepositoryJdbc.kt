package com.github.scroogemcfawk.csinvest.repository

import com.github.scroogemcfawk.csinvest.domain.*
import com.github.scroogemcfawk.csinvest.entity.ItemEntity
import com.github.scroogemcfawk.csinvest.entity.PaintingEntity
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

    override val tableName = "csi_painting"
    private val baseEnum = "csi_base_enum"
    private val categoryEnum = "csi_category_enum"
    private val exteriorEnum = "csi_exterior_enum"


    override fun setup() {

        createCategoryTypeType()
        createExteriorTypeType()
        createBaseTypeType()

        jdbcTemplate.execute(
            """
            CREATE TABLE IF NOT EXISTS 
            $tableName (
                id BIGINT PRIMARY KEY, 
                base $baseEnum, 
                category $categoryEnum, 
                exterior $exteriorEnum,

                CONSTRAINT fk_item 
                    FOREIGN KEY (id)
                        REFERENCES ${itemRepo.tableName} (id) 
            )
            """.trimIndent()
        )

    }

    private fun createCategoryTypeType() {
        try {
            jdbcTemplate.execute(
                "CREATE TYPE $categoryEnum AS ENUM (${Category.entries.joinToString(", ") { "'$it'" }})"
            )
        } catch (e: Exception) {
            log.debug("Error creating $categoryEnum type: {}", e.message)
        }
    }

    private fun createExteriorTypeType() {
        try {
            jdbcTemplate.execute(
                "CREATE TYPE $exteriorEnum AS ENUM (${Exterior.entries.joinToString(", ") { "'$it'" }})"
            )
        } catch (e: Exception) {
            log.debug("Error creating $exteriorEnum type: {}", e.message)
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
                "CREATE TYPE $baseEnum AS ENUM (${baseAggregation.joinToString(", ") { "'$it'" }})"
            )
        } catch (e: Exception) {
            log.debug("Error creating $baseEnum type: {}", e.message)
        }
    }

    override fun save(item: PaintingEntity) {
        if (item.id == 0L) {
            itemRepo.save(item as ItemEntity)
            jdbcTemplate.update(
                "INSERT INTO $tableName (id, base, category, exterior) " +
                "VALUES (?, CAST(? as $baseEnum), CAST(? as $categoryEnum), CAST(? as $exteriorEnum))",
                item.id, item.base.toString(), item.category.toString(), item.exterior.toString()
            )
        } else {
            itemRepo.save(item as ItemEntity)
            jdbcTemplate.update(
                "UPDATE $tableName SET " +
                "base = CAST(? as $baseEnum), " +
                "category = CAST(? as $categoryEnum), " +
                "exterior = CAST(? as $exteriorEnum) " +
                "WHERE id = ?",
                item.base.toString(),
                item.category.toString(),
                item.exterior.toString(),
                item.id
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

    override fun findAll(): Iterable<PaintingEntity> {
        return jdbcTemplate.query(
            "select ${itemRepo.tableName}.id, name, rarity, base, category, exterior from ${itemRepo.tableName} inner join $tableName on ${itemRepo.tableName}.id = $tableName.id",
            rowMapper
        )
    }

    override fun drop() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS $tableName")
        jdbcTemplate.execute("DROP TYPE IF EXISTS $baseEnum")
        jdbcTemplate.execute("DROP TYPE IF EXISTS $categoryEnum")
        jdbcTemplate.execute("DROP TYPE IF EXISTS $exteriorEnum")
    }

}
