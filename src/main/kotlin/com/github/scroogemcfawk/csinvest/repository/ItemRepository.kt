package com.github.scroogemcfawk.csinvest.repository

import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.Repository
import org.springframework.jdbc.core.RowMapper


@NoRepositoryBean
interface ItemRepository<PK, T> : Repository<PK, T> {

    val tableName: String

    val rowMapper: RowMapper<T>

    fun save(item: T)

    fun delete(id: PK)

    fun findAll(): Iterable<T>

    // DANGER

    fun setup()

    fun drop()

}
