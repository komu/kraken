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
package net.wanhack.service.creature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import net.wanhack.model.creature.Creature;
import net.wanhack.service.ServiceProvider;
import net.wanhack.service.config.ObjectDefinition;
import net.wanhack.service.config.ObjectFactory;


public class CreatureService {
    
    private final Random random = new Random();
    private static final CreatureService INSTANCE = new CreatureService();
    
    private CreatureService() {
    }

    public static CreatureService getInstance() {
        return INSTANCE;
    }

    public Collection<Creature> randomSwarm(int regionLevel, int playerLevel) {
        int minMonsterLevel = regionLevel / 6;
        int maxMonsterLevel = (regionLevel + playerLevel) / 2;

        return randomSwarmBetween(minMonsterLevel, maxMonsterLevel);
    }
    
    private Collection<Creature> randomSwarmBetween(int minLevel, int maxLevel) {
        ObjectFactory objectFactory = getObjectFactory();
        
        List<ObjectDefinition> defs = 
            objectFactory.getAvailableDefinitionsForClass(Creature.class);
        ObjectDefinition def = random(defs, minLevel, maxLevel);
        
        int swarmSize = def.swarmSize();

        List<Creature> swarm = new ArrayList<Creature>(swarmSize);
        for (int i = 0; i < swarmSize; i++) {
            swarm.add(objectFactory.create(Creature.class, def.getName()));
        }
        return swarm;
    }

    private ObjectFactory getObjectFactory() {
        return ServiceProvider.getObjectFactory();
    }

    // TODO: copy-paste from World
    private ObjectDefinition random(List<ObjectDefinition> defs, int minLevel, int maxLevel) {
        int probabilitySum = 0;
        
        List<DefProbability> probs = new ArrayList<DefProbability>(defs.size());
        
        for (ObjectDefinition od : defs) {
            Integer level = od.getLevel();
            if (level == null || (level >= minLevel && level <= maxLevel)) {
                int probability = od.getProbability();
                probs.add(new DefProbability(od, probability, level));
                probabilitySum += probability;
            }
        }
        
        int item = random.nextInt(probabilitySum);
        
        for (DefProbability dp : probs) {
            if (dp.level == null || (dp.level >= minLevel && dp.level <= maxLevel)) {
                if (item < dp.probability) {
                    return dp.def;
                }
                item -= dp.probability;
            }
        }
        
        throw new RuntimeException("could not randomize definition");
    }

    private static class DefProbability {
        private final ObjectDefinition def;
        private final int probability;
        private final Integer level;
        
        public DefProbability(ObjectDefinition def, int probability, Integer level) {
            this.def = def;
            this.probability = probability;
            this.level = level;
        }
    }
}
