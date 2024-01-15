package com.github.scroogemcfawk.csinvest.collector.csgostash

import com.github.scroogemcfawk.csinvest.domain.Consumable
import com.github.scroogemcfawk.csinvest.domain.ConsumableBuilder
import com.github.scroogemcfawk.csinvest.domain.ConsumableType
import com.github.scroogemcfawk.csinvest.domain.Rarity
import jakarta.annotation.Resource
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory


class ConsumableCollector {

    private val log = LoggerFactory.getLogger(ConsumableCollector::class.java)

    @Resource(name = "homePage")
    private lateinit var homePage: Document

    init {
        log.atDebug()
    }

    fun get(): ArrayList<Consumable> {
        val consumables = ArrayList<Consumable>()

        consumables.addAll(
            fetchStickers() + fetchPatches() + fetchKeysAndPasses() + fetchGraffities()
        )

        return consumables
    }

    private fun fetchStickers(): ArrayList<Consumable> {
        val stickers = ArrayList<Consumable>()


//        val tournamentStickersPageUrl = "https://csgostash.com/stickers/tournament"
//        val tournamentStickersPage = SectionScraper(tournamentStickersPageUrl)

        stickers += fetchRegularStickers()


        // TODO("Not yet implemented")
        return stickers
    }

    private fun fetchPatches(): ArrayList<Consumable> {
        val patches = ArrayList<Consumable>()
//        val menu = fetchOtherMenu(homePage)
        // TODO see OtherMenuAccessor's todo
//        println(menu)
        // TODO("Not yet implemented")
        return patches
    }

    private fun fetchKeysAndPasses(): ArrayList<Consumable> {
        val keysAndPasses = ArrayList<Consumable>()
        // TODO("Not yet implemented")
        return keysAndPasses
    }

    private fun fetchGraffities(): ArrayList<Consumable> {
        val graffities = ArrayList<Consumable>()
        // TODO("Not yet implemented")
        return graffities
    }

    private fun fetchRegularStickers(): ArrayList<Consumable> {
        val regularStickersPageUrl = "https://csgostash.com/stickers/regular"
        val regularStickerPage = SectionScraper(regularStickersPageUrl).get()

        val res = ArrayList<Consumable>()

        val stickerPrototype = ConsumableBuilder().withType(ConsumableType.STICKER)

        for (tile in regularStickerPage) {
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

                    else -> {
                        Rarity.UNDEFINED
                    }
                }

                if (name.isBlank() || rarity == Rarity.UNDEFINED) {
                    log.warn("Item name or quality have unexpected value in tile markup.")
                    return@run
                }

                res.add(
                    stickerPrototype
                        .withName(name)
                        .withRarity(rarity)
                        .build()
                )

            }
        }

        return res

    }

}
