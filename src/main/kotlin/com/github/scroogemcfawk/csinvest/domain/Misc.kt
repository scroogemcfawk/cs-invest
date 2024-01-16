package com.github.scroogemcfawk.csinvest.domain


class Misc(
    name: String,
    rarity: Rarity,
    val type: MiscType,
) : Item(name, rarity) {

    override val builder: MiscBuilder
        get() = MiscBuilder().withName(name).withRarity(rarity).withType(type)

}


class MiscBuilder : ItemBuilder() {

    override fun withName(name: String): MiscBuilder {
        TODO("Not yet implemented")
    }

    override fun withRarity(rarity: Rarity): MiscBuilder {
        TODO("Not yet implemented")
    }

    fun withType(type: MiscType): MiscBuilder {
        TODO("Not yet implemented")
    }

    override fun build(): Misc {
        TODO("Not yet implemented")
    }

    override fun copy(): MiscBuilder {
        TODO("Not yet implemented")
    }
}


enum class MiscType {
    AGENT,
    MUSIC_KIT,
    COLLECTIBLE,
    TAG,
    TOOL
}
