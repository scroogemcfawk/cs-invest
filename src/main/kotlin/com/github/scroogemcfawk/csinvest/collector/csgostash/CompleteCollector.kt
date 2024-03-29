package com.github.scroogemcfawk.csinvest.collector.csgostash

import com.github.scroogemcfawk.csinvest.collector.ItemCollector
import com.github.scroogemcfawk.csinvest.domain.*
import jakarta.annotation.Resource
import org.slf4j.LoggerFactory


/**
 * Retrieves CS2 marketable items data from csgostash.com
 */
class CompleteCollector: ItemCollector {

    private val log = LoggerFactory.getLogger(CompleteCollector::class.java)

    @Resource(name = "containerCollector")
    private lateinit var containerCollector: ContainerCollector

    @Resource(name = "consumableCollector")
    private lateinit var consumableCollector: ConsumableCollector

    @Resource(name = "miscCollector")
    private lateinit var miscCollector: MiscCollector

    @Resource(name = "paintingCollector")
    private lateinit var paintingCollector: PaintingCollector

    override fun getAll(): ArrayList<Item> {
        val items = ArrayList<Item>()

        log.info("Get all items started.")

        items.addAll(getContainers())
        items.addAll(getConsumables())
        items.addAll(getMisc())
        items.addAll(getPaintings())

        log.info("Get all items complete.")

        return items
    }

    override fun getPaintings(): ArrayList<Painting> {
        return paintingCollector.get()
    }

    override fun getContainers(): ArrayList<Container> {
        return containerCollector.get()
    }

    override fun getConsumables(): ArrayList<Consumable> {
        return consumableCollector.get()
    }

    override fun getMisc(): ArrayList<Misc> {
        return miscCollector.get()
    }

}
