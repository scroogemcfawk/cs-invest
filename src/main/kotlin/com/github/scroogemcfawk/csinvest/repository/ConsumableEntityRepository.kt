package com.github.scroogemcfawk.csinvest.repository

import com.github.scroogemcfawk.csinvest.entity.ConsumableEntity
import org.springframework.data.repository.NoRepositoryBean


@NoRepositoryBean
interface ConsumableEntityRepository : ItemRepository<Long, ConsumableEntity>
