/*
 * Copyright 2013 The Wanhack Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.wanhack.utils.exp

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
        if (tok.allDigits()) {
            currentValue = Integer(tok)
            return TokenType.NUMBER
        } else {
            currentValue = tok
            return TokenType.IDENTIFIER
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

    class object {
        private fun String.allDigits() =
            this.all { Character.isDigit(it) }

        private fun Char.isIdentifierChar() =
            Character.isLetter(this) || Character.isDigit(this)
    }
}
