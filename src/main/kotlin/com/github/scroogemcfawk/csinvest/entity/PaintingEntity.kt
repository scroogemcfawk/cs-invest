package com.github.scroogemcfawk.csinvest.entity

import com.github.scroogemcfawk.csinvest.domain.*
import org.springframework.data.relational.core.mapping.Table


@Table("csi_painting")
class PaintingEntity(
    id: Long = 0L,
    name: String,
    rarity: Rarity,
    var base: Base,
    var category: Category,
    var exterior: Exterior

) : ItemEntity(id, name, rarity) {

    override fun toTypedString(): String {
        return Painting(name, rarity, base, category, exterior).toString()
    }

}
