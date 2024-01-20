package com.github.scroogemcfawk.csinvest.repository

import com.github.scroogemcfawk.csinvest.entity.ContainerEntity
import org.springframework.data.repository.NoRepositoryBean


@NoRepositoryBean
interface ContainerEntityRepository : ItemRepository<Long, ContainerEntity>
