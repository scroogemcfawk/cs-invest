package com.github.scroogemcfawk.csinvest.collector

import com.github.scroogemcfawk.csinvest.collector.csgostash.CompleteCollector
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration
open class ItemCollectorConfig {

    @Bean
    open fun getItemCollector(): ItemCollector {
        return CompleteCollector()
    }

}
