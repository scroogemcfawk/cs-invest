package com.github.scroogemcfawk.csinvest.collector.csgostash

import com.github.scroogemcfawk.csinvest.domain.Container
import com.github.scroogemcfawk.csinvest.domain.ContainerBuilder
import com.github.scroogemcfawk.csinvest.domain.ContainerType
import com.github.scroogemcfawk.csinvest.domain.Rarity
import jakarta.annotation.Resource
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

@Component
class ContainerCollector {

    private companion object {
        private const val DROPDOWN_MENU_EXPECTED_SECTION_COUNT = 7
    }

    private val log = LoggerFactory.getLogger(ContainerCollector::class.java)

    @Resource(name = "homePage")
    private lateinit var homePage: Document

    fun get(): ArrayList<Container> {
        val containers = ArrayList<Container>()
        containers.addAll(fetchContainersFromNavigationPage())
        containers.addAll(fetchContainersFromOtherMenu())
        return containers
    }

    private fun fetchContainersFromNavigationPage(): ArrayList<Container> {
        val containers = ArrayList<Container>()
        // WEAPON_CASE (ALL)
        // SOUVENIR_PACKAGE (ALL)
        // GIFT_PACKAGE (ALL)
        // STICKER_CAPSULE (EXCEPT 'Keys & Other Items' PAGE (I don't know why T~T))
        val containerNavigationPage: Document by lazy {
            Jsoup.connect("https://csgostash.com/containers").get()
        }
        // Fetching items in all subpages
        for (element in containerNavigationPage.select(".jumbotron ul li h4")) {
            val name = element.text()
            val url = element.selectFirst("a")?.attr("href") ?: ""
            if (url.isNotBlank()) {
                log.debug("Fetching Container subpage '$name' via $url")
                containers.addAll(fetchContainersFromPages(url))
            } else {
                log.warn("Blank 'href' attribute of (Container > $name) link.")
            }
        }
        log.debug("Container Page: \n{}", containers.joinToString(separator = "\n\t") { it.toString() })
        log.debug("Container Page size: {} items", containers.size)
        return containers
    }

    private fun fetchContainersFromPages(url: String): Collection<Container> {
        val elements = SectionScraper(url).get()
        val containerBuilder = ContainerBuilder()
        containerBuilder.rarity = Rarity.COMMON
        containerBuilder.type = when (url.split("/").last()) {
            "skin-cases" -> {
                ContainerType.WEAPON_CASE
            }
            "souvenir-packages" -> {
                ContainerType.SOUVENIR_PACKAGE
            }
            "sticker-capsules", "autograph-capsules"-> {
                ContainerType.STICKER_CAPSULE
            }
            "gift-packages" -> {
                ContainerType.GIFT_PACKAGE
            }
            // when function fetches items from Other Items pages (only 2 sticker capsules for some fucking reason)  >:(  <- angry me
            in Regex("items(\$|\\?[a-z=0-9]*)") -> {
                ContainerType.STICKER_CAPSULE
            }
            else -> {
                log.warn("Unexpected container type found.")
                return ArrayList()
            }
        }

        val containers = ArrayList<Container>()

        for (element in elements) {
            element.selectFirst("h4")?.let { h4 ->
                containerBuilder.name = h4.text()
                containers.add(
                    containerBuilder.build()
                )
            } ?: {
                log.warn("'h4' not found. ($url, ${element.html()})")
            }
        }

        return containers
    }

    private fun fetchContainersFromOtherMenu(): Collection<Container> {

        val containers = ArrayList<Container>()

        fetchOtherMenu()?.let { bar ->
            val sections = fetchSectionsFromMenu(bar)

            val musicElements = sections[1]
            val patchElements = sections[3]
            val pinElements = sections[4]

            val graffitiElements = sections[5]
            // for the last 2 sticker capsules  >:(  <- angry me
            val otherHTML = sections[6]

            // all containers have COMMON rarity
            val containerBuilder = ContainerBuilder().withRarity(Rarity.COMMON)

            containers.addAll(fetchMusicKitBoxes(containerBuilder, musicElements))
            containers.addAll(fetchPinPacks(containerBuilder, pinElements))
            containers.addAll(fetchPatchPacks(containerBuilder, patchElements))
            containers.addAll(fetchGraffitiBoxes(containerBuilder, graffitiElements))
            containers.addAll(fetchContainersFromOtherPage(otherHTML))

        } ?: {
            log.warn("'Other' menu not found.")
        }

        return containers
    }

    private fun fetchOtherMenu(): Element? {
        return homePage.selectFirst("ul.nav.navbar-nav li.dropdown:contains(Other)")
    }

    private fun fetchSectionsFromMenu(bar: Element): ArrayList<Elements> {
        val sectionsHTML = ArrayList(bar.html().split("<li class=\"divider\"></li>"))
        if (sectionsHTML.size != DROPDOWN_MENU_EXPECTED_SECTION_COUNT) {
            log.warn("Unexpected section count found (${sectionsHTML.size}).")
        }
        return parseSections(sectionsHTML)
    }

    private fun parseSections(sections: ArrayList<String>): ArrayList<Elements> {
        return ArrayList(
            sections.map { Jsoup.parse(it).select("li:has(a)") }
        )
    }

    private fun fetchMusicKitBoxes(prototype: ContainerBuilder, music: Elements): ArrayList<Container> {
        log.debug("Fetching MUSIC_KIT_BOX Containers")

        val containers = ArrayList<Container>()

        // using prototype so it doesn't change outside of function
        val containerBuilder = prototype.withType(ContainerType.MUSIC_KIT_BOX)

        for (element in music) {
            element.selectFirst("a")?.let { a ->
                if ("Kits" !in a.text()) {
                    containers.add(
                        containerBuilder.withName(
                            Jsoup.connect(a.attr("href")).get().select("h1.margin-top-sm").text()
                        ).build()
                    )
                }
            } ?: {
                log.warn("'a' not found.")
            }
        }

        return containers
    }

    private fun fetchPatchPacks(prototype: ContainerBuilder, patch: Elements): ArrayList<Container> {
        log.debug("Fetching PATCH_PACK Containers")

        val containers = ArrayList<Container>()

        // using prototype so it doesn't change outside of function
        val containerBuilder = prototype.withType(ContainerType.PATCH_PACK)

        for (element in patch) {
            element.selectFirst("a")?.let { a ->
                if ("Pack" in a.text()) {
                    containers.add(
                        containerBuilder.withName(
                            Jsoup.connect(a.attr("href")).get().select("h1.margin-top-sm").text()
                        ).build()
                    )
                }
            } ?: {
                log.warn("'a' not found.")
            }
        }

        return containers
    }

    private fun fetchPinPacks(prototype: ContainerBuilder, pin: Elements): ArrayList<Container> {
        log.debug("Fetching PIN_PACK Containers")

        val containers = ArrayList<Container>()

        // using prototype so it doesn't change outside of function
        val containerBuilder = prototype.withType(ContainerType.PIN_PACK)

        for (element in pin) {
            element.selectFirst("a")?.let { a ->
                if ("Collectable" !in a.text()) {
                    containers.add(
                        containerBuilder.withName(
                            Jsoup.connect(a.attr("href")).get().select("h1.margin-top-sm").text()
                        ).build()
                    )
                }
            } ?: {
                log.warn("'a' not found.")
            }
        }

        return containers
    }

    private fun fetchGraffitiBoxes(prototype: ContainerBuilder, graffiti: Elements): ArrayList<Container> {
        log.debug("Fetching GRAFFITI_BOX Containers")

        val containers = ArrayList<Container>()

        // using prototype so it doesn't change outside of function
        val containerBuilder = prototype.withType(ContainerType.GRAFFITI_BOX)

        for (element in graffiti) {
            element.selectFirst("a")?.let { a ->
                if ("Box" in a.text()) {
                    containers.add(
                        containerBuilder.withName(
                            Jsoup.connect(a.attr("href")).get().select("h1.margin-top-sm").text()
                        ).build()
                    )
                }
            } ?: {
                log.warn("'a' not found.")
            }
        }

        return containers
    }

    private fun fetchContainersFromOtherPage(other: Elements): ArrayList<Container> {
        log.debug("Fetching other STICKER_CAPSULE Containers")

        val containers = ArrayList<Container>()

        if (other.size != 1) {
            log.warn("Unexpected section size found.")
            return containers
        }

        other[0].selectFirst("a")?.let { a ->
            containers.addAll(
                fetchContainersFromPages(a.attr("href")).filter { "ESL Cologne 2014" in it.name }
            )
        } ?: {
            log.warn("'a' not found.")
        }

        return containers
    }

    private operator fun Regex.contains(text: String): Boolean {
        return matches(text)
    }

}
