package com.github.scroogemcfawk.csinvest

import com.github.scroogemcfawk.csinvest.service.collector.CsgoStashItemCollector


fun main() {
    val c = CsgoStashItemCollector()
    c.getAll()
}
