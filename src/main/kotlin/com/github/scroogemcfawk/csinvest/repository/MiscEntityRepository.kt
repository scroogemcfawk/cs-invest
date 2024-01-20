package com.github.scroogemcfawk.csinvest.repository

import com.github.scroogemcfawk.csinvest.entity.MiscEntity
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface MiscEntityRepository : ItemRepository<Long, MiscEntity>
