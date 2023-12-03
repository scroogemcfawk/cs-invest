package com.github.scroogemcfawk.csinvest.service.collector

import com.github.scroogemcfawk.csinvest.domain.Item
import org.jsoup.Jsoup

class CsgoStashItemCollector : ItemCollector {

    override fun getAll(): ArrayList<Item> {
        // TODO("Not yet implemented")
        val doc = Jsoup.connect("https://csgostash.com/").get()
        println(doc.body())
        return ArrayList()
    }

    override fun getPaintings(): ArrayList<Item> {
        // TODO("Not yet implemented")
        return ArrayList()
    }

    override fun getContainers(): ArrayList<Item> {
        // TODO("Not yet implemented")
        return ArrayList()
    }

    override fun getConsumables(): ArrayList<Item> {
        // TODO("Not yet implemented")
        return ArrayList()
    }

    override fun getMisc(): ArrayList<Item> {
        // TODO("Not yet implemented")
        return ArrayList()
    }
}
