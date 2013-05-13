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
package net.wanhack.model.common;

import static net.wanhack.model.common.Direction.*;
import net.wanhack.model.common.Direction;

import junit.framework.TestCase;

public class DirectionTest extends TestCase {

    public void testOpposite() {
        assertOpposite(EAST, WEST);
        assertOpposite(SOUTH, NORTH);
        assertOpposite(NE, SW);
        
        assertNotOpposite(NE, NORTH);
        assertNotOpposite(NORTH, NORTH);
        assertNotOpposite(NORTH, EAST);
    }
    
    private void assertOpposite(Direction d1, Direction d2) {
        assertTrue(d1.isOpposite(d2));
    }

    private void assertNotOpposite(Direction d1, Direction d2) {
        assertFalse(d1.isOpposite(d2));
    }
}
