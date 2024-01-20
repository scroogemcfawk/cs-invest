package com.github.scroogemcfawk.csinvest.repository

import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.Repository


@NoRepositoryBean
interface ItemRepository<PK, T> : Repository<PK, T> {

    fun save(item: T)

    fun findAll(): Iterable<T>

}
