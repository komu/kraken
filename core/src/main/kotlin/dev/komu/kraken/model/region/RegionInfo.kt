package dev.komu.kraken.model.region

class RegionInfo(val id: String, val level: Int, val random: Boolean) {

    var previous: RegionInfo? = null
    var next: RegionInfo? = null
}
