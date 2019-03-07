package dev.komu.kraken.utils.exp

class ExpressionLexer(private val str: String) {

    private var pos = 0
    var currentValue: Any? = null
    private var oldPos = -1

    fun next(): TokenType {
        currentValue = null
        if (!hasNext())
            return TokenType.EOF

        oldPos = pos
        val first = str[pos++]
        when (first) {
            '(' -> return TokenType.LPAR
            ')' -> return TokenType.RPAR
            '+' -> return TokenType.PLUS
            '-' -> return TokenType.MINUS
            '*' -> return TokenType.MUL
            '/' -> return TokenType.DIV
            '%' -> return TokenType.MOD
            ',' -> return TokenType.COMMA
            else -> pos--
        }

        val start = pos++
        while (pos < str.length && str[pos].isLetterOrDigit())
            pos++

        val tok = str.substring(start, pos)
        return if (tok.all { it.isDigit() }) {
            currentValue = tok.toInt()
            TokenType.NUMBER
        } else {
            currentValue = tok
            TokenType.IDENTIFIER
        }
    }

    fun pushBack() {
        check(oldPos != -1) { "no tokens read: can't pushback" }

        pos = oldPos
    }

    fun hasNext(): Boolean {
        skipWhitespace()
        return pos < str.length
    }

    private fun skipWhitespace() {
        while (pos < str.length && str[pos].isWhitespace())
            pos++
    }
}
