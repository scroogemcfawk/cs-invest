package com.github.scroogemcfawk.csinvest.domain


class Consumable(
    name: String,
    rarity: Rarity,
    val type: ConsumableType
) : Item(name, rarity) {

    override val builder: ConsumableBuilder
        get() = ConsumableBuilder().withName(name).withRarity(rarity).withType(type)

    override fun toString(): String {
        return "Consumable(name=$name, rarity=$rarity, type=$type)"
    }

}

class ConsumableBuilder: ItemBuilder() {

    var type = ConsumableType.UNDEFINED

    override fun withName(name: String): ConsumableBuilder {
        val new = this.copy()
        new.name = name
        return new
    }

    override fun withRarity(rarity: Rarity): ConsumableBuilder {
        val new = this.copy()
        new.rarity = rarity
        return new
    }

    fun withType(type: ConsumableType): ConsumableBuilder {
        val new = this.copy()
        new.type = type
        return new
    }

    override fun build(): Consumable {
        return Consumable(name, rarity, type)
    }

    override fun copy(): ConsumableBuilder {
        val copy = ConsumableBuilder()
        copy.name = name
        copy.rarity = rarity
        copy.type = type
        return copy
    }

}


enum class ConsumableType {
    STICKER,
    PATCH,
    KEY,
    PASS,
    GRAFFITI,

    UNDEFINED;
}
