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
package net.wanhack.model.events.region;

import java.util.Collection;
import java.util.Random;

import net.wanhack.model.Game;
import net.wanhack.model.creature.Creature;
import net.wanhack.model.creature.Player;
import net.wanhack.model.events.PersistentEvent;
import net.wanhack.model.region.Cell;
import net.wanhack.model.region.CellSet;
import net.wanhack.model.region.Region;
import net.wanhack.service.creature.CreatureService;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Event for creating new monsters periodically to current region.
 * <p>
 * Tries to create a monster to a cell invisible to player, but if
 * there are no empty invisible cells, creates the monster to a visible
 * cell. If there are no empty cells at all, no monster is created.
 */
public final class CreateMonstersEvent extends PersistentEvent {

    private final Region region;
    private final Random random = new Random();
    private static final Log log = LogFactory.getLog(CreateMonstersEvent.class);

    public CreateMonstersEvent(Region region) {
        super(500 * 100);
        
        this.region = region;
    }
    
    @Override
    protected void fire(Game game) {
        CreatureService creatureService = CreatureService.getInstance();
        
        // TODO: the logic is mostly duplicated from World.addRandomCreatures
        
        Player player = game.getPlayer();
        
        Collection<Creature> creatures =
            creatureService.randomSwarm(region.getLevel(), player.getLevel());

        log.debug("Created new random creatures: " + creatures);
        
        for (Creature creature : creatures) {
            // TODO: add the swarm of creatures close to each others
            Cell target = getTargetCell(player, creature);
            if (target != null) {
                game.addCreature(creature, target);
            } else {
                log.warn("No empty cells available, creature not added.");
            }
        }
    }
    
    private Cell getTargetCell(Player player, Creature creature) {
        Cell cell = selectRandomTargetCell(player.getInvisibleCells(), creature);
        if (cell != null) {
            return cell;
        } else {
            return selectRandomTargetCell(region.getCells(), creature);
        }
    }
    
    private Cell selectRandomTargetCell(CellSet candidates, Creature creature) {
        while (!candidates.isEmpty()) {
            Cell cell = candidates.get(random.nextInt(candidates.size()));
            if (cell.getCreature() == null
                    && cell.canMoveInto(creature.isCorporeal())) {
                return cell;
            }
        }
        
        return null;
    }
}
