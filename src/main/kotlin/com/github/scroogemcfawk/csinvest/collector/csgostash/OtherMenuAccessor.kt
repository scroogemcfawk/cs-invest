package com.github.scroogemcfawk.csinvest.collector.csgostash

import org.jsoup.select.Elements


interface OtherMenuAccessor {

    fun getSections(): HashMap<OtherMenuSection, Elements>

}
