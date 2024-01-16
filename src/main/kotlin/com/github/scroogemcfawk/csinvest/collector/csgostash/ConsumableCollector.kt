package com.github.scroogemcfawk.csinvest.collector.csgostash

import com.github.scroogemcfawk.csinvest.domain.Consumable
import com.github.scroogemcfawk.csinvest.domain.ConsumableBuilder
import com.github.scroogemcfawk.csinvest.domain.ConsumableType
import com.github.scroogemcfawk.csinvest.domain.Rarity
import com.github.scroogemcfawk.csinvest.utils.contains
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
            fetchStickers() + fetchPatches() + fetchGraffities() + fetchKeysAndPasses()
        )

        return consumables
    }

    private fun fetchStickers(): ArrayList<Consumable> {
        val stickers = ArrayList<Consumable>()

        val regularStickerPageUrl = "https://csgostash.com/stickers/regular"
        val tournamentStickerPageUrl = "https://csgostash.com/stickers/tournament"

        stickers += fetchStickersFromUrl(regularStickerPageUrl)
        stickers += fetchStickersFromUrl(tournamentStickerPageUrl)

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

        val patchBlanks = fetchConsumablesFromTiles(patchTiles, ConsumableBuilder().withType(ConsumableType.PATCH))

        for (p in patchBlanks) {
            val prototype = p.builder
            prototype.name = prototype.name.removeSuffix("Patch").trim()
            patches.add(prototype.build())
            if (p.rarity == Rarity.UNDEFINED) log.warn("Unexpected UNDEFINED patch rarity found.")
        }

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

        for (g in graffiti) {
            if (g.rarity == Rarity.UNDEFINED) log.warn("Unexpected UNDEFINED graffiti rarity found.")
        }

        return graffiti
    }

    private fun fetchKeysAndPasses(): ArrayList<Consumable> {
        val keysAndPasses = ArrayList<Consumable>()

        val menu = otherMenuAccessor.getSections()

        val keysAndOtherItemsPageUrl = menu[OtherMenuSection.KEYS_AND_OTHER_ITEMS]?.let {
            it[0].select("a").attr("href")
        } ?: run {
            log.warn("Unexpected menu element while fetching keys and passes.")
            return keysAndPasses
        }

        val keyAndPassTiles = SectionScraper(keysAndOtherItemsPageUrl).get()

        val keysAndPassesBlanks = fetchConsumablesFromTiles(keyAndPassTiles, ConsumableBuilder().withType(ConsumableType.UNDEFINED))

        for (c in keysAndPassesBlanks) {
            val prototype = c.builder
            prototype.type = when (prototype.name) {
                in Regex("[\\w\\s]* Pass( [\\w\\s+]*)?") -> {
                    ConsumableType.PASS
                }
                in Regex("[:\\w\\s]* Key") -> {
                    ConsumableType.KEY
                }
                else -> {
                    continue // ignore capsules, name tag and tools
                }
            }
            keysAndPasses.add(prototype.withRarity(Rarity.COMMON).build())
        }

        return keysAndPasses
    }

    private fun fetchStickersFromUrl(pageUrl: String): ArrayList<Consumable> {
        val tiles = SectionScraper(pageUrl).get()

        val stickers = fetchConsumablesFromTiles(tiles, ConsumableBuilder().withType(ConsumableType.STICKER))

        for (s in stickers) {
            if (s.rarity == Rarity.UNDEFINED) log.warn("Unexpected UNDEFINED sticker rarity found.")
        }

        return stickers
    }

    private fun fetchConsumablesFromTiles(tiles: Elements, prototype: ConsumableBuilder): ArrayList<Consumable> {
        val res = ArrayList<Consumable>()

        for (tile in tiles) {
            run {

                val name = tile.select("h3, h4").joinToString(" | ") { it.text() }

                val rarityString = tile.selectFirst("div.quality")?.className()?.split("-")?.last() ?: ""

                val rarity = Rarity.fromString(rarityString)

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
