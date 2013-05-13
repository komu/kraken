/*
 *  Copyright 2005-2006 The Wanhack Team
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
package net.wanhack.model.creature;

import java.awt.Color;

import net.wanhack.model.Game;
import net.wanhack.model.common.Attack;
import net.wanhack.model.item.weapon.NaturalWeapon;
import net.wanhack.utils.RandomUtils;

public class Oracle extends Creature {

    private static final String[] WISDOMS = {
        // real hints
        "Beauty is in the eye of beholder.",
        "They say that no ordinary shovel can dig into Exceptionally Hard Rock.",
        "Only the light of Graal can defeat the ultimate darkness.",
        "They say that Festivus is the day to start your adventure.",
        "They say that The Founders are there on Friday.",
        "The deaf are not afraid of the Ni.",
        "All names have a meaning.",
        "How small a rock needs to be in order to float on water?",
        "Beholders tend to feel uneasy around spoons.",
        "Chain smoking might be good for you.",
        "The Siamese bats are more dangerous.",
        
        // true but quite useless wisdoms
        "There is no fork.",
        "Enlarge your shield.",
        
        // random mumbling
        "They say a lot of things.",
        "Don't believe everything you are told.",
        "This sentence is a lie.",
        "This sentence is true, but can't be proved.",
        "What's the value of x in the following sequence? 1, 2, 720!, x, ...",
        "How many roads must a man walk down?",
        "Attributes are the first step on the road that leads to the Dark Side.",
    };
    
    private Attack curse = new NaturalWeapon("curse", "20", "0");
    
    public Oracle(String name) {
        super(name);
        
        setHitpoints(10000);
        setLetter('@');
        setColor(Color.WHITE);
        setFriendly(true);
        setTickRate(10000000); // big number, since the Oracle does nothing
    }
    
    @Override
    public void talk(Creature talker) {
        talker.say(this, RandomUtils.randomItem(WISDOMS));
    }
    
    @Override
    protected void onTick(Game game) {
        // Oracle does nothing
    }

    @Override
    public Attack getNaturalAttack() {
        return curse;
    }
}
