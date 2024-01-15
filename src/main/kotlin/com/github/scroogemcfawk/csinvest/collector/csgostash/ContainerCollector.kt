package com.github.scroogemcfawk.csinvest.collector.csgostash

import com.github.scroogemcfawk.csinvest.domain.Container
import com.github.scroogemcfawk.csinvest.domain.ContainerBuilder
import com.github.scroogemcfawk.csinvest.domain.ContainerType
import com.github.scroogemcfawk.csinvest.domain.Rarity
import jakarta.annotation.Resource
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.slf4j.LoggerFactory


class ContainerCollector {

    private val log = LoggerFactory.getLogger(ContainerCollector::class.java)

//    @Resource(name = "homePage")
//    private lateinit var homePage: Document

    @Resource(name = "otherMenuAccessor")
    private lateinit var otherMenuAccessor: OtherMenuAccessor

    fun get(): ArrayList<Container> {
        val containers = ArrayList<Container>()
        containers.addAll(
            fetchContainersFromNavigationPage() + fetchContainersFromOtherMenu()
        )
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

        otherMenuAccessor.getSections().let { bar ->
            // all containers have COMMON rarity
            val containerBuilder = ContainerBuilder().withRarity(Rarity.COMMON)

            try {
                containers.addAll(
                    fetchMusicKitBoxes(containerBuilder, bar[OtherMenuSection.MUSIC]!!) +
                    fetchPinPacks(containerBuilder, bar[OtherMenuSection.PINS]!!) +
                    fetchPatchPacks(containerBuilder, bar[OtherMenuSection.PATCHES]!!) +
                    fetchGraffitiBoxes(containerBuilder, bar[OtherMenuSection.GRAFFITI]!!) +
                    // for the last 2 sticker capsules  >:(  <- angry me
                    fetchContainersFromOtherPage(bar[OtherMenuSection.KEYS_AND_OTHER_ITEMS]!!)
                )
            } catch (_: Exception) {
                log.warn("Failed to fetch items from other menu.")
            }
        }

        return containers
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
