package com.github.scroogemcfawk.csinvest

import com.github.scroogemcfawk.csinvest.collector.ItemCollector
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean


@SpringBootApplication
open class Application {

    @Autowired
    lateinit var collector: ItemCollector

    @Bean
    open fun commandLineRunner(): CommandLineRunner {
        return CommandLineRunner {
            collector.getAll().forEach {
                println(it)
            }
        }
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}
