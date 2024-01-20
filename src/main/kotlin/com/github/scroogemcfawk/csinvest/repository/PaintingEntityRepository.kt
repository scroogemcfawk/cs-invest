package com.github.scroogemcfawk.csinvest.repository

import com.github.scroogemcfawk.csinvest.entity.PaintingEntity
import org.springframework.data.repository.NoRepositoryBean

@NoRepositoryBean
interface PaintingEntityRepository: ItemRepository<Long, PaintingEntity>
