/*
 *  Copyright 2005 The Wanhack Team
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package net.wanhack.utils.exp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class ExpressionParser {

    private final String expression;
    private final ExpressionLexer lexer;
    private static final Pattern DIE_PATTERN = Pattern.compile("(\\d*)d(\\d+)");
    
    public ExpressionParser(String expression) {
        this.expression = expression;
        this.lexer = new ExpressionLexer(expression);
    }

    public Expression parse() {
        return parseExpression();
    }
    
    /**
     * expr = factor
     *      | factor + expr
     *      | factor - expr 
     */
    private Expression parseExpression() {
        Expression exp = parseFactor();
        
        while (true) {
            TokenType next = nextToken();
            switch (next) {
            case PLUS:
                exp = new BinaryExpression(BinOp.ADD, exp, parseFactor());
                break;
            case MINUS:
                exp = new BinaryExpression(BinOp.SUB, exp, parseFactor());
                break;
            default:    
                lexer.pushBack(); 
                return exp;
            }
        }
    }
    
    /**
     * factor = term
     *        | term * factor
     *        | term / factor
     *        | term % factor
     */
    private Expression parseFactor() {
        Expression factor = parseTerm();

        while (true) {
            TokenType next = nextToken();
            switch (next) {
            case MUL:
                factor = new BinaryExpression(BinOp.MUL, factor, parseTerm());
                break;
            case DIV:
                factor = new BinaryExpression(BinOp.DIV, factor, parseTerm());
                break;
            case MOD:
                factor = new BinaryExpression(BinOp.MOD, factor, parseTerm());
                break;
            default:    
                lexer.pushBack(); 
                return factor;
            }
        }
    }

    /**
     * term = IDENT argument_list 
     *      | IDENT
     *      | die_exp
     *      | ( expr )
     */
    private Expression parseTerm() {
        TokenType token = nextToken();
     
        if (token == TokenType.IDENTIFIER) {
            String name = (String) lexer.getCurrentValue();
            
            if (nextToken() == TokenType.LPAR) {
                lexer.pushBack();
                List<Expression> args = parseArgumentList();
                return new ApplyExpression(name, args);
            } else {
                lexer.pushBack();
                return parseVariableOrDie(name);
            }
            
        } else if (token == TokenType.LPAR) {
            Expression term = parseExpression();
            assertToken(TokenType.RPAR);
            return term;
        } else {
            lexer.pushBack();
            return new ConstantExpression(getNumber());
        }
    }

    private static Expression parseVariableOrDie(String token) {
        Matcher m = DIE_PATTERN.matcher(token);
        if (m.matches()) {
            int multiplier = parseInt(m.group(1), 1);
            int sides = Integer.parseInt(m.group(2));
            return new DieExpression(multiplier, sides);
        
        } else {
            return new VariableExpression(token);
        }
    }

    private static int parseInt(String value, int defaultValue) {
        if (value != null && !value.equals("")) {
            return Integer.parseInt(value);
        } else {
            return defaultValue;
        }
    }

    /**
     * argumentList = ()
     *              | (nonEmptyExplist)
     */
    private List<Expression> parseArgumentList() {
        assertToken(TokenType.LPAR);
        
        if (nextToken() == TokenType.RPAR) {
            return Collections.emptyList();
        } else {
            lexer.pushBack();
            List<Expression> exps = parseNonEmptyExpList();
            assertToken(TokenType.RPAR);
            return exps;
        }
    }

    /**
     * nonEmptyExpList = exp
     *                 | exp, explist
     */
    private List<Expression> parseNonEmptyExpList() {
        List<Expression> result = new ArrayList<Expression>();
        while (true) {
            result.add(parseExpression());
            if (nextToken() != TokenType.COMMA) {
                lexer.pushBack();
                return result;
            }
        }
    }
    
    private void assertToken(TokenType token) {
        if (nextToken() != token) {
            throw new ParseException(
                    "invalid expression <" + expression + ">, expected: " + token);
        }
    }

    private int getNumber() {
        int sign = 1;
        TokenType type = nextToken();
        if (type == TokenType.PLUS || type == TokenType.MINUS) {
            sign = (type == TokenType.PLUS) ? 1 : -1;
            type = nextToken();
        }
        
        if (type == TokenType.NUMBER) {
            return sign * (Integer) lexer.getCurrentValue();
        } else {
            throw new ParseException("expected number, got " + type);
        }
    }
    
    private TokenType nextToken() {
        TokenType type = lexer.next();
        if (type != null) {
            return type;
        } else {
            throw new RuntimeException("eof");
        }
    }
}
