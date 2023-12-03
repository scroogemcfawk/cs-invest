package com.github.scroogemcfawk.csinvest.service.collector

import com.github.scroogemcfawk.csinvest.domain.Item

interface ItemCollector {
    fun getAll(): ArrayList<Item>
    fun getPaintings(): ArrayList<Item>
    fun getContainers(): ArrayList<Item>
    fun getConsumables(): ArrayList<Item>
    fun getMisc(): ArrayList<Item>
}
