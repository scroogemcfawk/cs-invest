package com.github.scroogemcfawk.csinvest.domain


class Painting(
    name: String,
    rarity: Rarity,
    val base: Base,
    val category: Category,
    val exterior: Exterior
) : Item(name, rarity)


enum class Base {
    AK_47,
    AUG,
    AWP,
    BAYONET_KNIFE,
    BOWIE_KNIFE,
    BUTTERFLY_KNIFE,
    CLASSIC_KNIFE,
    CZ75_AUTO,
    DESERT_EAGLE,
    DUAL_BERETTAS,
    FALCHION_KNIFE,
    FAMAS,
    FIVE_SEVEN,
    FLIP_KNIFE,
    G3SG1,
    GALIL_AR,
    GLOCK_18,
    GUT_KNIFE,
    HUNTSMAN_KNIFE,
    KARAMBIT_KNIFE,
    M249,
    M4A1_S,
    M4A4,
    M9_BAYONET_KNIFE,
    MAC_10,
    MAG_7,
    MP5_SD,
    MP7,
    MP9,
    NAVAJA_KNIFE,
    NEGEV,
    NOMAD_KNIFE,
    NOVA,
    P2000,
    P250,
    P90,
    PARACORD_KNIFE,
    PP_BIZON,
    R8_REVOLVER,
    SAWED_OFF,
    SCAR_20,
    SG_553,
    SHADOW_DAGGERS_KNIFE,
    SKELETON_KNIFE,
    SSG_08,
    STILETTO_KNIFE,
    SURVIVAL_KNIFE,
    TALON_KNIFE,
    TEC_9,
    UMP_45,
    URSUS_KNIFE,
    USP_S,
    XM1014,
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
