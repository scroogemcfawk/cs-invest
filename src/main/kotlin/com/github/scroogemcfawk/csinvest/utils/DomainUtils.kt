package com.github.scroogemcfawk.csinvest.utils


fun normalizeItemString(name: String): String {
    return name.uppercase().trim().replace(" ", "_").replace("-", "_")
}
