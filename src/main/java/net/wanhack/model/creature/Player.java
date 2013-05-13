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
import java.util.ArrayList;
import java.util.List;

import net.wanhack.model.Game;
import net.wanhack.model.IGame;
import net.wanhack.model.common.Attack;
import net.wanhack.model.events.OneTimeEvent;
import net.wanhack.model.item.Item;
import net.wanhack.model.item.armor.Armor;
import net.wanhack.model.item.weapon.NaturalWeapon;
import net.wanhack.model.item.weapon.WeaponClass;
import net.wanhack.model.region.Cell;
import net.wanhack.model.region.CellSet;
import net.wanhack.model.region.VisibilityChecker;
import net.wanhack.model.skill.Proficiency;
import net.wanhack.model.skill.SkillSet;
import net.wanhack.utils.RandomUtils;

public class Player extends Creature {
    
    private int maximumHitpoints;
    private int experience = 0;
    private final Attack hit = new NaturalWeapon("hit", "0", "randint(1, 3)");
    private final SkillSet skills = new SkillSet();
    private CellSet visibleCells = null;
    private int hunger = 2000;
    private boolean fainted = false;
    private boolean regenerated = false;
    private Sex sex = RandomUtils.randomEnum(Sex.class);
    
    /** How far the player sees? */
    private int sight = 20;
    
    public Player(String name) {
        super(name);

        setLetter('@');
        setColor(Color.BLUE);
        
        maximumHitpoints = 8 + RandomUtils.rollDie(5);
        setHitpoints(maximumHitpoints);
        setTickRate(90);
        
        skills.setWeaponProficiency(WeaponClass.SWORD, Proficiency.BASIC);
    }

    public Sex getSex() {
        return sex;
    }
    
    public void setSex(Sex sex) {
        this.sex = sex;
    }

    /**
     * Calculates how far the player can throw an item with given weight.
     */
    public int getThrowRange(int weight) {
        // TODO: calculate throw range based on weight and strength
        return (weight < 1000)  ? 30
             : (weight < 2000)  ? 20
             : (weight < 3000)  ? 15
             : (weight < 5000)  ? 10
             : (weight < 10000) ? 8
             : (weight < 15000) ? 5
             : (weight < 20000) ? 3
             : (weight < 25000) ? 2
             : (weight < 50000) ? 1 
             : 0;
    }
    
    @Override
    public int getTickRate() {
        int intrinsicRate = super.getTickRate();
        
        return Math.max(1, intrinsicRate + getWeightPenalty());
    }

    private int getWeightPenalty() {
        int carriedWeightInKilograms = getWeightOfCarriedItems() / 1000;
        int factor = 30;
        
        return factor * carriedWeightInKilograms / getStrength();
    }
    
    public Armor replaceArmor(Armor armor) {
        return armoring.replaceArmor(armor);
    }
    
    public List<? extends Item> getActivatedItems() {
        ArrayList<Item> result = new ArrayList<Item>();
        
        if (getWieldedWeapon() != null) {
            result.add(getWieldedWeapon());
        }
        
        for (Armor armor : armoring) {
            result.add(armor);
        }

        return result;
    }
    
    @Override
    public void die(String killer) {
        Game game = getGame();
        if (game.isWizardMode() && !game.ask("Die?")) {
            regenerate();
            game.message("You regenerate.");
        } else {
            // super.die causes all sorts of problems for player, since
            // we want to access the game state after dying..
            // super.die(killer);
            game.gameOver(killer);
        }
    }
    
    public boolean isRegenerated() {
        return regenerated;
    }
    
    private void regenerate() {
        regenerated = true;
        setHitpoints(getMaximumHitpoints());
        setHunger(1000);
    }
    
    @Override
    public int getProficiency(WeaponClass weaponClass) {
        return skills.getWeaponProficiency(weaponClass).getBonus();
    }
    
    @Override
    public boolean isPlayer() {
        return true;
    }

    @Override
    public void onSuccessfulHit(Creature target, Attack weapon) {
        skills.exerciseSkill(weapon.getWeaponClass(), this);
    }
    
    @Override
    public void onKilledCreature(Creature target) {
        addExperience(target.getKillExperience());
    }
    
    public boolean isFainted() {
        return fainted;
    }

    public void decreaseHungriness(int effectiveness) {
        hunger += effectiveness;
    }
    
    public void increaseHungriness(Game game) {
        if (fainted) return;
        
        hunger--;
        
        if (hunger < 0) {
            message("You faint.");
            setHitpoints(1);
            
            fainted = true;
            int ticks = 100 * (5 + RandomUtils.rollDie(45));
            
            game.addGlobalEvent(new OneTimeEvent(ticks) {
                @Override
                protected void fire(IGame game) {
                    if (fainted) {
                        message("You wake up.");
                        if (hunger <= 0) {
                            hunger = 10;
                        }
                        fainted = false;
                    }
                }
            });
        }
    }
    
    public HungerLevel getHungerLevel() {
        return HungerLevel.getHungerLevel(hunger);
    }
    
    public int getHunger() {
        return hunger;
    }
    
    public void setHunger(int hunger) {
        this.hunger = hunger;
    }
    
    private int newHitpoints() {
        return 3 + RandomUtils.rollDie(3);
    }
    
    public int getExperience() {
        return experience;
    }
    
    private void addExperience(int exp) {
        experience += exp;
        
        if (experience >= experienceNeededForNextLevel()) {
            gainLevel();
        }
    }

    private void gainLevel() {
        int newhp = newHitpoints();
        
        maximumHitpoints += newhp;
        setHitpoints(Math.min(getHitpoints() + newHitpoints(), maximumHitpoints));
        setLevel(getLevel() + 1);
    }

    private int experienceNeededForNextLevel() {
        int level = getLevel();
        
        if (level < 10) {
            return (10 * (1 << level));
        } else if (level < 20) {
            return (10000 * (1 << (level - 10)));
        } else {
            return (10000000 * (level - 19));
        }
    }

    public boolean seesCreatures() {
        for (Cell cell : visibleCells) {
            Creature creature = cell.getCreature();
            if (creature != null && creature != this) {
                return true;
            }
        }
        return false;
    }
    
    public boolean seesNonFriendlyCreatures() {
        for (Cell cell : visibleCells) {
            Creature creature = cell.getCreature();
            if (creature != null && creature != this && !creature.isFriendly()) {
                return true;
            }
        }
        return false;
    }
    
    public int getMaximumHitpoints() {
        return maximumHitpoints;
    }
    
    @Override
    protected void onTick(Game game) {
        updateVisiblePoints();
    }
    
    public void regainHitpoint() {
        setHitpoints(Math.min(getMaximumHitpoints(), getHitpoints() + 1));
    }

    @Override
    public boolean canSee(Cell cell) {
        return visibleCells.contains(cell);
    }
    
    private void updateVisiblePoints() {
        visibleCells = new VisibilityChecker().getVisibleCells(getCell(), sight);
    }

    public SkillSet getSkills() {
        return skills;
    }

    /**
     * Returns the cells on current region that player sees.
     */
    public CellSet getVisibleCells() {
        return visibleCells;
    }

    /**
     * Returns the cells on current region that player does not see.
     */
    public CellSet getInvisibleCells() {
        CellSet cells = getRegion().getCells();
        cells.removeAll(visibleCells);
        return cells;
    }
    
    @Override
    public String you() {
        return "you";
    }
    
    @Override
    public Attack getNaturalAttack() {
        return hit;
    }
    
    @Override
    public String verb(String verb) {
        return verb;
    }
    
    @Override
    public void message(String message, Object... args) {
        getGame().message(message, args);
    }

    @Override
    public void say(Creature talker, String message, Object... args) {
        getGame().message('"' + message + '"', args);
    }
    
    @Override
    public boolean ask(boolean defaultValue, String question, Object... args) {
        return getGame().ask(question, args);
    }
    
    /**
     * Returns null. Player does not leave a corpse.
     * 
     * @see net.wanhack.model.creature.Creature#getCorpse()
     */
    @Override
    protected Item getCorpse() {
        return null;
    }
}
