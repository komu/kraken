package dev.komu.kraken.utils.exp

class ExpressionLexer(val str: String) {

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
        while (pos < str.length && str[pos].isIdentifierChar())
            pos++

        val tok = str.substring(start, pos)
        return if (tok.allDigits()) {
            currentValue = tok.toInt()
            TokenType.NUMBER
        } else {
            currentValue = tok
            TokenType.IDENTIFIER
        }
    }

    fun pushBack() {
        if (oldPos == -1)
            throw IllegalStateException("no tokens read: can't pushback")

        pos = oldPos
    }

    fun hasNext(): Boolean {
        skipWhitespace()
        return pos < str.length
    }

    private fun skipWhitespace() {
        while (pos < str.length && Character.isWhitespace(str[pos]))
            pos++
    }

    companion object {
        private fun String.allDigits() =
            this.all { Character.isDigit(it) }

        private fun Char.isIdentifierChar() =
            Character.isLetter(this) || Character.isDigit(this)
    }
}
