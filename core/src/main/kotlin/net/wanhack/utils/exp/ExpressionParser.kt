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

import net.wanhack.utils.exp.TokenType.*
import java.util.*
import java.util.regex.Pattern

open class ExpressionParser(val expression: String) {

    private val lexer = ExpressionLexer(expression)

    fun parse() =
        parseExpression()

    /**
     * expr = factor
     *      | factor + expr
     *      | factor - expr
     */
    private fun parseExpression(): Expression {
        var exp = parseFactor()
        while (true) {
            val next = nextToken()
            when (next) {
                PLUS  -> exp = BinaryExpression(BinOp.ADD, exp, parseFactor())
                MINUS -> exp = BinaryExpression(BinOp.SUB, exp, parseFactor())
                else -> {
                    lexer.pushBack()
                    return exp
                }
            }
        }
    }

    /**
     * factor = term
     *        | term * factor
     *        | term / factor
     *        | term % factor
     */
    private fun parseFactor(): Expression {
        var factor = parseTerm()
        while (true) {
            val next = nextToken()
            when (next) {
                MUL -> factor = BinaryExpression(BinOp.MUL, factor, parseTerm())
                DIV -> factor = BinaryExpression(BinOp.DIV, factor, parseTerm())
                MOD -> factor = BinaryExpression(BinOp.MOD, factor, parseTerm())
                else -> {
                    lexer.pushBack()
                    return factor
                }
            }
        }
    }

    /**
     * term = IDENT argument_list
     *      | IDENT
     *      | die_exp
     *      | ( expr )
     */
    private fun parseTerm(): Expression {
        val token = nextToken()
        if (token == TokenType.IDENTIFIER) {
            val name = lexer.currentValue as String
            if (nextToken() == TokenType.LPAR) {
                lexer.pushBack()
                val args = parseArgumentList()
                return ApplyExpression(name, args)
            } else {
                lexer.pushBack()
                return parseVariableOrDie(name)
            }
        } else if (token == TokenType.LPAR) {
            val term = parseExpression()
            assertToken(TokenType.RPAR)
            return term
        } else {
            lexer.pushBack()
            return ConstantExpression(getNumber())
        }
    }

    /**
     * argumentList = ()
     *              | (nonEmptyExplist)
     */
    private fun parseArgumentList(): List<Expression> {
        assertToken(TokenType.LPAR)
        if (nextToken() == TokenType.RPAR) {
            return Collections.emptyList()
        } else {
            lexer.pushBack()
            val exps = parseNonEmptyExpList()
            assertToken(TokenType.RPAR)
            return exps
        }
    }

    /**
     * nonEmptyExpList = exp
     *                 | exp, explist
     */
    private fun parseNonEmptyExpList(): List<Expression> {
        val result = mutableListOf<Expression>()
        while (true) {
            result.add(parseExpression())
            if (nextToken() != TokenType.COMMA) {
                lexer.pushBack()
                return result
            }
        }
    }

    private fun assertToken(token: TokenType) {
        if (nextToken() != token)
            throw ParseException("invalid expression <$expression>, expected: $token")
    }

    private fun getNumber(): Int {
        var sign = 1
        var tokenType = nextToken()
        if (tokenType == TokenType.PLUS || tokenType == TokenType.MINUS) {
            sign = if (tokenType == TokenType.PLUS) 1 else -1
            tokenType = nextToken()
        }

        if (tokenType == TokenType.NUMBER) {
            return sign * (lexer.currentValue as Int)
        } else {
            throw ParseException("expected number, got $tokenType")
        }
    }

    private fun nextToken(): TokenType =
        lexer.next()

    companion object {
        private val DIE_PATTERN = Pattern.compile("(\\d*)d(\\d+)")

        private fun parseVariableOrDie(token: String): Expression {
            val m = DIE_PATTERN.matcher(token)
            if (m.matches()) {
                val dieCount = m.group(1)
                val multiplier = if (dieCount != null && dieCount != "") dieCount.toInt() else 1
                val sides = m.group(2)!!.toInt()
                return DieExpression(multiplier, sides)
            } else {
                return VariableExpression(token)
            }
        }
    }
}
