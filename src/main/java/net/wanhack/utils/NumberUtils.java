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

public class NumberUtils {

    /**
     * Returns the number of cells that are in a row from distance
     * at any cell. E.g. at distance 1, there are 8 cells (the
     * cells surrounding); at distance 2, there are 16 cells. 
     */
    public static int cellsAtDistance(int distance) {
        if (distance == 0) return 1;
        
        int width = 2 * distance + 1; 

        return square(width) - square(width - 1);
    }
    
    public static int square(int n) {
        return n * n;
    }
    
    public static int signum(int n) {
        return (n < 0) ? -1 
             : (n > 0) ? 1
             : 0;
    }
    
    /**
     * Calculates modulo for range [00, mod) so that negative values
     * "flip" on the other side.
     */
    public static int mod(int x, int mod) {
        if (x >= 0) {
            return x % mod;
        } else {
            int xx = Math.abs(x) % mod;
            return (xx != 0) ? (mod - xx) : 0;
        }
    }
}
