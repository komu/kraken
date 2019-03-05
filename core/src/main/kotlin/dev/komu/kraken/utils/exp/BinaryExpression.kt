package dev.komu.kraken.utils.exp

class BinaryExpression(val op: BinOp, val lhs: Expression, val rhs: Expression): Expression() {

    override fun evaluate(env: Map<String, Int>) =
        op.evaluate(lhs.evaluate(env), rhs.evaluate(env))

    override fun toString() = "($lhs $op $rhs)"
}
