package dev.komu.kraken.model.common

data class Color(val r: Int, val g: Int, val b: Int) {

    fun darker(): Color =
        Color((0.7*r).toInt().coerceAtLeast(0),
              (0.7*g).toInt().coerceAtLeast(0),
              (0.7*b).toInt().coerceAtLeast(0))

    companion object {
        val ALUMINIUM       = Color(220, 230, 250)
        val BLACK           = Color(0, 0, 0)
        val BLACKISH        = Color(10, 10, 10)
        val BLUE            = Color(0, 0, 255)
        val BROWN           = Color(100, 100, 0)
        val BROWNISH        = Color(120, 100, 10)
        val CYAN            = Color(0, 255, 255)
        val DARK_GRAY       = Color(64, 64, 64)
        val DARK_GREEN      = Color(0, 130, 0)
        val GRAY            = Color(128, 128, 128)
        val GREEN           = Color(0, 150, 0)
        val LIGHT_BLUE      = Color(100, 100, 255)
        val LIGHT_BROWN     = Color(200, 200, 0)
        val RED             = Color(255, 0, 0)
        val WHITE           = Color(255, 255, 255)
        val WHITEISH        = Color(240, 240, 230)
        val YELLOW          = Color(255, 255, 0)
        val YELLOWISH       = Color(250, 240, 140)
    }
}
