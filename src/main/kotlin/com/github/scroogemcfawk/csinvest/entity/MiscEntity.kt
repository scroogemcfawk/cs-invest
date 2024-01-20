package com.github.scroogemcfawk.csinvest.entity

import com.github.scroogemcfawk.csinvest.domain.Misc
import com.github.scroogemcfawk.csinvest.domain.MiscType
import com.github.scroogemcfawk.csinvest.domain.Rarity
import org.springframework.data.relational.core.mapping.Table


@Table("csi_misc")
class MiscEntity(
    id: Long = 0L,
    name: String,
    rarity: Rarity,
    var type: MiscType
) : ItemEntity(id, name, rarity) {

    override fun toTypedString(): String {
        return Misc(name, rarity, type).toString()
    }

}
