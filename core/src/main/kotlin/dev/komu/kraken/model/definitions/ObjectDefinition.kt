package dev.komu.kraken.model.definitions

import dev.komu.kraken.utils.randomInt

abstract class ObjectDefinition<out T> {
    abstract val level: Int?
    var probability = 100

    abstract fun create(): T
}

fun <T : ObjectDefinition<*>> Collection<T>.weightedRandom(): T =
    ProbabilityDistribution(this).randomItem()

class ProbabilityDistribution<T : ObjectDefinition<*>>(private val items: Collection<T>) {
    private val probabilitySum = items.sumBy { it.probability }

    fun randomItem(): T {
        var item = randomInt(probabilitySum)
        for (dp in items) {
            if (item < dp.probability)
                return dp

            item -= dp.probability
        }

        error("could not randomize definition")
    }
}

fun <T : ObjectDefinition<*>> Collection<T>.betweenLevels(minLevel: Int, maxLevel: Int): List<T> =
    filter {
        val level = it.level
        level == null || (level in minLevel..maxLevel)
    }
