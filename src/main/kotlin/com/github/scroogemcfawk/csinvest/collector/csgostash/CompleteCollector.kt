package com.github.scroogemcfawk.csinvest.collector.csgostash

import com.github.scroogemcfawk.csinvest.collector.ItemCollector
import com.github.scroogemcfawk.csinvest.domain.*
import jakarta.annotation.Resource
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service


/**
 * Retrieves data from csgostash.com
 */
class CompleteCollector: ItemCollector {

    private val log = LoggerFactory.getLogger(CompleteCollector::class.java)

    @Resource(name = "collectorContainer")
    private lateinit var containerCollector: ContainerCollector

    init {
        log.atLevel(Level.DEBUG)
    }

    override fun getAll(): ArrayList<Item> {
        val items = ArrayList<Item>()

        items.addAll(getContainers())

        return items
    }

    override fun getPaintings(): ArrayList<Item> {
        // TODO("Not yet implemented")
        return ArrayList()
    }

    override fun getContainers(): ArrayList<Container> {
        return containerCollector.get()
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
