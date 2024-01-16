package com.github.scroogemcfawk.csinvest.utils


// convenient regex contains string for when(String)
operator fun Regex.contains(text: String): Boolean {
    return matches(text)
}
