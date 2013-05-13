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
import java.util.Arrays;
import java.util.List;

import net.wanhack.utils.exp.ExpressionLexer;
import net.wanhack.utils.exp.TokenType;

import junit.framework.TestCase;

public class ExpressionLexerTest extends TestCase {
    
    public void testLexing() {
        assertTokens("");
        assertTokens("foo", "foo");
        assertTokens("foo bar", "foo", "bar");
        assertTokens("foo(bar)", "foo", "(", "bar", ")");
        assertTokens("(1 + 20) * 3", 
                     "(", "1", "+", "20", ")", "*", "3");
        assertTokens("(1+2) * 3 / randint(4, 5)",
                     "(", "1", "+", "2", ")", "*", "3", "/", "randint",
                     "(", "4", ",", "5", ")");
    }
    
    public void testPushback() {
        ExpressionLexer lexer = new ExpressionLexer("foo bar");
        
        assertEquals(TokenType.IDENTIFIER, lexer.next());
        assertEquals("foo", lexer.getCurrentValue());
        
        lexer.pushBack();
        
        assertEquals(TokenType.IDENTIFIER, lexer.next());
        assertEquals("foo", lexer.getCurrentValue());
        
        assertEquals(TokenType.IDENTIFIER, lexer.next());
        assertEquals("bar", lexer.getCurrentValue());
        
        lexer.pushBack();
        
        assertEquals(TokenType.IDENTIFIER, lexer.next());
        assertEquals("bar", lexer.getCurrentValue());

        assertEquals(TokenType.EOF, lexer.next());
    }

    private static void assertTokens(String exp, String... expected) {
        List<String> tokens = lex(exp);
        
        assertEquals(Arrays.asList(expected), tokens);
    }

    private static List<String> lex(String exp) {
        ArrayList<String> tokens = new ArrayList<String>();

        ExpressionLexer lexer = new ExpressionLexer(exp);

        TokenType token;
        while ((token = lexer.next()) != TokenType.EOF) {
            if (token == TokenType.IDENTIFIER || token == TokenType.NUMBER) {
                tokens.add(lexer.getCurrentValue().toString());
            } else {
                tokens.add(token.toString());
            }
        }
        
        return tokens;
    }
}
