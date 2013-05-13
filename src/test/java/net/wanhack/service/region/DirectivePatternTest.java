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
package net.wanhack.service.region;

import java.util.Arrays;

import net.wanhack.service.region.DirectivePattern;


import junit.framework.TestCase;

public class DirectivePatternTest extends TestCase {
    
    public void testNoMatch() {
        assertNoMatch("foo [int] [str]", "foo bar baz");
    }
    
    public void testParseSimpleTokens() {
        assertMatch("foo [str] [str]", 
                    "foo \"bar\" \"baz\"", 
                    "bar", "baz");
    }

    private static void assertNoMatch(String pattern, String line) {
        DirectivePattern directivePattern = new DirectivePattern(pattern);
        assertNull(directivePattern.getTokens(line));
    }
    
    private static void assertMatch(String pattern, String line, Object... expected) {
        DirectivePattern directivePattern = new DirectivePattern(pattern);
        Object[] tokens = directivePattern.getTokens(line);
        
        assertEquals(Arrays.asList(expected), Arrays.asList(tokens));
    }
}
