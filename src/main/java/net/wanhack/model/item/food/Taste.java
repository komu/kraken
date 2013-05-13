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
package net.wanhack.model.item.food;

public enum Taste {
    APPLE("apple", true), 
    CHICKEN("chicken", true), 
    STRAWBERRY("strawberries", true), 
    BLUEBERRY("blueberries", true), 
    ELDERBERRY("elderberries", true),
    VANILLA("vanilla", true), 
    CHEESE("cheese", true), 
    STRANGE("strange", false), 
    DULL("dull", false);
    
    private final String s;
    private final boolean like;
    
    private Taste(String s, boolean like) {
        this.s = s;
        this.like = like;
    }
    
    @Override
    public String toString() {
        return like ? "like " + s : s;
    }
}

