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

import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomUtils {

    private static final Random random = new Random();
    
    /**
     * Returns random enum constant of given type.
     */
    public static <T extends Enum<T>> T randomEnum(Class<T> type) {
        T[] types = type.getEnumConstants();
        return randomItem(types);
    }
    
    /**
     * Returns random element from given array.
     */
    public static <T> T randomItem(T... items) {
        return items[random.nextInt(items.length)];
    }
    
    /**
     * Returns random item from given list.
     */
    public static <T> T randomItem(List<? extends T> items) {
        return items.get(random.nextInt(items.size()));
    }
    
    /**
     * Return random number between 1..<code>sides</code>, inclusive.
     */
    public static int rollDie(int sides) {
        return 1 + random.nextInt(sides);
    }

    /**
     * Rolls die for several times.
     */
    public static int rollDie(int times, int sides) {
        int total = 0;
        for (int i = 0; i < times; i++) {
            total += rollDie(sides);
        }
        return total;
    }
    
    /**
     * Shuffles given list.
     */
    public static void shuffle(List<?> list) {
        Collections.shuffle(list, random);
    }

    public static int randomInt(int n) {
        return random.nextInt(n);
    }
}
