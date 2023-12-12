package com.github.scroogemcfawk.csinvest.collector.csgostash

import com.github.scroogemcfawk.csinvest.collector.ItemCollector
import com.github.scroogemcfawk.csinvest.domain.*
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import org.springframework.beans.factory.annotation.Autowired


/**
 * Retrieves data from csgostash.com
 */
class CompleteCollector: ItemCollector {

    private val log = LoggerFactory.getLogger(CompleteCollector::class.java)

    @Autowired
    private lateinit var containerCollector: ContainerCollector

    init {
        log.atLevel(Level.DEBUG)
    }

    override fun getAll(): ArrayList<Item> {
        // TODO("Not yet implemented")
        return ArrayList()
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
