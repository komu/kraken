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

import java.io.Serializable;

public final class Probability implements Serializable {

    private final int percentage;
    private static final long serialVersionUID = 0;
    
    public Probability(int percentage) {
        assert percentage >= 0 && percentage <= 100;
        
        this.percentage = percentage;
    }
    
    public boolean check() {
        return check(percentage);
    }
    
    public static boolean check(int percentage) {
        assert percentage >= 0 && percentage <= 100;
        
        return percentage > RandomUtils.randomInt(100);
    }
}
