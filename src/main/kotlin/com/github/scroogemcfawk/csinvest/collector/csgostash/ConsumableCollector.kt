package com.github.scroogemcfawk.csinvest.collector.csgostash

import com.github.scroogemcfawk.csinvest.domain.Consumable


class ConsumableCollector {

    fun get(): ArrayList<Consumable> {
        val consumables = ArrayList<Consumable>()

        consumables.addAll(fetchStickers())
        consumables.addAll(fetchPatches())
        consumables.addAll(fetchKeysAndPasses())
        consumables.addAll(fetchGraffities())

        return consumables
    }

    private fun fetchStickers(): ArrayList<Consumable> {
        val stickers = ArrayList<Consumable>()
        // TODO("Not yet implemented")
        return stickers
    }

    private fun fetchPatches(): ArrayList<Consumable> {
        val patches = ArrayList<Consumable>()
        // TODO("Not yet implemented")
        return patches
    }

    private fun fetchKeysAndPasses(): ArrayList<Consumable> {
        val keysAndPasses = ArrayList<Consumable>()
        // TODO("Not yet implemented")
        return keysAndPasses
    }

    private fun fetchGraffities(): ArrayList<Consumable> {
        val graffities = ArrayList<Consumable>()
        // TODO("Not yet implemented")
        return graffities
    }

}
