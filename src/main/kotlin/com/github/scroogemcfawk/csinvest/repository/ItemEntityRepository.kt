package com.github.scroogemcfawk.csinvest.repository

import com.github.scroogemcfawk.csinvest.entity.ItemEntity
import org.springframework.data.repository.NoRepositoryBean


@NoRepositoryBean
interface ItemEntityRepository: ItemRepository<Long, ItemEntity>
