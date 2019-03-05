package dev.komu.kraken.utils.exp

class ConstantExpression(val value: Int): Expression() {

    override fun evaluate(env: Map<String, Int>) = value

    override fun toString() = value.toString()
}
