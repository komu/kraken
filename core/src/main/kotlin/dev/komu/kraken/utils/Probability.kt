package dev.komu.kraken.utils

class Probability(private val percentage: Int) {

    init {
        require(percentage in 0..100)
    }

    fun check() = check(percentage)

    companion object {
        fun check(percentage: Int): Boolean {
            require(percentage in 0..100)
            return percentage > randomInt(100)
        }
    }
}
