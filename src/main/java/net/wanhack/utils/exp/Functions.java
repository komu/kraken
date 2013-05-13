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

import java.util.Random;

class Functions {
    
    private static final Random random = new Random();

    public static int abs(int x) {
        return Math.abs(x);
    }
    
    public static int max(int x, int y) {
        return Math.max(x, y);
    }

    public static int min(int x, int y) {
        return Math.min(x, y);
    }
    
    public static int randint(int n) {
        return random.nextInt(n);
    }

    public static int randint(int min, int max) {
        return min + random.nextInt(max - min + 1);
    }
}
