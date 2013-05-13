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
package net.wanhack.ui.game;

import net.wanhack.model.item.Item;

class ItemInfo implements Comparable<ItemInfo> {

    private final Item item;
    private final boolean inUse;
    
    public ItemInfo(Item item, boolean inUse) {
        this.item = item;
        this.inUse = inUse;
    }

    public Item getItem() {
        return item;
    }
    
    public boolean isInUse() {
        return inUse;
    }
    
    public String getTitle() {
        return item.getTitle();
    }

    public String getDescription() {
        return item.getDescription(); 
    }

    public char getLetter() {
        return item.getLetter();
    }

    public int compareTo(ItemInfo o) {
        if (inUse != o.inUse) {
            return inUse ? -1 : 1;
        }
        
        return getTitle().compareTo(o.getTitle());
    }
}
