package dev.komu.kraken.utils.exp

class VariableExpression(val name: String): Expression() {

    override fun evaluate(env: Map<String, Int>): Int =
        env[name] ?: throw EvaluationException("Unbound variable <$name>")

    override fun toString() = name
}
