package com.github.scroogemcfawk.csinvest.domain


class Misc(
    name: String,
    rarity: Rarity,
    val type: MiscType,
) : Item(name, rarity)


enum class MiscType {
    AGENT,
    MUSIC_KIT,
    COLLECTIBLE,
    TAG,
    TOOL
}
