package dev.komu.kraken.utils.exp

import dev.komu.kraken.utils.randomInt
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.min

object Functions {

    private val functions: Map<Pair<String, Int>, (List<Int>) -> Int> = mapOf(
        ("abs" to 1) to { args -> args[0].absoluteValue },
        ("max" to 2) to { args -> max(args[0], args[1]) },
        ("min" to 2) to { args -> min(args[0], args[1]) },
        ("randint" to 1) to { args -> randomInt(args[0]) },
        ("randint" to 2) to { args -> randomInt(args[0], args[1]) })

    fun findFunction(name: String, arity: Int): ((List<Int>) -> Int)? = functions[name to arity]
}
