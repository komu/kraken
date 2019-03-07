package dev.komu.kraken.utils.exp

import dev.komu.kraken.utils.rollDie
import java.util.Collections.emptyMap

sealed class Expression {

    abstract fun evaluate(env: Map<String, Int> = emptyMap()): Int

    class Apply(private val function: String, private val args: List<Expression>): Expression() {

        override fun evaluate(env: Map<String, Int>): Int {
            val func = Functions.findFunction(function, args.size) ?: throw EvaluationException("No such function: $function/${args.size}")
            return func(args.map { it.evaluate(env) })
        }

        override fun toString() =
            function + args.joinToString(", ", "(", ")")
    }

    class Binary(private val op: BinOp, private val lhs: Expression, private val rhs: Expression): Expression() {

        override fun evaluate(env: Map<String, Int>) =
            op.evaluate(lhs.evaluate(env), rhs.evaluate(env))

        override fun toString() = "($lhs $op $rhs)"
    }

    class Constant(private val value: Int): Expression() {

        override fun evaluate(env: Map<String, Int>) = value

        override fun toString() = value.toString()
    }

    class Die(private val multiplier: Int, private val sides: Int): Expression() {

        override fun evaluate(env: Map<String, Int>) =
            rollDie(sides, multiplier)

        override fun toString() = "${multiplier}d$sides"
    }

    class Variable(private val name: String): Expression() {

        override fun evaluate(env: Map<String, Int>): Int =
            env[name] ?: throw EvaluationException("Unbound variable <$name>")

        override fun toString() = name
    }

    companion object {

        fun evaluate(exp: String, env: Map<String, Int> = emptyMap()): Int =
            parse(exp).evaluate(env)

        fun parse(exp: String): Expression =
            ExpressionParser(exp).parse()
    }
}

enum class BinOp(private val op: String) {
    ADD("+") {
        override fun evaluate(lhs: Int, rhs: Int) = lhs + rhs
    },
    SUB("-") {
        override fun evaluate(lhs: Int, rhs: Int) = lhs - rhs
    },
    MUL("*") {
        override fun evaluate(lhs: Int, rhs: Int) = lhs * rhs
    },
    DIV("/") {
        override fun evaluate(lhs: Int, rhs: Int) = lhs / rhs
    },
    MOD("%") {
        override fun evaluate(lhs: Int, rhs: Int) = lhs % rhs
    };

    abstract fun evaluate(lhs: Int, rhs: Int): Int

    override fun toString() = op
}

class EvaluationException(message: String): RuntimeException(message)
