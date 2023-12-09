package com.github.scroogemcfawk.csinvest.domain


class Container(
    name: String,
    rarity: Rarity,
    type: ContainerType
): Item(name, rarity)


enum class ContainerType {
    WEAPON_CASE,
    SOUVENIR_PACKAGE,
    GIFT_BOX,
    STICKER_CAPSULE,
    MUSIC_KIT_BOX,
    GRAFFITI_BOX,
    PATCH_PACK
}
