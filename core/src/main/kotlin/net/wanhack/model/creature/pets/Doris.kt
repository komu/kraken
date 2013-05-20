/*
 * Copyright 2013 The Wanhack Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.wanhack.model.creature.pets

import net.wanhack.model.Game
import net.wanhack.model.creature.Creature
import net.wanhack.utils.Probability
import net.wanhack.utils.RandomUtils

class Doris(name: String): Pet(name) {

    {
        weight = 10
        letter = 'f'
    }

    public override fun talk(target: Creature) {
        val verb = RandomUtils.randomItem("meows", "purrs")
        target.message("%s %s.", name, verb)
    }

    protected override fun onTick(game: Game) {
        val player = game.player
        if (isAdjacentToCreature(player) && Probability.check(1))
            game.attack(this, player)
        else
            super.onTick(game)
    }
}
