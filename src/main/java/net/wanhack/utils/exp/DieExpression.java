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

import java.util.Map;

import net.wanhack.utils.RandomUtils;


public class DieExpression extends Expression {

    private final int multiplier;
    private final int sides;
    
    public DieExpression(int multiplier, int sides) {
        this.multiplier = multiplier;
        this.sides = sides;
    }
    
    @Override
    public int evaluate(Map<String, Integer> env) {
        int result = 0;
        
        for (int i = 0; i < multiplier; i++) {
            result += RandomUtils.rollDie(sides);
        }
        
        return result;
    }

    @Override
    public String toString() {
        return multiplier + "d" + sides;
    }
}
