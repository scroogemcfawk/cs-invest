package com.github.scroogemcfawk.csinvest.collector.csgostash

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.slf4j.LoggerFactory


/**
 * Retrieves all records of given page and following pages (if any).
 */
class SectionScraper(private val url: String) {

    private val log = LoggerFactory.getLogger(SectionScraper::class.java)

    private val initial: Document by lazy {
//        Jsoup.connect(url)
//            .header("Accept-Language", "en-US")
//            .cookie("currency", currencyCookie)
//            .get()
        Jsoup.connect(url).get()
    }

    val size: Int = run {
        val pagination = initial.selectFirst("ul.pagination")
        return@run pagination?.let { ul ->
            // take second element from the end
            ul.children().takeLast(2)[0].text().toInt()
        } ?: 0
    }

    fun get(): Elements {
        val result = Elements()

        // first page
        result.addAll(getItemsFromPage(initial))

        // remaining pages
        for (pageNumber in 2..size) {
            val page = Jsoup.connect("$url?page=$pageNumber").get()
            result.addAll(getItemsFromPage(page))
        }

        return result
    }

    private fun getItemsFromPage(page: Document): Elements {
        // select all boxes, excluding advert
        return page.select("div.col-lg-4.col-md-6.col-widen.text-center:not(.adv-result-box-containers) > div:has(h4)")
    }

}
