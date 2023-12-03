package com.github.scroogemcfawk.csinvest.domain

abstract class Item(
    val name: String,
    val rarity: Rarity
)


enum class Rarity {
    COMMON,
    UNCOMMON,
    RARE,
    MYTHICAL,
    LEGENDARY,
    ANCIENT,
    IMMORTAL
}
