package dev.komu.kraken.utils.exp

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ExpressionLexerTest {

    @Test
    fun lexing() {
        assertTokens("")
        assertTokens("foo", "foo")
        assertTokens("foo bar", "foo", "bar")
        assertTokens("foo(bar)", "foo", "(", "bar", ")")
        assertTokens("(1 + 20) * 3", "(", "1", "+", "20", ")", "*", "3")
        assertTokens("(1+2) * 3 / randint(4, 5)", "(", "1", "+", "2", ")", "*", "3", "/", "randint", "(", "4", ",", "5", ")")
    }

    @Test
    fun pushBack() {
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

    companion object {

        private fun assertTokens(exp: String, vararg expected: String) {
            assertEquals(expected.toList(), lex(exp))
        }

        private fun lex(exp: String): List<String> {
            val tokens = mutableListOf<String>()
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

            return tokens
        }
    }
}
