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
package net.wanhack.utils;

import net.wanhack.utils.NumberUtils;
import junit.framework.TestCase;

public class NumberUtilsTest extends TestCase {

    public void testModForPositiveValues() {
        assertMod(0, 0, 10);
        assertMod(1, 1, 10);
        assertMod(2, 2, 10);
        assertMod(9, 9, 10);
        assertMod(0, 10, 10);
        assertMod(1, 11, 10);
        assertMod(9, 19, 10);
        assertMod(2, 12, 10);
        assertMod(0, 20, 10);
        assertMod(1, 21, 10);
        assertMod(2, 22, 10);
        assertMod(9, 29, 10);
    }

    public void testModForNegativeValues() {
        assertMod(9, -1, 10);
        assertMod(8, -2, 10);
        assertMod(1, -9, 10);
        assertMod(0, -10, 10);
        assertMod(9, -11, 10);
        assertMod(8, -12, 10);
        assertMod(1, -19, 10);
        assertMod(0, -20, 10);
    }
    
    private static void assertMod(int expected, int x, int mod) {
        assertEquals("mod", expected, NumberUtils.mod(x, mod));
    }
}
