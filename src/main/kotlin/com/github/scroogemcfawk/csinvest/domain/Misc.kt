package com.github.scroogemcfawk.csinvest.domain


class Misc(
    name: String,
    rarity: Rarity,
    val type: MiscType,
) : Item(name, rarity) {

    override fun toString(): String {
        return "Misc(name=$name, rarity=$rarity, type=$type)"
    }

    override val builder: MiscBuilder
        get() = MiscBuilder().withName(name).withRarity(rarity).withType(type)

}


class MiscBuilder: ItemBuilder() {

    var type = MiscType.UNDEFINED

    override fun withName(name: String): MiscBuilder {
        val new = this.copy()
        new.name = name
        return new
    }

    override fun withRarity(rarity: Rarity): MiscBuilder {
        val new = this.copy()
        new.rarity = rarity
        return new
    }

    fun withType(type: MiscType): MiscBuilder {
        val new = this.copy()
        new.type = type
        return new
    }

    override fun build(): Misc {
        return Misc(name, rarity, type)
    }

    override fun copy(): MiscBuilder {
        val copy = MiscBuilder()
        copy.name = name
        copy.rarity = rarity
        copy.type = type
        return copy
    }

}


enum class MiscType {
    AGENT,
    MUSIC_KIT,
    COLLECTIBLE, // PIN
    TAG,
    TOOL,

    UNDEFINED;
}
