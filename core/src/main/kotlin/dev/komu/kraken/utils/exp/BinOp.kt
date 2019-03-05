package dev.komu.kraken.utils.exp

enum class BinOp(val op: String) {
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
