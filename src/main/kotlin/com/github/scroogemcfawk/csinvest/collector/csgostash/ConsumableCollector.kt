package com.github.scroogemcfawk.csinvest.collector.csgostash

import com.github.scroogemcfawk.csinvest.domain.Consumable
import com.github.scroogemcfawk.csinvest.domain.ConsumableBuilder
import com.github.scroogemcfawk.csinvest.domain.ConsumableType
import com.github.scroogemcfawk.csinvest.domain.Rarity
import jakarta.annotation.Resource
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.slf4j.LoggerFactory


class ConsumableCollector {

    private val log = LoggerFactory.getLogger(ConsumableCollector::class.java)

    @Resource(name = "homePage")
    private lateinit var homePage: Document

    @Resource(name = "otherMenuAccessor")
    private lateinit var otherMenuAccessor: OtherMenuAccessor

    init {
        log.atDebug()
    }

    fun get(): ArrayList<Consumable> {
        val consumables = ArrayList<Consumable>()

        consumables.addAll(
//            fetchStickers() +
//                    fetchPatches() +
                            fetchGraffities() +
                                    fetchKeysAndPasses()
        )

        return consumables
    }

    private fun fetchStickers(): ArrayList<Consumable> {
        val stickers = ArrayList<Consumable>()

        stickers += fetchRegularStickers()
        stickers += fetchTournamentStickers()

        return stickers
    }

    private fun fetchPatches(): ArrayList<Consumable> {
        val patches = ArrayList<Consumable>()
        val menu = otherMenuAccessor.getSections()

        val allPageUrl = menu[OtherMenuSection.PATCHES]?.let {
            it[0].select("a").attr("href")
        } ?: run {
            log.warn("Unexpected menu element while fetching patches.")
            return patches
        }

        val patchTiles = SectionScraper(allPageUrl).get()

        patches += fetchConsumablesFromTiles(patchTiles, ConsumableBuilder().withType(ConsumableType.PATCH))

        return patches
    }

    private fun fetchGraffities(): ArrayList<Consumable> {
        val graffiti = ArrayList<Consumable>()
        val menu = otherMenuAccessor.getSections()

        val allPageUrl = menu[OtherMenuSection.GRAFFITI]?.let {
            it[0].select("a").attr("href")
        } ?: run {
            log.warn("Unexpected menu element while fetching graffities.")
            return graffiti
        }

        val graffitiTiles = SectionScraper(allPageUrl).get()

        graffiti += fetchConsumablesFromTiles(graffitiTiles, ConsumableBuilder().withType(ConsumableType.GRAFFITI))

        return graffiti
    }

    private fun fetchKeysAndPasses(): ArrayList<Consumable> {
        val keysAndPasses = ArrayList<Consumable>()
        // TODO("Not yet implemented")
        return keysAndPasses
    }

    private fun fetchRegularStickers(): ArrayList<Consumable> {
        val regularStickersPageUrl = "https://csgostash.com/stickers/regular"
        val regularStickerTiles = SectionScraper(regularStickersPageUrl).get()

        return fetchConsumablesFromTiles(regularStickerTiles, ConsumableBuilder().withType(ConsumableType.STICKER))
    }

    private fun fetchTournamentStickers(): ArrayList<Consumable> {
        val tournamentStickersPageUrl = "https://csgostash.com/stickers/tournament"
        val tournamentStickerTiles = SectionScraper(tournamentStickersPageUrl).get()

        return fetchConsumablesFromTiles(tournamentStickerTiles, ConsumableBuilder().withType(ConsumableType.STICKER))

    }

    private fun fetchConsumablesFromTiles(tiles: Elements, prototype: ConsumableBuilder): ArrayList<Consumable> {
        val res = ArrayList<Consumable>()

        for (tile in tiles) {
            run {
                val name = tile.selectFirst("h3 > a")?.text() ?: let {
                    log.warn("Item name not found in tile markup.")
                    return@run // continue for loop
                }

                val rarityString = tile.selectFirst("div.quality")?.className()?.split("-")?.last() ?: let {
                    log.warn("Item rarity not found in tile markup.")
                    return@run // continue for loop
                }

                // TODO make rarity converter
                val rarity = when(rarityString) {

                    "contraband" -> {
                        Rarity.IMMORTAL
                    }
                    "covert" -> {
                        Rarity.ANCIENT
                    }
                    "classified" -> {
                        Rarity.LEGENDARY
                    }
                    "restricted" -> {
                        Rarity.MYTHICAL
                    }
                    "milspec" -> {
                        Rarity.RARE
                    }
                    "industrial" -> {
                        Rarity.UNCOMMON
                    }
                    "consumer" -> {
                        Rarity.COMMON
                    }

                    else -> {
                        Rarity.UNDEFINED
                    }
                }

                if (name.isBlank() || rarity == Rarity.UNDEFINED) {
                    log.warn("Item name or quality have unexpected value in tile markup.")
                    return@run
                }

                res.add(
                    prototype
                        .withName(name)
                        .withRarity(rarity)
                        .build()
                )

            }
        }

        return res
    }

}
