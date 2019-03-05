package dev.komu.kraken.utils.exp

class ApplyExpression(val function: String, val args: List<Expression>): Expression() {

    override fun evaluate(env: Map<String, Int>): Int {
        val func = Functions.findFunction(function, args.size) ?: throw EvaluationException("No such function: $function/${args.size}")
        return func(args.map { it.evaluate(env) })
    }

    override fun toString() =
        function + args.joinToString(", ", "(", ")")
}
