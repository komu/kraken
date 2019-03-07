package dev.komu.kraken.utils.exp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExpressionTest {

    @Test
    fun simple() {
        assertExpression(0, "0")
        assertExpression(1, "1")
        assertExpression(5, "5")
        assertExpression(-5, "-5")
        assertExpression(5, "+5")
    }

    @Test
    fun additiveExpression()  {
        assertExpression(4, "2 + 2")
        assertExpression(3, "5 - 2")
        assertExpression(6, "1 + 2 + 3")
        assertExpression(2, "5 - 2 - 1")
    }

    @Test
    fun multiplicativeExpression()  {
        assertExpression(6, "2 * 3")
        assertExpression(11, "1 + 2 * 3 + 4")
    }

    @Test
    fun parenthesizedExpression() {
        assertExpression(21, "(1 + 2) * (3 + 4)")
    }

    @Test
    fun functionCalls() {
        assertExpression(2, "max(1, 2)")
    }

    @Test
    fun variables() {
        assertExpression(3, "one + two", mapOf("one" to 1, "two" to 2))
    }

    @Test
    fun diceSyntax() {
        assertExpression(2, "d1 + d1")
        assertExpression(4, "3d1 + 1")

        val result = Expression.evaluate("d4")
        assertTrue(result in 1..4)
    }

    companion object {

        fun assertExpression(expected: Int, exp: String) {
            assertEquals(expected, Expression.evaluate(exp))
        }

        fun assertExpression(expected: Int, exp: String, env: Map<String, Int>) {
            assertEquals(expected, Expression.evaluate(exp, env))
        }
    }
}
