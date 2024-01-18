package com.github.scroogemcfawk.csinvest.domain

import com.github.scroogemcfawk.csinvest.utils.normalizeItemString


class Painting(
    name: String,
    rarity: Rarity,
    val base: Base,
    val category: Category,
    val exterior: Exterior
) : Item(name, rarity) {

    override val builder: PaintingBuilder
        get() = PaintingBuilder().withName(name).withRarity(rarity).withBase(base).withCategory(category).withExterior(exterior)


    override fun toString(): String {
        return "Painting(name=$name, rarity=$rarity, base=$base, category=$category, exterior=$exterior)"
    }

}


class PaintingBuilder : ItemBuilder() {

    var base: Base = UndefinedBase.UNDEFINED
    var category: Category = Category.UNDEFINED
    var exterior: Exterior = Exterior.UNDEFINED

    override fun withName(name: String): PaintingBuilder {
        return super.withName(name) as PaintingBuilder
    }

    override fun withRarity(rarity: Rarity): PaintingBuilder {
        return super.withRarity(rarity) as PaintingBuilder
    }

    fun withBase(base: Base): PaintingBuilder {
        val new = this.copy()
        new.base = base
        return new
    }

    fun withCategory(category: Category): PaintingBuilder {
        val new = this.copy()
        new.category = category
        return new
    }

    fun withExterior(exterior: Exterior): PaintingBuilder {
        val new = this.copy()
        new.exterior = exterior
        return new
    }

    override fun build(): Painting {
        return Painting(name, rarity, base, category, exterior)
    }

    override fun copy(): PaintingBuilder {
        val copy = PaintingBuilder()
        copy.name = name
        copy.rarity = rarity
        copy.base = base
        copy.category = category
        copy.exterior = exterior
        return copy
    }

}


/**
 * Marker interface for painting Base enums
 */
interface Base {
    // don't ask
    companion object {
        fun valueOf(s: String): Base {
            val normalized = normalizeName(s)
            try {
                return Weapon.valueOf(normalized)
            } catch (_: Exception) {}
            try {
                return Knife.valueOf(normalized)
            } catch (_: Exception) {}
            try {
                return Gloves.valueOf(normalized)
            } catch (_: Exception) {}
            return UndefinedBase.UNDEFINED
        }

        private fun normalizeName(s: String): String {
            return normalizeItemString(s).removeSuffix("KNIFE").removeSuffix("GLOVES").removeSuffix("_")
        }
    }
}

enum class UndefinedBase : Base {
    UNDEFINED;
}

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

enum class Gloves : Base {
    BLOODHOUND,
    BROKEN_FANG,
    DRIVER,
    HAND_WRAPS,
    HYDRA,
    MOTO,
    SPECIALIST,
    SPORT,
}

enum class Category {
    NORMAL,
    STATTRAK,
    SOUVENIR,

    UNDEFINED;
}

enum class Exterior {
    FACTORY_NEW,
    MINIMAL_WEAR,
    FIELD_TESTED,
    WELL_WORN,
    BATTLE_SCARRED,
    NOT_PAINTED,

    UNDEFINED;
}
