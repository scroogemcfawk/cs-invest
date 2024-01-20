package com.github.scroogemcfawk.csinvest.entity

import com.github.scroogemcfawk.csinvest.domain.Consumable
import com.github.scroogemcfawk.csinvest.domain.ConsumableType
import com.github.scroogemcfawk.csinvest.domain.Rarity
import org.springframework.data.relational.core.mapping.Table


@Table("Consumable")
class ConsumableEntity(
    id: Long = 0L,
    name: String,
    rarity: Rarity,
    var type: ConsumableType
) : ItemEntity(id, name, rarity) {

    override fun toTypedString(): String {
        return Consumable(name, rarity, type).toString()
    }

}
