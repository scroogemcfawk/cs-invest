package com.github.scroogemcfawk.csinvest.repository

import com.github.scroogemcfawk.csinvest.domain.MiscType
import com.github.scroogemcfawk.csinvest.domain.Rarity
import com.github.scroogemcfawk.csinvest.entity.ItemEntity
import com.github.scroogemcfawk.csinvest.entity.MiscEntity
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

    override val tableName = "csi_misc"
    private val miscEnum = "csi_misc_enum"


    override fun setup() {
        createMiscTypeType()

        jdbcTemplate.execute(
            """
            CREATE TABLE IF NOT EXISTS 
            $tableName (
            
                id BIGINT PRIMARY KEY, 
                type $miscEnum,
                
                CONSTRAINT fk_item 
                    FOREIGN KEY (id)
                        REFERENCES ${itemRepo.tableName} (id) 
            )
            """.trimIndent()
        )

    }

    private fun createMiscTypeType() {
        try {
            jdbcTemplate.execute(
                "CREATE TYPE $miscEnum AS ENUM (${MiscType.entries.joinToString(", ") { "'$it'" }})"
            )
        } catch (e: Exception) {
            log.debug("Error creating $miscEnum type: {}", e.message)
        }
    }

    override fun save(item: MiscEntity) {

        if (item.id == 0L) {
            itemRepo.save(item as ItemEntity)
            jdbcTemplate.update(
                "INSERT INTO csi_misc (id, type) VALUES (?, CAST(? as $miscEnum))",
                item.id, item.type.toString()
            )
        } else {
            itemRepo.save(item as ItemEntity)
            jdbcTemplate.update(
                "UPDATE csi_misc SET type = CAST(? as $miscEnum) WHERE id = ?",
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

    override fun findAll(): Iterable<MiscEntity> {
        return jdbcTemplate.query(
            "select ${itemRepo.tableName}.id, name, rarity, type from ${itemRepo.tableName} inner join $tableName on ${itemRepo.tableName}.id = $tableName.id",
            rowMapper
        )
    }

    override fun drop() {
        jdbcTemplate.execute("DROP TABLE IF EXISTS $tableName")
        jdbcTemplate.execute("DROP TYPE IF EXISTS $miscEnum")
    }

}
