package com.github.scroogemcfawk.csinvest.collector.csgostash

//@Configuration
//private open class CsgoStashCollectorConfig {
//
//    private val currencyCookie: String by lazy {
//        Jsoup.connect("https://csgostash.com/setcurrency/USD")
//            .get().connection().response().cookie("currency") ?: ""
//    }
//
//    @Bean(name = ["skinCasesSectionScraper"])
//    open fun getSkinCasesSectionScraper(): SectionScraper {
//        return SectionScraper("https://csgostash.com/containers/skin-cases")
//    }
//
//}
