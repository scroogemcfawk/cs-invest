package com.github.scroogemcfawk.csinvest.entity


import com.github.scroogemcfawk.csinvest.domain.Container
import com.github.scroogemcfawk.csinvest.domain.ContainerType
import com.github.scroogemcfawk.csinvest.domain.Rarity
import org.springframework.data.relational.core.mapping.Table


@Table("csi_container")
class ContainerEntity(
    id: Long = 0L,
    name: String,
    rarity: Rarity,
    var type: ContainerType
) : ItemEntity(id, name, rarity) {

    override fun toTypedString(): String {
        return Container(name, rarity, type).toString()
    }

}
