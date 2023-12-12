package com.github.scroogemcfawk.csinvest.collector

import com.github.scroogemcfawk.csinvest.domain.Item

interface ItemCollector {
    fun getAll(): ArrayList<out Item>
    fun getPaintings(): ArrayList<out Item>
    fun getContainers(): ArrayList<out Item>
    fun getConsumables(): ArrayList<out Item>
    fun getMisc(): ArrayList<out Item>
}
