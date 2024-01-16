package com.github.scroogemcfawk.csinvest.collector.csgostash

import com.github.scroogemcfawk.csinvest.domain.Misc
import com.github.scroogemcfawk.csinvest.domain.MiscBuilder
import com.github.scroogemcfawk.csinvest.domain.MiscType
import com.github.scroogemcfawk.csinvest.domain.Rarity
import com.github.scroogemcfawk.csinvest.utils.contains
import org.jsoup.select.Elements
import org.slf4j.LoggerFactory


class MiscCollector {

    private val log = LoggerFactory.getLogger(MiscCollector::class.java)

    fun get(): ArrayList<Misc> {
        val misc = ArrayList<Misc>()

        misc += getAgents()
        misc += getMusicKits()
        misc += getCollectibles()
        misc += getTagsAndTools()

        return misc
    }

    private fun getAgents(): ArrayList<Misc> {
        val agents = ArrayList<Misc>()

        val agentPageUrl = "https://csgostash.com/agents"
        val tiles = SectionScraper(agentPageUrl).get()

        val agentBuilder = MiscBuilder().withType(MiscType.AGENT)

        agents += fetchMiscFromTiles(tiles, agentBuilder)

        return agents
    }

    private fun getMusicKits(): ArrayList<Misc> {
        val musicKits = ArrayList<Misc>()

        val musicPageUrl = "https://csgostash.com/music"
        val tiles = SectionScraper(musicPageUrl).get()

        val musicKitBuilder = MiscBuilder().withType(MiscType.MUSIC_KIT)

        val musicKitBlanks = fetchMiscFromTiles(tiles, musicKitBuilder)

        for (m in musicKitBlanks) {
            val blank = m.builder
            blank.name = blank.name.replace("| By ", "| ")
            musicKits.add(blank.build())
        }

        return musicKits
    }

    private fun getCollectibles(): ArrayList<Misc> {
        val collectibles = ArrayList<Misc>()

        val collectiblePageUrl = "https://csgostash.com/pins"
        val tiles = SectionScraper(collectiblePageUrl).get()

        val collectibleBuilder = MiscBuilder().withType(MiscType.COLLECTIBLE)

        collectibles += fetchMiscFromTiles(tiles, collectibleBuilder)

        return collectibles
    }

    private fun getTagsAndTools(): ArrayList<Misc> {
        val tagsAndTools = ArrayList<Misc>()

        val keysAndOtherPageUrl = "https://csgostash.com/items"
        val tiles = SectionScraper(keysAndOtherPageUrl).get()

        val blankOtherItems = fetchMiscFromTiles(tiles)

        for (b in blankOtherItems) {
            // yeah, it's kinda bullshit, I know
            when(b.name) {
                in Regex("[\\w\\s]* Tag") -> {
                    tagsAndTools.add(b.builder.withType(MiscType.TAG).withRarity(Rarity.COMMON).build())
                }
                in Regex("[\\u2122\\w\\s]* Tool") -> {
                    tagsAndTools.add(b.builder.withType(MiscType.TOOL).withRarity(Rarity.COMMON).build())
                }
                else -> continue
            }
        }

        return tagsAndTools
    }

    private fun fetchMiscFromTiles(tiles: Elements, prototype: MiscBuilder = MiscBuilder()): ArrayList<Misc> {
        val res = ArrayList<Misc>()

        // TODO create shared method
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
