package com.github.scroogemcfawk.csinvest.collector.csgostash

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.slf4j.LoggerFactory
import kotlin.math.min


/**
 * Retrieves all records of given page and following pages (if any).
 */
class SectionScraper(private val url: String) {

    // TODO maybe cache?

    private val log = LoggerFactory.getLogger(SectionScraper::class.java)

    init {
        log.atDebug()
    }

    private val initial: Document by lazy {
        Jsoup.connect(url).get()
    }

    val size: Int = run {
        val pagination = initial.selectFirst("ul.pagination")
        return@run pagination?.let { ul ->
            // take second element from the end
            ul.children().takeLast(2)[0].text().toInt()
        } ?: 0
    }

    fun get(pageLimit: Int = 0): Elements {
        log.debug("Scraping page '{}'", url)

        val result = Elements()

        val upperBound = if (pageLimit == 0) size else min(pageLimit, size)

        // first page
        result.addAll(getItemsFromPage(initial))
        log.debug("[1/{}] done", upperBound)

        // remaining pages
        for (pageNumber in 2..upperBound) {
            val page = Jsoup.connect("$url?page=$pageNumber").get()
            result.addAll(getItemsFromPage(page))
            log.debug("[{}/{}] done", pageNumber, upperBound)
        }

        return result
    }

    private fun getItemsFromPage(page: Document): Elements {
        // select all boxes, excluding advert
        val res = page.select("div.col-lg-4.col-md-6.col-widen.text-center:not(.adv-result-box-containers) > div:has(h3, h4)")
        if (res.size < 1) {
            log.warn("Unexpected empty tile list during page scraping.")
        }
        return res
    }

}
