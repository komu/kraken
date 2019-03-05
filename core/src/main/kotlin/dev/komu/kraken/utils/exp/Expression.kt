package dev.komu.kraken.utils.exp

import java.util.Collections.emptyMap
import java.util.Collections.singletonMap

abstract class Expression {

    fun evaluate(): Int =
        evaluate(emptyMap<String, Int>())

    fun evaluate(name: String, value: Int): Int =
        evaluate(singletonMap(name, value))

    abstract fun evaluate(env: Map<String, Int>): Int

    companion object {
        fun evaluate(exp: String): Int =
            parse(exp).evaluate()

        fun evaluate(exp: String, name: String, value: Int): Int =
            parse(exp).evaluate(name, value)

        fun evaluate(exp: String, env: Map<String, Int>): Int =
            parse(exp).evaluate(env)

        fun parse(exp: String): Expression =
            ExpressionParser(exp).parse()

        fun constant(value: Int): Expression =
            ConstantExpression(value)
    }
}
