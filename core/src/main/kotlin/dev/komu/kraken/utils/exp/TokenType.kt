package dev.komu.kraken.utils.exp

enum class TokenType(val s: String) {
    NUMBER("number"),
    IDENTIFIER("identifier"),
    LPAR("("),
    RPAR(")"),
    PLUS("+"),
    MINUS("-"),
    MUL("*"),
    DIV("/"),
    MOD("%"),
    COMMA(","),
    EOF("eof");

    override fun toString() = s
}
