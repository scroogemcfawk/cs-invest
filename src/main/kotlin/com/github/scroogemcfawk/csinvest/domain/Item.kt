package com.github.scroogemcfawk.csinvest.domain

abstract class Item(
    val name: String, // TODO make a dynamic field
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

    open fun withName(name: String): ItemBuilder {
        val new = this.copy()
        new.name = name
        return new
    }

    open fun withRarity(rarity: Rarity): ItemBuilder {
        val new = this.copy()
        new.rarity = rarity
        return new
    }

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

    companion object {
        fun fromString(s: String): Rarity {
            return when(s.uppercase()) {
                "CONTRABAND" -> {
                    IMMORTAL
                }
                "COVERT" -> {
                    ANCIENT
                }
                "CLASSIFIED" -> {
                    LEGENDARY
                }
                "RESTRICTED" -> {
                    MYTHICAL
                }
                "MILSPEC" -> {
                    RARE
                }
                "INDUSTRIAL" -> {
                    UNCOMMON
                }
                "CONSUMER" -> {
                    COMMON
                }
                else -> {
                    UNDEFINED
                }
            }
        }
    }
}
