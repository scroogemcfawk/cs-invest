package com.github.scroogemcfawk.csinvest.domain

abstract class Item(
    val name: String,
    val rarity: Rarity
) {

    override fun toString(): String {
        return "Item(name=$name, rarity=${rarity})"
    }

}

abstract class ItemBuilder {
    var name: String = ""
    var rarity: Rarity = Rarity.UNDEFINED

    abstract fun build(): Item
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
