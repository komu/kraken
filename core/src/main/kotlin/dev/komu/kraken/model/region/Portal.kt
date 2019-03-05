package dev.komu.kraken.model.region

class Portal(region: String, location: String, val up: Boolean) {
    private val target = JumpTarget(region, location)

    fun getTarget(up: Boolean): JumpTarget? =
        if (up == this.up) target else null
}
