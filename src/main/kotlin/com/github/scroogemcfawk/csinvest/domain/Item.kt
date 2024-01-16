package com.github.scroogemcfawk.csinvest.domain

abstract class Item(
    val name: String,
    val rarity: Rarity
) {

    abstract val builder: ItemBuilder

    override fun toString(): String {
        return "Item(name=$name, rarity=${rarity})"
    }

}

abstract class ItemBuilder {

    var name: String = ""
    var rarity: Rarity = Rarity.UNDEFINED

    abstract fun withName(name: String): ItemBuilder

    abstract fun withRarity(rarity: Rarity): ItemBuilder

    abstract fun build(): Item

    abstract fun copy(): ItemBuilder

}

enum class Rarity {
    COMMON,
    UNCOMMON,
    RARE,
    MYTHICAL,
    LEGENDARY,
    ANCIENT,
    IMMORTAL,

    UNDEFINED;
}
