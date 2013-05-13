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

import org.junit.Test as test
import org.junit.Assert.*

class ExpressionLexerTest {
    
    test fun lexing() {
        assertTokens("")
        assertTokens("foo", "foo")
        assertTokens("foo bar", "foo", "bar")
        assertTokens("foo(bar)", "foo", "(", "bar", ")")
        assertTokens("(1 + 20) * 3", "(", "1", "+", "20", ")", "*", "3")
        assertTokens("(1+2) * 3 / randint(4, 5)", "(", "1", "+", "2", ")", "*", "3", "/", "randint", "(", "4", ",", "5", ")")
    }
    
    test fun pushBack() {
        val lexer = ExpressionLexer("foo bar")

        assertEquals(TokenType.IDENTIFIER, lexer.next())
        assertEquals("foo", lexer.currentValue)

        lexer.pushBack()

        assertEquals(TokenType.IDENTIFIER, lexer.next())
        assertEquals("foo", lexer.currentValue)
        assertEquals(TokenType.IDENTIFIER, lexer.next())
        assertEquals("bar", lexer.currentValue)

        lexer.pushBack()

        assertEquals(TokenType.IDENTIFIER, lexer.next())
        assertEquals("bar", lexer.currentValue)
        assertEquals(TokenType.EOF, lexer.next())
    }

    class object {

        private fun assertTokens(exp: String, vararg expected: String) {
            assertEquals(expected.toList(), lex(exp))
        }

        private fun lex(exp: String): List<String> {
            val tokens = listBuilder<String>()
            val lexer = ExpressionLexer(exp)

            while (true) {
                val token = lexer.next()
                if (token == TokenType.EOF)
                    break

                if (token == TokenType.IDENTIFIER || token == TokenType.NUMBER)
                    tokens.add(lexer.currentValue.toString())
                else
                    tokens.add(token.toString())
            }

            return tokens.build()
        }
    }
}
