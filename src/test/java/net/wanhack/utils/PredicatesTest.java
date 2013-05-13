/*
 *  Copyright 2006 The Wanhack Team
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
package net.wanhack.utils;

import junit.framework.TestCase;

public class PredicatesTest extends TestCase {

    public void testMatchNever() {
        assertFalse(Predicates.matchNever().evalute(""));
        assertFalse(Predicates.matchNever().evalute(null));
    }

    public void testMatchAlways() {
        assertTrue(Predicates.matchAlways().evalute(""));
        assertTrue(Predicates.matchAlways().evalute(null));
    }

    public void testNot() {
        assertFalse("not true", Predicates.not(Predicates.matchAlways()).evalute(""));
        assertTrue("not false", Predicates.not(Predicates.matchNever()).evalute(""));
    }
    
    public void testOrEmpty() {
        assertFalse(Predicates.or().evalute(""));
    }
    
    public void testAndEmpty() {
        assertTrue(Predicates.and().evalute(""));
    }
}
