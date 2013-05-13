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
package net.wanhack.model.events.global;

import net.wanhack.model.Game;
import net.wanhack.model.events.PersistentEvent;

/**
 * Event that makes player hungrier as time passes.
 */
public final class HungerEvent extends PersistentEvent {
    
    public HungerEvent() {
        super(100);
    }
    
    @Override
    protected void fire(Game game) {
        game.getPlayer().increaseHungriness(game);
    }
}
