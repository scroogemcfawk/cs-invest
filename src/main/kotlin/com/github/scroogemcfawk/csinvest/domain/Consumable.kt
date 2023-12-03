package com.github.scroogemcfawk.csinvest.domain


class Consumable(
    name: String,
    rarity: Rarity,
    val type: ConsumableType
) : Item(name, rarity)


enum class ConsumableType {
    STICKER,
    PATCH,
    KEY,
    PASS,
    GRAFFITI
}
