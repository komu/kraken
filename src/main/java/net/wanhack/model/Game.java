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
package net.wanhack.model;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import net.wanhack.model.GameConfiguration.PetType;
import net.wanhack.model.common.Attack;
import net.wanhack.model.common.Console;
import net.wanhack.model.common.Direction;
import net.wanhack.model.common.Updateable;
import net.wanhack.model.creature.Creature;
import net.wanhack.model.creature.Player;
import net.wanhack.model.creature.pets.Doris;
import net.wanhack.model.creature.pets.Lassie;
import net.wanhack.model.creature.pets.Pet;
import net.wanhack.model.events.GameEvent;
import net.wanhack.model.events.global.HungerEvent;
import net.wanhack.model.events.global.RegainHitpointsEvent;
import net.wanhack.model.events.region.CreateMonstersEvent;
import net.wanhack.model.item.Item;
import net.wanhack.model.item.armor.Armor;
import net.wanhack.model.item.food.Food;
import net.wanhack.model.item.weapon.Weapon;
import net.wanhack.model.region.Cell;
import net.wanhack.model.region.CellType;
import net.wanhack.model.region.JumpTarget;
import net.wanhack.model.region.Region;
import net.wanhack.model.region.World;
import net.wanhack.service.ServiceProvider;
import net.wanhack.service.config.ObjectFactory;
import net.wanhack.service.score.HighScoreService;
import net.wanhack.utils.RandomUtils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Represents the game.
 */
public final class Game implements Serializable, IGame {
    
    private final Clock globalClock = new Clock();
    private final Clock regionClock = new Clock();
    private final Player player;
    private final World world = new World();
    private final boolean wizardMode;
    private int maxDungeonLevel = 0;
    private boolean over = false;
    private transient Updateable listener;
    private transient DefaultGameRef selfRef;
    private final GameConfiguration config;
    
    private static final Log log = LogFactory.getLog(Game.class);
    
    public Game(GameConfiguration config, boolean wizardMode) {
        this.wizardMode = wizardMode;
        this.player = new Player(config.getName());
        this.player.setSex(config.getSex());
        this.world.setGame(this);
        this.config = config;
    }
    
    public void setListener(Updateable listener) {
        this.listener = listener;
    }
    
    public synchronized DefaultGameRef getSelfRef() {
        if (selfRef == null) {
            selfRef = new DefaultGameRef(this);
        }
        return selfRef;
    }

    public void revealCurrentRegion() {
        getCurrentRegion().reveal();
    }

    public void start() {
        assertWriteLock();
        
        ObjectFactory objectFactory = ServiceProvider.getObjectFactory();
        
        player.setWieldedWeapon(objectFactory.create(Weapon.class, "a dagger"));
        player.addItemToInventory(objectFactory.create(Item.class, "food ration"));
        player.addItemToInventory(objectFactory.create(Item.class, "a cyanide capsule"));
        
        enterRegion("start", "from up");
        
        if (config.getPet() == PetType.DORIS) {
            putPetNextToPlayer(new Doris("Doris"));
        } else if (config.getPet() == PetType.LASSIE) {
            putPetNextToPlayer(new Lassie("Lassie"));
        }
        
        getCurrentRegion().updateLighting();
        player.act(this);
        getCurrentRegion().updateSeenCells(player.getVisibleCells());
       
        player.message("Hello %s, welcome to Wanhack!", player.getName());
        if (todayIsFestivus()) {
            player.message("Happy Festivus!");
            player.setStrength(player.getStrength() + 10);
            player.setLuck(2);
            player.addItemToInventory(objectFactory.create(Item.class, "Aluminium Pole"));
        }
        
        if (todayIsFriday()) {
            player.message("It is Friday, good luck!");
            player.setLuck(1);
        }
        
        addGlobalEvent(new HungerEvent());
        addGlobalEvent(new RegainHitpointsEvent());
        
        listener.update();
    }
    
    /**
     * Adds a pet to the game.
     */
    private boolean putPetNextToPlayer(Creature pet) {
        Cell target = null;
        for (Cell cell : player.getCell().getAdjacentCells()) {
            if (cell.getType().isFloor() && cell.getCreature() == null) {
                target = cell;
            }
        }
        
        if (target != null) {
            pet.setCell(target);
            regionClock.schedule(pet.getTickRate(), pet);
            return true;
        } else {
            return false;
        }
    }
    
    public void addGlobalEvent(GameEvent event) {
        globalClock.schedule(event.getRate(), event);
    }

    public void addRegionEvent(GameEvent event) {
        regionClock.schedule(event.getRate(), event);
    }
    
    public Cell getCellInFocus() {
        if (getSelectedCell() != null) {
            return getSelectedCell();
        } else if (player != null) {
            return player.getCell();
        } else {
            return null;
        }
    }
    
    public Cell getSelectedCell() {
        return null;
    }
    
    public int getDungeonLevel() {
        return getCurrentRegion().getLevel();
    }
    
    public int getMaxDungeonLevel() {
        return maxDungeonLevel;
    }
    
    public void save(ObjectOutputStream oos) throws IOException {
        oos.writeObject(this);
    }
    
    public void enterRegion(String name, String location) {
        Region region = world.getRegion(player, name);
        Cell oldCell = player.getCell();
        Region oldRegion = (oldCell != null) ? oldCell.getRegion() : null;
        region.setPlayerLocation(player, location);
        
        if (region != oldRegion) {
            regionClock.clear();

            maxDungeonLevel = Math.max(region.getLevel(), maxDungeonLevel);
            
            // If pet is adjacent to player, move it also
            if (oldCell != null) {
                for (Cell cell : oldCell.getAdjacentCells()) {
                    Creature creature = cell.getCreature();
                    if (creature instanceof Pet) {
                        putPetNextToPlayer(creature);
                    }
                }
            }
            
            // Add events
            addRegionEvent(new CreateMonstersEvent(region));
            
            // Add creatures
            for (Creature creature : region.getCreatures()) {
                regionClock.schedule(creature.getTickRate(), creature);
            }
        }
    }

    public void addCreature(Creature creature, Cell target) {
        creature.setCell(target);
        if (target.getRegion() == getCurrentRegion()) {
            regionClock.schedule(creature.getTickRate(), creature);
        }
    }
    
    public void talk() {
        assertWriteLock();
        
        if (isOver()) return;
        
        Set<Creature> adjacent = player.getAdjacentCreatures();
        if (adjacent.size() == 1) {
            Creature creature = adjacent.iterator().next();
            creature.talk(player);
            tick();
        } else if (adjacent.isEmpty()) {
            player.message("There's no-one to talk to.");
        } else {
            Direction dir = getConsole().selectDirection();
            if (dir != null) {
                Cell cell = player.getCell().getCellTowards(dir);
                if (cell.getCreature() != null) {
                    cell.getCreature().talk(player);
                    tick();
                } else {
                    player.message("There's nobody in selected direction.");
                }
            }
        }
    }

    public void openDoor() {
        assertWriteLock();
        
        if (isOver()) return;
        
        List<Cell> closedDoors = 
            player.getCell().getAdjacentCellsOfType(CellType.CLOSED_DOOR);

        if (closedDoors.isEmpty()) {
            player.message("There are no closed doors around you.");
        
        } else if (closedDoors.size() == 1) {
            closedDoors.get(0).openDoor(player);
            tick();
            
        } else {
            Direction dir = getConsole().selectDirection();
            if (dir != null) {
                Cell cell = player.getCell().getCellTowards(dir);
                if (cell.getType() == CellType.CLOSED_DOOR) {
                    cell.openDoor(player);
                    tick(); 
                } else {
                    player.message("No closed door in selected direction.");
                }
            }
        }
    }

    public void closeDoor() {
        assertWriteLock();
        
        if (isOver()) return;

        List<Cell> openDoors = 
            player.getCell().getAdjacentCellsOfType(CellType.OPEN_DOOR);

        if (openDoors.isEmpty()) {
            player.message("There are no open doors around you.");
            
        } else if (openDoors.size() == 1) {
            closeDoor(openDoors.get(0));
            
        } else {
            Direction dir = getConsole().selectDirection();
            if (dir != null) {
                Cell cell = player.getCell().getCellTowards(dir);
                if (cell.getType() == CellType.OPEN_DOOR) {
                    closeDoor(cell);
                } else {
                    player.message("No open door in selected direction.");
                }
            }
        }
    }
    
    private void closeDoor(Cell door) {
        assertWriteLock();
        
        if (door.closeDoor(player)) {
            tick();
        }
    }

    public void pickup() {
        assertWriteLock();
        
        if (isOver()) return;

        Cell cell = player.getCell();
        
        Set<Item> items = cell.getItems();
        if (items.isEmpty()) {
            player.message("There's nothing to pick up.");
            
        } else if (items.size() == 1) {
            Item item = items.iterator().next();
            player.addItemToInventory(item);
            cell.removeItem(item);
            player.message("Picked up %s.", item.getTitle());
            tick();
        } else {
            for (Item item : getConsole().selectItems("Select items to pick up", items)) {
                player.addItemToInventory(item);
                cell.removeItem(item);
                player.message("Picked up %s.", item.getTitle());
            }
            tick();
        }
    }

    public void wield() {
        assertWriteLock();
        
        if (isOver()) return;
        
        Weapon oldWeapon = player.getWieldedWeapon();
        
        Weapon weapon = getConsole().selectItem(Weapon.class, 
                "Select weapon to wield", player.getInventoryItems(Weapon.class));
        if (weapon != null && weapon != oldWeapon) {
            player.setWieldedWeapon(weapon);
            player.removeItemFromInventory(weapon);
            
            if (oldWeapon != null) {
                player.message("You were wielding %s.", oldWeapon.getTitle());
                player.addItemToInventory(oldWeapon);
            }

            player.message("You wield %s.", weapon.getTitle());
            tick();
        }
    }

    public void wear() {
        assertWriteLock();
        
        if (isOver()) return;

        Armor armor = getConsole().selectItem(Armor.class, 
                "Select armor to wear", player.getInventoryItems(Armor.class));
        if (armor != null) {
            Armor oldArmor = player.replaceArmor(armor);

            player.removeItemFromInventory(armor);
            
            if (oldArmor != null && armor != oldArmor) {
                player.message("You were wearing %s.", oldArmor.getTitle());
                player.addItemToInventory(oldArmor);
            }

            player.message("You are now wearing %s.", armor.getTitle());
            tick();
        }
    }
    
    public void drop() {
        assertWriteLock();
        
        if (isOver()) return;
        
        Collection<Item> items = player.getInventoryItems(); 
        for (Item item : getConsole().selectItems("Select items to drop", items)) {
            drop(item);
        }
    }
    
    public void drop(Item item) {
        assertWriteLock();
        
        if (isOver()) return;

        if (item != null && player.getInventoryItems().contains(item)) {
            player.removeItemFromInventory(item);
            player.getCell().addItem(item);
            message("Dropped %s.", item.getTitle());
            tick();
        }
    }

    public void eat() {
        assertWriteLock();
        
        if (isOver()) return;
        
        Food food = getConsole().selectItem(
                Food.class, "Select food to eat", player.getInventoryItems(Food.class));
        if (food != null) {
            player.removeItemFromInventory(food);
            food.onEatenBy(player);
            tick();
        }
    }
    
    public void search() {
        assertWriteLock();
        
        if (isOver()) return;
        
        for (Cell cell : player.getCell().getAdjacentCells()) {
            if (cell.search(player)) {
                break;
            }
        }
        tick();
    }

    /**
     * The action for throwing items. For certain reasons, this method could
     * not be named throw().
     */
    public void fling() {
        assertWriteLock();
        
        if (isOver()) return;

        Item projectile = getConsole().selectItem(Item.class, 
                "Select item to throw", player.getInventoryItems(Item.class));
        
        if (projectile != null) {
            Direction dir = getConsole().selectDirection();

            if (dir != null) {
                player.removeItemFromInventory(projectile);
                
                Cell currentCell = player.getCell();
                Cell nextCell = currentCell.getCellTowards(dir);

                int range = player.getThrowRange(projectile.getWeight());
                
                for (int d = 0; d < range && nextCell.isPassable(); d++) {
                    currentCell = nextCell;
                    nextCell = currentCell.getCellTowards(dir);

                    Creature creature = currentCell.getCreature();
                    if (creature != null) {
                        if (throwAttack(player, projectile, creature, d)) {
                            break;
                        }
                    }
                }
                
                currentCell.addItem(projectile);
                
                tick();
            }
        }
    }
    
    private boolean throwAttack(Creature attacker,
                                Item projectile, 
                                Creature target, 
                                int distance) {
        target.onAttackedBy(attacker);
        
        int rollToHit = findRollToHit(attacker, projectile, target);
        if (RandomUtils.rollDie(20) <= rollToHit) {
            message("%s %s %s at %s.", 
                    attacker.You(),
                    attacker.verb("throw"),
                    projectile.getTitle(),
                    target.you());
            
            assignDamage(attacker, projectile, target);
            
            attacker.onSuccessfulHit(target, projectile);
            
            if (!target.isAlive()) {
                attacker.onKilledCreature(target);
            }

            return true;
        } else {
            message("%s flies past %s.", 
                    projectile.getTitle(), 
                    target.getName());
            
            return false;
        }
    }

    public boolean isOver() {
        return over;
    }

    public Clock getGlobalClock() {
        return globalClock;
    }
    
    public Clock getRegionClock() {
        return regionClock;
    }
    
    public Region getCurrentRegion() {
        return player.getRegion();
    }
    
    public Player getPlayer() {
        return player;
    }

    public int getScore() {
        return player.getExperience();
    }

    public void movePlayer(Direction direction) {
        assertWriteLock();
        
        if (isOver()) return;

        Cell cell = player.getCell().getCellTowards(direction);
        
        Creature creatureInCell = cell.getCreature();
        if (creatureInCell != null) {
            if (attack(player, creatureInCell)) {
                tick();
            }
            
        } else if (cell.canMoveInto(player.isCorporeal())) {
            cell.enter(player);
            tick();
        } else {
            if (log.isTraceEnabled()) {
                log.trace("Can't move towards: " + direction);
            }
        }
    }
    
    public void runTowards(Direction direction) {
        assertWriteLock();
        
        if (isOver()) return;
        
        Cell target = player.getCell().getCellTowards(direction);
        if (isInCorridor(target)) {
            runInCorridor(direction);
        } else {
            runInRoom(direction);
        }
    }
    
    private boolean isInCorridor(Cell cell) {
        return cell.countPassableMainNeighbours() == 2 && !cell.isRoomCorner();
    }
    
    private void runInCorridor(Direction direction) {
        // TODO: clean up this horrible, horrible code
        Cell previous = null;
        do {
            // First check if we can continue to the current direction.
            Cell target = player.getCell().getCellTowards(direction);
            if (target.canMoveInto(player.isCorporeal())) {
                previous = player.getCell();
                target.enter(player);
                tick();
                
            } else {
                // We can not move in the current direction. Try to find
                // a new direction.
                
                // No previous cell, we don't know where to go. Return.
                if (previous == null) return;
                
                // Try to move around corners. We look for empty cells in
                // adjacent main directions and want to find exactly one.
                // If we find one, we choose that as the new target. If we
                // find more than one, we bail out.
                Cell newTarget = null;
                for (Cell c : player.getCell().getAdjacentCellsInMainDirections()) {
                    if (c != previous && c.canMoveInto(player.isCorporeal())) {
                        if (newTarget == null) {
                            newTarget = c;
                        } else {
                            return;
                        }
                    }
                }

                // Ok, we have a new target. If we can move to it, we move,
                // otherwise we bail out of the loop.
                if (newTarget != null && newTarget.canMoveInto(player.isCorporeal())) {
                    previous = player.getCell();
                    direction = previous.getDirection(newTarget);
                    newTarget.enter(player);
                    tick();
                } else {
                    return;
                }
            }
            
        } while (!isCurrentCellInteresting(true));
    }
    
    private void runInRoom(Direction direction) {
        // TODO: clean up this horrible, horrible code
        Cell previous = null;
        do {
            // First check if we can continue to the current direction.
            Cell target = player.getCell().getCellTowards(direction);
            if (target.canMoveInto(player.isCorporeal())) {
                boolean first = (previous == null);
                previous = player.getCell();
                target.enter(player);
                tick();
                
                if (!first) {
                    int previousNeighbours = previous.countPassableMainNeighbours();
                    int currentNeighbours = target.countPassableMainNeighbours();
                    if (previousNeighbours != currentNeighbours) {
                        return;
                    }
                }
                
            } else {
                // if we can't move in the current direction, bail out
                return;
            }
            
        } while (!isCurrentCellInteresting(false));
    }
    
    private boolean isCurrentCellInteresting(boolean corridor) {
        Cell cell = player.getCell();
        
        if (cell.isInteresting() || player.seesNonFriendlyCreatures()) {
            return true;
        }

        if (corridor) {
            return cell.countPassableMainNeighbours() > 2;
        } else {
            return false;
        }
    }

    public void movePlayerVertically(boolean up) {
        assertWriteLock();
        
        if (isOver()) return;
        
        JumpTarget target = player.getCell().getJumpTarget(up);
        if (target != null) {
            if (target.isExit()) {
                if (getConsole().ask("Really escape from the dungeon?")) {
                    gameOver("Escaped the dungeon.");
                    return;
                }
            
            } else {
                log.debug("Found portal to target " + target);
                enterRegion(target.getRegion(), target.getLocation());
                tick();
            }
        } else {
            log.debug("No matching portal at current location");
        }
    }
    
    public void skipTurn() {
        assertWriteLock();
        
        if (isOver()) return;
        
        tick();
    }
    
    public void rest(int maxTurns) {
        assertWriteLock();
        
        if (isOver()) return;

        int startTime = globalClock.getTime();
        
        if (maxTurns == -1 && player.getHitpoints() == player.getMaximumHitpoints()) {
            player.message("You don't feel like you need to rest.");
            return;
        }
        
        player.message("Resting...");
        
        while (maxTurns == -1 || maxTurns < startTime - globalClock.getTime()) {
            if (player.getHitpoints() == player.getMaximumHitpoints()) {
                player.message("You feel rested!");
                break;
            }
            
            if (player.getHungerLevel().isHungry()) {
                player.message("You wake up feeling hungry.");
                break;
            }
            
            if (player.seesNonFriendlyCreatures()) {
                player.message("Your rest is interrupted.");
                break;
            }

            tick();
        }
    }


    /**
     * This handles the attack logic for the game. All attacks should
     * go through this function.
     * 
     * @return true if attacker attacked, false if attack was cancelled
     */
    public boolean attack(Creature attacker, Creature target) {
        assertWriteLock();
        
        if (!target.isAlive()) return false;
        
        if (attacker.isPlayer() && target.isFriendly()) {
            if (!ask("Really attack %s?", target.getName())) {
                return false;
            }
        }
        
        // TODO: check for capacity: if attacker is heavily loaded, he can't
        // attack
        
        target.onAttackedBy(attacker);
        
        Attack weapon = attacker.getAttack();
        
        int rollToHit = findRollToHit(attacker, weapon, target);
        if (RandomUtils.rollDie(20) <= rollToHit) {
            message("%s %s %s.", 
                    attacker.You(),
                    attacker.verb(weapon.getAttackVerb()),
                    target.you());
            
            assignDamage(attacker, weapon, target);
            
            attacker.onSuccessfulHit(target, weapon);

            if (!target.isAlive()) {
                attacker.onKilledCreature(target);
            }

        } else {
            message("%s %s.", attacker.You(), attacker.verb("miss"));
        }
        
        return true;
    }

    private int findRollToHit(Creature attacker, Attack weapon, Creature target) {
        int attackerLuck = attacker.getLuck();
        int hitBonus = attacker.getHitBonus();
        int weaponToHit = weapon.getToHit(target);
        int proficiency = attacker.getProficiency(weapon.getWeaponClass());
        int armorClass = target.getArmorClass();
        int targetLuck = target.getLuck();
        
        int roll = 1 + attackerLuck + hitBonus + weaponToHit 
                 + proficiency + armorClass - targetLuck;
        
        if (log.isDebugEnabled()) {
            log.debug(String.format(
                    "hit roll: " + roll + " = 1 + %d + %d + %d + %d + %d - %d",
                    attackerLuck, hitBonus, weaponToHit, proficiency,
                    armorClass, targetLuck));
        }
        return roll;
    }

    private void assignDamage(Creature attacker, Attack weapon, Creature target) {
        int damage = weapon.getDamage(target);
        
        if (log.isDebugEnabled()) {
            log.debug("rolled " + damage + " hp damage from " + weapon);
        }

        target.takeDamage(damage, attacker);
        if (target.getHitpoints() <= 0) {
            attacker.message("%s %s.", target.You(), target.verb("die"));
            target.message("%s %s.", target.You(), target.verb("die"));
            target.die(attacker.getName());
        }
    }

    private static boolean todayIsFriday() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY;
    }

    private static boolean todayIsFestivus() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.MONTH) == Calendar.DECEMBER
            && calendar.get(Calendar.DAY_OF_MONTH) == 23;
    }
    
    private void tick() {
        if (player.isAlive()) {
            do {
                int ticks = player.getTickRate();
                globalClock.tick(ticks, this);
                regionClock.tick(ticks, this);
            } while (player.isAlive() && player.isFainted());
            
            getCurrentRegion().updateLighting();
            getCurrentRegion().updateSeenCells(player.getVisibleCells());
            
            if (listener != null) {
                listener.update();
            }
        }
    }

    public void message(String message, Object... args) {
        getConsole().message(String.format(message, args));
    }
    
    public boolean ask(String question, Object... args) {
        return getConsole().ask(question, args);
    }
    
    private Console getConsole() {
        return new LockSafeConsole(ServiceProvider.getConsole(), getSelfRef());
    }

    public boolean isWizardMode() {
        return wizardMode;
    }
    
    /**
     * Asserts that the current thread has the write lock.
     */
    private void assertWriteLock() {
        getSelfRef().assertWriteLockedByCurrentThread();
    }

    /**
     * Called by {@link Player#die(java.lang.String)} when player dies. 
     * Calculates score for player and sends it to remote server.
     */
    public void gameOver(String reason) {
        over = true;
        
        if (!player.isRegenerated()) {
            // If player has used regeneration we don't save the score since
            // it would be cheating. Otherwise, save the score of the game.
            
            HighScoreService service = ServiceProvider.getHighScoreService();
            service.saveGameScore(getSelfRef(), reason);
        }
    }
    
    public int getTime() {
        return globalClock.getTime();
    }
}
