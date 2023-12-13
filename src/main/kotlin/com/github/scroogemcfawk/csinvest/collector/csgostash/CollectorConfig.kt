package com.github.scroogemcfawk.csinvest.collector.csgostash

import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Lazy


@Configuration
open class CollectorConfig {

    private val currencyCookie: String by lazy {
        Jsoup.connect("https://csgostash.com/setcurrency/USD")
            .get().connection().response().cookie("currency") ?: ""
    }

    @Lazy
    @Bean(name = ["homePage"])
    open fun homePage(): Document {
        return Jsoup.connect("https://csgostash.com/").get()
    }

    @Lazy
    @Bean(name = ["collectorContainer"])
    open fun collectorContainer(): ContainerCollector {
        return ContainerCollector()
    }

}
