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
package net.wanhack.model.region;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.wanhack.model.Game;
import net.wanhack.model.creature.Creature;
import net.wanhack.model.creature.Player;
import net.wanhack.model.item.Item;
import net.wanhack.service.ServiceProvider;
import net.wanhack.service.config.ObjectDefinition;
import net.wanhack.service.config.ObjectFactory;
import net.wanhack.service.creature.CreatureService;
import net.wanhack.service.region.RegionLoader;
import net.wanhack.service.region.RegionLoadingException;
import net.wanhack.service.region.generators.MazeRegionGenerator;
import net.wanhack.service.region.generators.RegionGenerator;
import net.wanhack.service.region.generators.RoomFirstRegionGenerator;
import net.wanhack.utils.Probability;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public final class World implements Serializable {

    private final Map<String,Region> loadedRegions = new HashMap<String,Region>();
    private final List<RegionInfo> regions = new ArrayList<RegionInfo>();
    private Game game;
    private final Random random = new Random();
    private Probability mazeProbability = new Probability(5);
    private static final long serialVersionUID = 0;
    private static final Log log = LogFactory.getLog(World.class);
    
    public World() {
        addNonRandomRegion(0, "start");
        
        addRandomRegion(1,  "level1");
        addRandomRegion(2,  "level2");
        addRandomRegion(3,  "level3");
        addRandomRegion(4,  "level4");
        addRandomRegion(5,  "level5");
        addRandomRegion(6,  "level6");
        addRandomRegion(7,  "level7");
        addRandomRegion(8,  "level8");
        addRandomRegion(9,  "level9");
        addRandomRegion(10, "level10");
        addRandomRegion(11, "level11");
        addRandomRegion(12, "level12");
        addRandomRegion(13, "level13");
        addRandomRegion(14, "level14");
        addRandomRegion(15, "level15");
        addRandomRegion(16, "level16");
        addRandomRegion(17, "level17");
        addRandomRegion(18, "level18");
        addRandomRegion(19, "level19");
        addRandomRegion(20, "level20");
        addRandomRegion(21, "level21");
        addRandomRegion(22, "level22");
        addRandomRegion(23, "level23");
        addRandomRegion(24, "level24");
        addRandomRegion(25, "level25");
        addRandomRegion(26, "level26");
        addRandomRegion(27, "level27");
        addRandomRegion(28, "level28");
        addRandomRegion(29, "level29");
        addRandomRegion(30, "level30");

        addNonRandomRegion(31, "end");
        
        RegionInfo previous = null;
        for (RegionInfo region : regions) {
            if (previous != null) {
                previous.setNext(region);
                region.setPrevious(previous);
            }
            previous = region;
        }
    }

    private void addRandomRegion(int level, String id) {
        regions.add(new RegionInfo(id, level, true));
    }

    private void addNonRandomRegion(int level, String id) {
        regions.add(new RegionInfo(id, level, false));
    }
    
    public Game getGame() {
        return game;
    }
    
    public void setGame(Game game) {
        this.game = game;
    }
    
    public Region getRegion(Player player, String name) throws RegionLoadingException {
        Region region = loadedRegions.get(name);
        if (region == null) {
            region = initRegion(player, name);
            loadedRegions.put(name, region);
        }
        return region;
    }

    private Region initRegion(Player player, String name) {
        RegionInfo info = getRegionInfo(name);
        
        Region region = loadRegion(info);
        
        addRandomCreatures(player, region);
        addRandomItems(region);
        return region;
    }

    private Region loadRegion(RegionInfo info) {
        if (info.isRandom()) {
            String up = info.getPrevious() != null ? info.getPrevious().getId() : null;
            String down = info.getNext() != null ? info.getNext().getId() : null;
    
            return getRegionGenerator().generate(
                    this, info.getId(), info.getLevel(), 
                    up, down);
        } else {
            return getRegionLoader().loadRegion(this, info);
        }
    }
    
    private RegionInfo getRegionInfo(String id) {
        for (RegionInfo region : regions) {
            if (id.equals(region.getId())) {
                return region;
            }
        }
        
        throw new IllegalArgumentException("unknown region <" + id + ">");
    }
    
    private RegionGenerator getRegionGenerator() {
        if (mazeProbability.check()) {
            return new MazeRegionGenerator();
        } else {
            return new RoomFirstRegionGenerator();
        }
    }

    private void addRandomCreatures(Player player, Region region) {
        if (region.getLevel() == 0) return;
        
        int monsterCount = 1 + random.nextInt(2 * region.getLevel());
        
        CellSet empty = region.getCellsForItemsAndCreatures();
        
        CreatureService creatureService = CreatureService.getInstance();

        for (int i = 0; i < monsterCount; i++) {
            Collection<Creature> creatures = 
                creatureService.randomSwarm(region.getLevel(), player.getLevel());

            if (empty.isEmpty()) {
                return;
            }
            
            Cell cell = empty.get(random.nextInt(empty.size()));
            Iterator<Cell> cells = 
                cell.getMatchingCellsNearestFirst(
                        CellPredicates.CAN_PUT_CREATURE_ON_CELL).iterator();
            
            for (Creature creature : creatures) {
                if (cells.hasNext()) {
                    Cell target = cells.next();
                    
                    empty.remove(target);
                    creature.setCell(target);
                }
            }
        }
    }

    private void addRandomItems(Region region) {
        int minItemLevel = 0;
        int maxItemLevel = region.getLevel();
        int itemCount = random.nextInt(6);
        
        log.debug(String.format("Randomizing %d random items between levels %d and %d.", 
                                itemCount, minItemLevel, maxItemLevel));
        
        CellSet empty = region.getCellsForItemsAndCreatures();
        
        for (int i = 0; i < itemCount; i++) {
            Item item = randomItem(minItemLevel, maxItemLevel);
            
            if (empty.isEmpty()) {
                return;
            }
            
            log.debug(String.format("item %d: %s", i, item));
            
            Cell cell = empty.get(random.nextInt(empty.size()));
            cell.addItem(item);
        }
    }
    
    private Item randomItem(int minLevel, int maxLevel) {
        List<ObjectDefinition> defs = 
            getObjectFactory().getAvailableDefinitionsForClass(Item.class);
        ObjectDefinition def = random(defs, minLevel, maxLevel);
        
        return getObjectFactory().create(Item.class, def.getName());
    }
    
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
    
    private RegionLoader getRegionLoader() {
        return ServiceProvider.getRegionLoader();
    }

    private ObjectFactory getObjectFactory() {
        return ServiceProvider.getObjectFactory();
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
