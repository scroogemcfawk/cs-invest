package com.github.scroogemcfawk.csinvest.domain


class Painting(
    name: String,
    rarity: Rarity,
    val base: Base,
    val category: Category,
    val exterior: Exterior
) : Item(name, rarity) {

    override val builder: PaintingBuilder
        get() = PaintingBuilder().withName(name).withRarity(rarity).withBase(base).withCategory(category).withExterior(exterior)

}


class PaintingBuilder : ItemBuilder() {

    override fun withName(name: String): PaintingBuilder {
        TODO("Not yet implemented")
    }

    override fun withRarity(rarity: Rarity): PaintingBuilder {
        TODO("Not yet implemented")
    }

    fun withBase(base: Base): PaintingBuilder {
        TODO("Not yet implemented")
    }

    fun withCategory(category: Category): PaintingBuilder {
        TODO("Not yet implemented")
    }

    fun withExterior(exterior: Exterior): PaintingBuilder {
        TODO("Not yet implemented")
    }

    override fun build(): Item {
        TODO("Not yet implemented")
    }

    override fun copy(): ItemBuilder {
        TODO("Not yet implemented")
    }

}


/**
 * Marker interface for painting Base enums
 */
interface Base

enum class Weapon : Base {
    AK_47,
    AUG,
    AWP,
    CZ75_AUTO,
    DESERT_EAGLE,
    DUAL_BERETTAS,
    FAMAS,
    FIVE_SEVEN,
    G3SG1,
    GALIL_AR,
    GLOCK_18,
    M249,
    M4A1_S,
    M4A4,
    MAC_10,
    MAG_7,
    MP5_SD,
    MP7,
    MP9,
    NEGEV,
    NOVA,
    P2000,
    P250,
    P90,
    PP_BIZON,
    R8_REVOLVER,
    SAWED_OFF,
    SCAR_20,
    SG_553,
    SSG_08,
    TEC_9,
    UMP_45,
    USP_S,
    XM1014,
}

enum class Knife : Base {
    BAYONET,
    BOWIE,
    BUTTERFLY,
    CLASSIC,
    FALCHION,
    FLIP,
    GUT,
    HUNTSMAN,
    KARAMBIT,
    M9_BAYONET,
    NAVAJA,
    NOMAD,
    PARACORD,
    SHADOW_DAGGERS,
    SKELETON,
    STILETTO,
    SURVIVAL,
    TALON,
    URSUS,
}

enum class Category {
    NORMAL,
    STATTRAK,
    SOUVENIR
}

enum class Exterior {
    FACTORY_NEW,
    MINIMAL_WEAR,
    FIELD_TESTED,
    WELL_WORN,
    BATTLE_SCARRED,
    NOT_PAINTED
}
