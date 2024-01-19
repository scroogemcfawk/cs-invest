package com.github.scroogemcfawk.csinvest.collector.csgostash

import com.github.scroogemcfawk.csinvest.domain.*
import com.github.scroogemcfawk.csinvest.utils.normalizeItemString
import jakarta.annotation.Resource
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.slf4j.LoggerFactory


class PaintingCollector {

    private val log = LoggerFactory.getLogger(PaintingCollector::class.java)

    @Resource(name = "homePage")
    private lateinit var homePage: Document

    private val menu: Elements by lazy { homePage.select("#navbar-expandable > ul") }

    fun get(): ArrayList<Painting> {
        val paintings = ArrayList<Painting>()

        log.info("Get paintings started.")

        paintings += getPistols()
        paintings += getMidTier()
        paintings += getRifles()
        paintings += getKnives()
        paintings += getGloves()

        log.info("Get paintings complete.")

        return paintings
    }

    private fun getPistols(): ArrayList<Painting> {
        log.info("Get pistols started.")
        val res = fetchPaintingsFromDropDownMenu("Pistols")
        log.info("Get pistols complete.")
        return res
    }

    private fun getMidTier(): ArrayList<Painting> {
        log.info("Get mid-tier started.")
        val res = fetchPaintingsFromDropDownMenu("Mid-Tier")
        log.info("Get mid-tier started.")
        return res
    }

    private fun getRifles(): ArrayList<Painting> {
        log.info("Get rifles started.")
        val res = fetchPaintingsFromDropDownMenu("Rifles")
        log.info("Get rifles complete.")
        return res
    }

    private fun getKnives(): ArrayList<Painting> {
        log.info("Get knives started.")
        val res = getItemFromBasePage("https://csgostash.com/skin-rarity/Knife")
        log.info("Get knives complete.")
        return res
    }

    private fun getGloves(): ArrayList<Painting> {
        log.info("Get gloves started.")
        val res = getItemFromBasePage("https://csgostash.com/gloves")
        log.info("Get gloves complete.")
        return res
    }

    private fun fetchPaintingsFromDropDownMenu(menuName: String): ArrayList<Painting> {
        val res = ArrayList<Painting>()

        val menuItems = menu.select("li:contains($menuName) li:not(dropdown-header)")

        for (a in menuItems.select("a")) {
            res += getItemFromBasePage(a.attr("href"))
        }

        return res
    }

    private fun getItemFromBasePage(pageUrl: String): ArrayList<Painting> {
        val res = ArrayList<Painting>()

        val tiles = try {
            SectionScraper(pageUrl).get()
        } catch (e: Exception) {
            log.warn("Could not get page {}", pageUrl)
            return res
        }

        for (t in tiles) {
            val url = t.select("a:has(img)").attr("href")
            if (url.isBlank()) continue
            try {
                res += getItemFromSkinPage(url)
            } catch (e: Exception) {
                log.warn("Could not get item from skin page {}", url)
            }
        }

        return res
    }

    private fun getItemFromSkinPage(url: String): ArrayList<Painting> {
        val page = Jsoup.connect(url).get()

        val res = ArrayList<Painting>()

        val tile = page.select("div.well.result-box.nomargin")
        val qualifiedName = tile.select("h2").text()
        val base = Base.valueOf(qualifiedName.split(" | ")[0])
        val skin = qualifiedName.split(" | ")[1].replace("(Vanilla)", "").trim()

        val rarity = try {
            Rarity.fromString(tile.select("div.quality")[0].className().split("-").last())
        } catch (_: Exception) {
            Rarity.UNDEFINED
        }

        val proto = PaintingBuilder().withName(skin).withRarity(rarity).withBase(base)

        val tab = page.select("div.tab-pane.active")

        val categoryList = tab.select("a.market-button-skin:not(.disabled), a.market-button-item:not(.disabled)")

        for (a in categoryList) {
            val pl = a.select("span.pull-left")
            if (pl.size == 0) continue
            val exteriorCategory = pl.joinToString("|") { it.text() }
            processExteriorCategory(exteriorCategory, proto)
            val obj = proto.build()
            res.add(obj)
        }

        return res
    }

    private fun processExteriorCategory(exteriorCategory: String, proto: PaintingBuilder) {
        val (category, exterior) = if ("|" in exteriorCategory) {
            val split = exteriorCategory.split("|")
            val cat = Category.valueOf(normalizeItemString(split[0]))
            val ext = normalizeItemString(split[1]).let {
                if ("VANILLA" in it) Exterior.NOT_PAINTED
                else Exterior.valueOf(it)
            }
            Pair(cat, ext) // return
        } else {
            val ext = normalizeItemString(exteriorCategory).let {
                if ("VANILLA" in it) Exterior.NOT_PAINTED
                else Exterior.valueOf(it)
            }
            Pair(Category.NORMAL, ext) // return
        }
        proto.category = category
        proto.exterior = exterior
    }

}
