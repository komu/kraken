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

import java.util.HashMap;
import java.util.Map;

import net.wanhack.utils.exp.Expression;

import junit.framework.TestCase;

public class ExpressionTest extends TestCase {

    public void testSimple() {
        assertExpression(0, "0");
        assertExpression(1, "1");
        assertExpression(5, "5");
        assertExpression(-5, "-5");
        assertExpression(5, "+5");
    }
    
    public void testAdditiveExpression() {
        assertExpression(4, "2 + 2");
        assertExpression(3, "5 - 2");
        assertExpression(6, "1 + 2 + 3");
        assertExpression(2, "5 - 2 - 1");
    }
    
    public void testMultiplicativeExpression() {
        assertExpression(6, "2 * 3");
        assertExpression(11, "1 + 2 * 3 + 4");
    }

    public void testParenthesizedExpression() {
        assertExpression(21, "(1 + 2) * (3 + 4)");
    }
    
    public void testFunctionCalls() {
        assertExpression(2, "max(1, 2)");
    }
    
    public void testVariables() {
        Map<String, Integer> env = new HashMap<String, Integer>();
        env.put("one", 1);
        env.put("two", 2);
        
        assertExpression(3, "one + two", env);
    }

    public void xtestDiceSyntax() {
        assertExpression(2, "d1 + d1");
        assertExpression(4, "3d1 + 1");
        
        int result = Expression.evaluate("d4");
        assertTrue(result >= 1 && result <= 4);
    }
    
    private static void assertExpression(int expected, String exp) {
        assertEquals(expected, Expression.evaluate(exp));
    }

    private static void assertExpression(int expected, String exp, Map<String, Integer> env) {
        assertEquals(expected, Expression.evaluate(exp, env));
    }
}
