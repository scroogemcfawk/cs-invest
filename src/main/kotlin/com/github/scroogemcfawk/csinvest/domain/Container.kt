package com.github.scroogemcfawk.csinvest.domain


class Container(
    name: String,
    rarity: Rarity,
    val type: ContainerType
) : Item(name, rarity) {

    override fun toString(): String {
        return "Container(name=$name, rarity=$rarity, type=$type)"
    }

    override val builder: ContainerBuilder
        get() = ContainerBuilder().withName(name).withRarity(rarity).withType(type)

}


class ContainerBuilder: ItemBuilder() {
    var type = ContainerType.UNDEFINED

    override fun withName(name: String): ContainerBuilder {
        val new = this.copy()
        new.name = name
        return new
    }

    override fun withRarity(rarity: Rarity): ContainerBuilder {
        val new = this.copy()
        new.rarity = rarity
        return new
    }

    fun withType(type: ContainerType): ContainerBuilder {
        val new = this.copy()
        new.type = type
        return new
    }

    override fun build(): Container {
        return Container(name, rarity, type)
    }

    override fun copy(): ContainerBuilder {
        val copy = ContainerBuilder()
        copy.name = name
        copy.rarity = rarity
        copy.type = type
        return copy
    }
}


enum class ContainerType {
    WEAPON_CASE,
    SOUVENIR_PACKAGE,
    GIFT_PACKAGE,
    STICKER_CAPSULE,
    MUSIC_KIT_BOX,
    GRAFFITI_BOX,
    PATCH_PACK,
    PIN_PACK,

    UNDEFINED;
}
