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
package net.wanhack.model.creature;

import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.wanhack.common.MessageTarget;
import net.wanhack.model.Game;
import net.wanhack.model.common.Actor;
import net.wanhack.model.common.Attack;
import net.wanhack.model.common.Direction;
import net.wanhack.model.item.Item;
import net.wanhack.model.item.armor.Armoring;
import net.wanhack.model.item.food.Corpse;
import net.wanhack.model.item.food.Taste;
import net.wanhack.model.item.weapon.Weapon;
import net.wanhack.model.item.weapon.WeaponClass;
import net.wanhack.model.region.Cell;
import net.wanhack.model.region.Region;
import net.wanhack.model.region.ShortestPathSearcher;
import net.wanhack.service.ServiceProvider;
import net.wanhack.utils.Probability;
import net.wanhack.utils.RandomUtils;
import net.wanhack.utils.exp.Expression;


public abstract class Creature implements Actor, Serializable, MessageTarget {

    private String name;
    private Cell cell;
    private char letter = 0;
    private Color color = Color.GRAY;
    private int hitpoints = 1;
    private boolean friendly = false;
    private boolean immobile = false;
    private boolean corporeal = true;
    private boolean omniscient = false;
    private int level = 1;
    private Integer killExperience;
    private int hitBonus = 0;
    private int armorClass = 10;
    private int luck = 0;
    private int tickRate = 100;
    private int weight = 50 * 1000;
    private boolean canUseDoors = false;
    private Expression corpsePoisonousness = Expression.parse("randint(1, 3)");
    private Weapon wieldedWeapon;
    private int strength = RandomUtils.rollDie(10, 10);
    private int charisma = RandomUtils.rollDie(10, 10);
    private Taste taste = Taste.CHICKEN;
    protected final Armoring armoring = new Armoring();
    private final Set<Item> inventoryItems = new HashSet<Item>();
    protected static final long serialVersionUID = 0;
    
    public Creature(String name) {
        this.name = name;
    }
    
    public Game getGame() {
        return getRegion().getWorld().getGame();
    }
    
    public Color getColor() {
        return color;
    }
    
    public void setColor(Color color) {
        this.color = color;
    }
    
    public int getWeight() {
        return weight;
    }
    
    public void setWeight(int weight) {
        this.weight = weight;
    }
    
    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }
    
    public int getCharisma() {
        return charisma;
    }
    
    public void setCharisma(int charisma) {
        this.charisma = charisma;
    }
    
    public Armoring getArmoring() {
        return armoring;
    }
    
    public int getTickRate() {
        return tickRate;
    }

    public boolean isImmobile() {
        return immobile;
    }

    public void setImmobile(boolean immobile) {
        this.immobile = immobile;
    }
    
    public boolean isCorporeal() {
        return corporeal;
    }
    
    public void setCorporeal(boolean corporeal) {
        this.corporeal = corporeal;
    }
    
    public boolean isOmniscient() {
        return omniscient;
    }
    
    public void setOmniscient(boolean omniscient) {
        this.omniscient = omniscient;
    }
    
    public void setTickRate(int tickRate) {
        this.tickRate = tickRate;
    }
    
    public int getWeightOfCarriedItems() {
        int weight = 0;
        
        if (getWieldedWeapon() != null) {
            weight += getWieldedWeapon().getWeight();
        }
        
        for (Item armor : armoring) {
            weight += armor.getWeight();
        }
        
        for (Item item : inventoryItems) {
            weight += item.getWeight();
        }
        
        return weight;
    }
    
    public Set<Item> getInventoryItems() {
        return Collections.unmodifiableSet(inventoryItems);
    }
    
    public <T extends Item> Collection<T> getInventoryItems(Class<T> cl) {
        Collection<T> result = new ArrayList<T>(inventoryItems.size());
        
        for (Item item : inventoryItems) {
            if (cl.isInstance(item)) {
                result.add(cl.cast(item));
            }
        }
        
        return result;
    }
    
    public void clearInventory() {
        inventoryItems.clear();
    }
    
    public void addItemToInventory(Item item) {
        assert item != null : "null item";
        
        inventoryItems.add(item);
    }

    public void removeItemFromInventory(Item item) {
        assert item != null : "null item";
        
        inventoryItems.remove(item);
    }
    
    public int getArmorClass() {
        return armorClass - armoring.getTotalArmorBonus();
    }
    
    public int getProficiency(WeaponClass weaponClass) {
        return 0;
    }
    
    public void setArmorClass(int armorClass) {
        this.armorClass = armorClass;
    }
    
    public boolean isPlayer() {
        return false;
    }
    
    public char getLetter() {
        return (letter != 0) ? letter : name.charAt(0);
    }
    
    public void setLetter(char letter) {
        this.letter = letter;
    }

    public int getLuck() {
        return luck;
    }

    public void setLuck(int luck) {
        this.luck = luck;
    }

    public int getHitBonus() {
        return hitBonus;
    }
    
    public void setHitBonus(int hitBonus) {
        this.hitBonus = hitBonus;
    }
    
    public boolean isFriendly() {
        return friendly;
    }
    
    public void setFriendly(boolean friendly) {
        this.friendly = friendly;
    }
    
    public int getKillExperience() {
        if (killExperience != null) {
            return killExperience.intValue();
        } else {
            return level * level;
        }
    }
    
    public void setKillExperience(int killExperience) {
        this.killExperience = killExperience;
    }
    
    public int getLevel() {
        return level;
    }
    
    public void setLevel(int level) {
        this.level = level;
    }
    
    public Region getRegion() {
        if (cell != null) {
            return cell.getRegion();
        } else {
            return null;
        }
    }
    
    public void setCell(Cell cell) {
        if (this.cell != null) {
            this.cell.setCreature(null);
        }
        
        this.cell = cell;
        
        if (this.cell != null) {
            this.cell.setCreature(this);
        }
    }
    
    public Cell getCell() {
        return cell;
    }
    
    public int getHitpoints() {
        return hitpoints;
    }
    
    // TODO: don't allow hitpoints to exceed maximum
    public void setHitpoints(int hitpoints) {
        this.hitpoints = hitpoints;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public boolean seesCreature(Creature creature) {
        return canSee(creature.getCell());
    }
    
    public final boolean isAdjacentToCreature(Creature creature) {
        return cell.isAdjacent(creature.cell);
    }
    
    public boolean isDestroyed() {
        return !isAlive();
    }
    
    public boolean isAlive() {
        return hitpoints > 0 && cell != null;
    }
    
    public final int act(Game game) {
        onTick(game);
        
        return getTickRate();
    }
    
    protected boolean moveTowards(Cell targetCell) {
        ShortestPathSearcher searcher = new CreatureShortestPathSearcher(this);
        Cell first = searcher.findFirstCellOnShortestPath(cell, targetCell);
        if (first != null && canMoveTo(first)) {
            setCell(first);
            return true;
        } else {
            return false;
        }
    }
    
    protected boolean move(Direction direction) {
        Cell target = cell.getCellTowards(direction);
        
        if (canMoveTo(target)) {
            target.enter(this);
            return true;
        } else if (target.isClosedDoor() && getCanUseDoors()) {
            target.openDoor(this);
            return true;

        } else {
            return false;
        }
    }
    
    protected boolean canMoveTo(Cell cell) {
        return cell.canMoveInto(corporeal);
    }

    protected void moveRandomly() {
        if (Probability.check(75)) {
            move(RandomUtils.randomEnum(Direction.class));
        }
    }
    
    protected abstract void onTick(Game game);

    public boolean canSee(Cell target) {
        if (omniscient) return true;
        
        return calculateCanSee(target);
    }
    
    protected final boolean calculateCanSee(Cell target) {
        for (Cell c : cell.getCellsBetween(target)) {
            if (!c.canSeeThrough()) {
                return false;
            }
        }
        
        return true;
    }
    
    public Set<Creature> getAdjacentCreatures() {
        Set<Creature> adjacent = new HashSet<Creature>();
        
        for (Cell c : cell.getAdjacentCells()) {
            if (c.getCreature() != null) {
                adjacent.add(c.getCreature());
            }
        }
        
        return adjacent;
    }
    
    @Override
    public String toString() {
        return getName() + " [hp=" + hitpoints + "]";
    }
    
    public void onAttackedBy(Creature attacker) {
        if (attacker.isPlayer()) {
            setFriendly(false);
        }
    }

    public void onSuccessfulHit(Creature target, Attack weapon) {
    }

    public void onKilledCreature(Creature target) {
    }
    
    public void takeDamage(int points, Creature attacker) {
        hitpoints = Math.max(0, hitpoints - points);
    }

    public void talk(Creature target) {
        target.say(this, "Hrmph.");
    }

    public final Attack getAttack() {
        Attack weapon = getWieldedWeapon();
        if (weapon == null) {
            weapon = getNaturalAttack();
            assert weapon != null : "null natural weapon";
        }
        
        return weapon;
    }
    
    /**
     * Returns the "natural weapon" of this creature. The returned weapon
     * should never be null.
     */
    protected abstract Attack getNaturalAttack();
    
    /**
     * Returns the weapon that this creatures currently wield, or
     * null if the creature has no wielded weapon.
     */
    public Weapon getWieldedWeapon() {
        return wieldedWeapon;
    }
    
    public void setWieldedWeapon(Weapon wieldedWeapon) {
        this.wieldedWeapon = wieldedWeapon;
    }

    public Expression getCorpsePoisonousness() {
        return corpsePoisonousness;
    }
    
    public void setCorpsePoisonousness(Expression corpsePoisonousness) {
        this.corpsePoisonousness = corpsePoisonousness;
    }
    
    public String you() {
        return getName();
    }

    public String You() {
        String name = you();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }
    
    public String verb(String verb) {
        if (verb.endsWith("s")) { // miss -> misses
            return verb + "es";
        } else {
            return verb + "s";
        }
    }

    /**
     * Returns the corpse for this creature. Returns null if the creature
     * does not leave a corpse behind.
     */
    protected Item getCorpse() {
        if (!isCorporeal()) {
            return null;
        }
        
        // TODO: add article a/an in front of the name
        Corpse corpse = new Corpse(name + " corpse");
        corpse.setWeight(this.weight);
        corpse.setColor(this.getColor());
        corpse.setLevel(this.level);
        corpse.setPoisonDamage(this.getCorpsePoisonousness());
        corpse.setEffectiveness(Math.max((int)(0.05 * this.weight), 800));
        corpse.setTaste(taste);
        
        return corpse;
    }
    
    public void die(String killer) {
        hitpoints = 0;
        
        cell.addItems(inventoryItems);
        clearInventory();
        
        if (wieldedWeapon != null) {
            cell.addItem(wieldedWeapon);
            wieldedWeapon = null;
        }
        
        cell.addItems(armoring.removeAllArmors());

        Item corpse = getCorpse();
        if (corpse != null) {
            cell.addItem(corpse);
        }
        
        removeFromGame();
    }
    
    protected final void removeFromGame() {
        setCell(null);
    }
    
    protected <T> T createItem(Class<T> cl, String name) {
        return ServiceProvider.getObjectFactory().create(cl, name);
    }

    public int getLighting() {
        int effectiveness = 0;
        
        for (Item item : inventoryItems) {
            effectiveness += item.getLighting();
        }
        
        return effectiveness;
    }

    public boolean getCanUseDoors() {
        return canUseDoors;
    }
    
    public void setCanUseDoors(boolean canUseDoors) {
        this.canUseDoors = canUseDoors;
    }

    /**
     * Sends a message to this creature. Overridden by player so that
     * messages sent to player are shown on console.
     * <p>
     * This message is useful for generic routines where message needs
     * to be shown if player performs an action, but not if a monster
     * does it. (For example, if opening door fails, "Door resists."
     * should be shown to a player, but if monster fails to open the
     * door, no message should be shown.) The door opening routine thus
     * simply calls <code>opener.message("Door resists.")</code>.
     */
    public void message(String message, Object... args) {
    }

    public void say(Creature talker, String message, Object... args) {
    }
    
    public boolean ask(boolean defaultValue, String question, Object... args) {
        return defaultValue;
    }
}
