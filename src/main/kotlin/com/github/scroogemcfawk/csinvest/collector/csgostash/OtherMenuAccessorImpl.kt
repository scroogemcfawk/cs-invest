package com.github.scroogemcfawk.csinvest.collector.csgostash

import jakarta.annotation.Resource
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.slf4j.LoggerFactory


class OtherMenuAccessorImpl : OtherMenuAccessor {

    private val log = LoggerFactory.getLogger(OtherMenuAccessor::class.java)

    @Resource(name = "homePage")
    private lateinit var homePage: Document

    override fun getSections(): HashMap<OtherMenuSection, Elements> {
        val splitByDivider = fetchOtherMenu()?.let { bar ->
            fetchSectionsFromMenu(bar)
        }

        val sections = HashMap<OtherMenuSection, Elements>()

        val expectedMapping = mapOf(
            OtherMenuSection.DEALS_AND_DISCOUNTS to 0,
            OtherMenuSection.MUSIC to 1,
            OtherMenuSection.AGENTS to 2,
            OtherMenuSection.PATCHES to 3,
            OtherMenuSection.PINS to 4,
            OtherMenuSection.GRAFFITI to 5,
            OtherMenuSection.KEYS_AND_OTHER_ITEMS to 6,
        )

        splitByDivider?.let {
            for ((k, v) in expectedMapping) {
                sections[k] = it[v]
            }
        } ?: log.debug("Unexpected empty section list in other drop down menu.")

        return sections
    }

    private fun fetchOtherMenu(): Element? {
        return homePage.selectFirst("ul.nav.navbar-nav li.dropdown:contains(Other)")
    }

    private fun fetchSectionsFromMenu(bar: Element): ArrayList<Elements> {
        val sectionsHTML = ArrayList(bar.html().split("<li class=\"divider\"></li>"))
        if (sectionsHTML.size != OtherMenuSection.DROPDOWN_MENU_EXPECTED_SECTION_COUNT) {
            log.warn("Unexpected section count found (${sectionsHTML.size}).")
        }
        return parseSections(sectionsHTML)
    }

    private fun parseSections(sections: ArrayList<String>): ArrayList<Elements> {
        return ArrayList(
            sections.map { Jsoup.parse(it).select("li:has(a)") }
        )
    }
}
