package com.github.scroogemcfawk.csinvest.entity

import com.github.scroogemcfawk.csinvest.domain.Item
import com.github.scroogemcfawk.csinvest.domain.ItemBuilder
import com.github.scroogemcfawk.csinvest.domain.Rarity
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table


@Table("csi_item")
open class ItemEntity(
    id: Long = 0L,
    var name: String,
    var rarity: Rarity
) : JdbcEntity {

    @Id
    var id: Long = id
        private set

    override fun overrideId(newId: Long) {
        id = newId
    }

    open fun toTypedString(): String {
        // I don't fucking know what I'm doing
        return object : Item(name, rarity){
            override val builder: ItemBuilder
                get() = TODO("IS NOT SUPPOSED TO BE IMPLEMENTED IF YOU SEE THIS MESSAGE THEN AAAAAAAAA")
        }.toString()
    }

}
