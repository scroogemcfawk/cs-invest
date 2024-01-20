package com.github.scroogemcfawk.csinvest.repository

import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.Repository
import org.springframework.jdbc.core.RowMapper


@NoRepositoryBean
interface ItemRepository<PK, T> : Repository<PK, T> {

    val rowMapper: RowMapper<T>

    fun save(item: T)

    fun findAll(): Iterable<T>

}
