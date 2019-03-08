package dev.komu.kraken.model

class Energy {
    var energy = 0

    fun gain(speed: Int) {
        energy += GAINS[speed]
    }


    val canTakeTurn: Boolean
        get() = energy >= ACTION_COST

    fun spend() {
        assert(energy >= ACTION_COST)
        energy -= ACTION_COST
    }

    companion object {
        const val MIN_SPEED = 0
        const val NORMAL_SPEED = 6
        const val MAX_SPEED = 12
        private const val ACTION_COST = 240

        private val GAINS = listOf(
            15,     // 1/4 normal speed
            20,     // 1/3 normal speed
            25,
            30,     // 1/2 normal speed
            40,
            50,
            60,     // normal speed
            80,
            100,
            120,    // 2x normal speed
            150,
            180,    // 3x normal speed
            240     // 4x normal speed
        )
    }
}
