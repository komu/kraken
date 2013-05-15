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

package net.wanhack.model

import java.io.ObjectOutputStream
import java.io.Serializable
import java.util.Calendar
import net.wanhack.model.GameConfiguration.PetType
import net.wanhack.model.common.Attack
import net.wanhack.model.common.Console
import net.wanhack.model.common.Direction
import net.wanhack.model.creature.Creature
import net.wanhack.model.creature.Player
import net.wanhack.model.creature.pets.Doris
import net.wanhack.model.creature.pets.Lassie
import net.wanhack.model.creature.pets.Pet
import net.wanhack.model.events.GameEvent
import net.wanhack.model.events.global.HungerEvent
import net.wanhack.model.events.global.RegainHitPointsEvent
import net.wanhack.model.events.region.CreateMonstersEvent
import net.wanhack.model.item.Item
import net.wanhack.model.item.armor.Armor
import net.wanhack.model.item.food.Food
import net.wanhack.model.item.weapon.Weapon
import net.wanhack.model.region.Cell
import net.wanhack.model.region.CellType
import net.wanhack.model.region.Region
import net.wanhack.model.region.World
import net.wanhack.service.ServiceProvider
import net.wanhack.utils.RandomUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import net.wanhack.utils.isFriday
import net.wanhack.utils.isFestivus

class Game(val config: GameConfiguration, val wizardMode: Boolean): Serializable, IGame {
    private val globalClock = Clock()
    private val regionClock = Clock()
    override val player = Player(config.name)
    private val world = World(this)
    override var maxDungeonLevel = 0
    private var over = false
    var listener: (() -> Unit) = { }
    val selfRef = DefaultGameRef(this);

    {
        player.sex = config.sex
    }

    override fun revealCurrentRegion() {
        currentRegion.reveal()
    }

    override fun start() {
        assertWriteLock()

        val objectFactory = ServiceProvider.objectFactory
        player.wieldedWeapon = objectFactory.create(javaClass<Weapon>(), "a dagger")
        player.inventoryItems.add(objectFactory.create(javaClass<Item>(), "food ration"))
        player.inventoryItems.add(objectFactory.create(javaClass<Item>(), "a cyanide capsule"))
        enterRegion("start", "from up")

        if (config.pet == PetType.DORIS)
            putPetNextToPlayer(Doris("Doris"))
        else if (config.pet == PetType.LASSIE)
            putPetNextToPlayer(Lassie("Lassie"))

        currentRegion.updateLighting()
        player.act(this)
        currentRegion.updateSeenCells(player.visibleCells!!)
        player.message("Hello %s, welcome to Wanhack!", player.name)

        val today = Calendar.getInstance()
        if (today.isFestivus()) {
            player.message("Happy Festivus!")
            player.strength += 10
            player.luck = 2
            player.inventoryItems.add(objectFactory.create(javaClass<Item>(), "Aluminium Pole"))
        }

        if (today.isFriday()) {
            player.message("It is Friday, good luck!")
            player.luck = 1
        }

        addGlobalEvent(HungerEvent())
        addGlobalEvent(RegainHitPointsEvent())

        listener()
    }

    private fun putPetNextToPlayer(pet: Creature): Boolean {
        var target: Cell? = null
        for (cell in player.cell.adjacentCells)
            if (cell.cellType.isFloor() && cell.creature == null)
                target = cell

        val targetCell = target
        if (targetCell != null) {
            pet.cell = targetCell
            regionClock.schedule(pet.tickRate, pet)
            return true
        } else {
            return false
        }
    }

    public fun addGlobalEvent(event: GameEvent) {
        globalClock.schedule(event.rate, event)
    }

    public fun addRegionEvent(event: GameEvent) {
        regionClock.schedule(event.rate, event)
    }

    override val dungeonLevel: Int
        get() = currentRegion.level

    override val cellInFocus: Cell
        get() = selectedCell ?: player.cell

    override val selectedCell: Cell?
        get() = null

    public override fun save(oos: ObjectOutputStream?): Unit {
        oos?.writeObject(this)
    }

    public fun enterRegion(name: String, location: String) {
        val region = world.getRegion(player, name)
        val oldCell = player.cellOrNull
        val oldRegion = oldCell?.region
        region.setPlayerLocation(player, location)
        if (region != oldRegion) {
            regionClock.clear()
            maxDungeonLevel = Math.max(region.level, maxDungeonLevel)
            if (oldCell != null) {
                for (cell in oldCell.adjacentCells) {
                    val creature = cell.creature
                    if (creature is Pet)
                        putPetNextToPlayer(creature)
                }
            }

            addRegionEvent(CreateMonstersEvent(region))
            for (creature in region.getCreatures())
                regionClock.schedule(creature.tickRate, creature)
        }
    }

    public fun addCreature(creature: Creature, target: Cell) {
        creature.cell = target
        if (target.region == currentRegion) {
            regionClock.schedule(creature.tickRate, creature)
        }
    }

    public override fun talk(): Unit {
        assertWriteLock()
        if (over)
            return

        val adjacent = player.adjacentCreatures
        if (adjacent.size == 1) {
            val creature = adjacent.iterator().next()
            creature.talk(player)
            tick()
        } else if (adjacent.empty) {
            player.message("There's no-one to talk to.")
        } else {
            val dir = console.selectDirection()
            if (dir != null) {
                val creature = player.cell.getCellTowards(dir).creature
                if (creature != null) {
                    creature.talk(player)
                    tick()
                } else {
                    player.message("There's nobody in selected direction.")
                }
            }

        }
    }

    override fun openDoor() {
        assertWriteLock()
        if (over)
            return

        val closedDoors  = player.cell.getAdjacentCellsOfType(CellType.CLOSED_DOOR)
        if (closedDoors.isEmpty()) {
            player.message("There are no closed doors around you.")
        } else if (closedDoors.size == 1) {
            closedDoors[0].openDoor(player)
            tick()
        } else {
            val dir = console.selectDirection()
            if (dir != null) {
                val cell = player.cell.getCellTowards(dir)
                if (cell.cellType == CellType.CLOSED_DOOR) {
                    cell.openDoor(player)
                    tick()
                } else {
                    player.message("No closed door in selected direction.")
                }
            }
        }
    }

    override fun closeDoor() {
        assertWriteLock()
        if (over)
            return

        val openDoors = player.cell.getAdjacentCellsOfType(CellType.OPEN_DOOR)
        if (openDoors.isEmpty()) {
            player.message("There are no open doors around you.")
        } else if (openDoors.size == 1) {
            closeDoor(openDoors[0])
        } else {
            val dir = console.selectDirection()
            if (dir != null) {
                val cell = player.cell.getCellTowards(dir)
                if (cell.cellType == CellType.OPEN_DOOR)
                    closeDoor(cell)
                else
                    player.message("No open door in selected direction.")
            }
        }
    }

    private fun closeDoor(door: Cell): Unit {
        assertWriteLock()

        if (door.closeDoor(player))
            tick()
    }

    override fun pickup() {
        assertWriteLock()
        if (over)
            return

        val cell = player.cell
        val items = cell.items
        if (items.empty) {
            player.message("There's nothing to pick up.")
        } else if (items.size == 1) {
            val item = items.iterator().next()
            player.inventoryItems.add(item)
            cell.items.remove(item)
            player.message("Picked up %s.", item.title)
            tick()
        } else {
            for (item in console.selectItems("Select items to pick up", items)) {
                player.inventoryItems.add(item)
                cell.items.remove(item)
                player.message("Picked up %s.", item.title)
            }
            tick()
        }
    }

    override fun wield() {
        assertWriteLock()
        if (over)
            return

        val oldWeapon = player.wieldedWeapon
        val weapon = console.selectItem(javaClass<Weapon>(), "Select weapon to wield", player.getInventoryItems(javaClass<Weapon>()))
        if (weapon != null && weapon != oldWeapon) {
            player.wieldedWeapon = weapon
            player.inventoryItems.remove(weapon)
            if (oldWeapon != null) {
                player.message("You were wielding %s.", oldWeapon.title)
                player.inventoryItems.add(oldWeapon)
            }

            player.message("You wield %s.", weapon.title)
            tick()
        }
    }

    override fun wear() {
        assertWriteLock()
        if (over)
            return

        val armor = console.selectItem(javaClass<Armor>(), "Select armor to wear", player.getInventoryItems(javaClass<Armor>()))
        if (armor != null ) {
            val oldArmor = player.replaceArmor(armor)
            player.inventoryItems.remove(armor)
            if (oldArmor != null) {
                player.message("You were wearing %s.", oldArmor.title)
                player.inventoryItems.add(oldArmor)
            }

            player.message("You are now wearing %s.", armor.title)
            tick()
        }
    }

    override fun drop() {
        assertWriteLock()
        if (over)
            return

        for (item in console.selectItems("Select items to drop", player.inventoryItems))
            drop(item)
    }

    override fun drop(item: Item) {
        assertWriteLock()
        if (over)
            return

        player.inventoryItems.remove(item)
        player.cell.items.add(item)
        message("Dropped %s.", item.title)
        tick()
    }

    override fun eat() {
        assertWriteLock()
        if (over)
            return

        val food = console.selectItem(javaClass<Food>(), "Select food to eat", player.getInventoryItems(javaClass<Food>()))
        if (food != null) {
            player.inventoryItems.remove(food)
            food.onEatenBy(player)
            tick()
        }
    }

    override fun search() {
        assertWriteLock()
        if (over)
            return

        for (cell in player.cell.adjacentCells)
            if (cell.search(player))
                break

        tick()
    }

    override fun fling() {
        assertWriteLock()
        if (over)
            return

        val projectile = console.selectItem(javaClass<Item>(), "Select item to throw", player.getInventoryItems(javaClass<Item>()))
        if (projectile != null) {
            val dir = console.selectDirection()
            if (dir != null) {
                player.inventoryItems.remove(projectile)
                var currentCell = player.cell
                var nextCell = currentCell.getCellTowards(dir)
                val range = player.getThrowRange(projectile.weight)

                var d = 0
                while (d < range && nextCell.isPassable()) {
                    currentCell = nextCell
                    nextCell = currentCell.getCellTowards(dir)
                    val creature = currentCell.creature
                    if (creature != null)
                        if (throwAttack(player, projectile, creature, d))
                            break

                    d++
                }
                currentCell.items.add(projectile)
                tick()
            }
        }
    }

    private fun throwAttack(attacker: Creature, projectile: Item, target: Creature, distance: Int): Boolean {
        target.onAttackedBy(attacker)
        val rollToHit = findRollToHit(attacker, projectile, target)
        if (RandomUtils.rollDie(20) <= rollToHit) {
            message("%s %s %s at %s.", attacker.You(), attacker.verb("throw"), projectile.title, target.you())
            assignDamage(attacker, projectile, target)
            attacker.onSuccessfulHit(target, projectile)
            if (!target.alive)
                attacker.onKilledCreature(target)

            return true
        } else {
            message("%s flies past %s.", projectile.title, target.name)
            return false
        }
    }

    override val currentRegion: Region
        get() = player.region

    override val score: Int
        get() = player.experience

    override fun movePlayer(direction: Direction) {
        assertWriteLock()
        if (over)
            return

        val cell = player.cell.getCellTowards(direction)
        val creatureInCell = cell.creature
        if (creatureInCell != null) {
            if (attack(player, creatureInCell))
                tick()
        } else if (cell.canMoveInto(player.corporeal)) {
            cell.enter(player)
            tick()
        } else {
            log.trace("Can't move towards: " + direction)
        }
    }

    public override fun runTowards(direction: Direction) {
        assertWriteLock()
        if (over)
            return

        var target = player.cell.getCellTowards(direction)
        if (isInCorridor(target))
        {
            runInCorridor(direction)
        }
        else
        {
            runInRoom(direction)
        }
    }

    private fun isInCorridor(cell: Cell) =
        cell.countPassableMainNeighbours() == 2 && !cell.isRoomCorner()

    private fun runInCorridor(initialDirection: Direction): Unit {
        var direction = initialDirection
        var previous: Cell? = null
        do {
            val target = player.cell.getCellTowards(direction)
            if (target.canMoveInto(player.corporeal)) {
                previous = player.cell
                target.enter(player)
                tick()
            } else {
                if (previous == null)
                    return

                var newTarget: Cell? = null
                for (c in player.cell.adjacentCellsInMainDirections)
                    if (c != previous && c.canMoveInto(player.corporeal))
                        if (newTarget == null)
                            newTarget = c
                        else
                            return

                val targetCell = newTarget
                if (targetCell != null && targetCell.canMoveInto(player.corporeal)) {
                    previous = player.cell
                    direction = player.cell.getDirection(targetCell)!!
                    targetCell.enter(player)
                    tick()
                } else {
                    return
                }
            }
        } while (!isCurrentCellInteresting(true))
    }

    private fun runInRoom(direction: Direction) {
        var first = true
        do {
            val target = player.cell.getCellTowards(direction)
            if (!target.canMoveInto(player.corporeal))
                break;

            val previous = player.cell
            target.enter(player)
            tick()

            if (!first && previous.countPassableMainNeighbours() != target.countPassableMainNeighbours())
                break

            first = false
        } while (!isCurrentCellInteresting(false))
    }

    private fun isCurrentCellInteresting(corridor: Boolean): Boolean {
        val cell = player.cell
        if (cell.isInteresting() || player.seesNonFriendlyCreatures())
            return true

        if (corridor) {
            return cell.countPassableMainNeighbours() > 2
        } else {
            return false
        }
    }

    public override fun movePlayerVertically(up: Boolean) {
        assertWriteLock()
        if (over)
            return

        val target = player.cell.getJumpTarget(up)
        if (target != null) {
            if (target.isExit) {
                if (console.ask("Really escape from the dungeon?")) {
                    gameOver("Escaped the dungeon.")
                    return
                }

            } else {
                log.debug("Found portal to target $target")
                enterRegion(target.region, target.location)
                tick()
            }
        } else {
            log.debug("No matching portal at current location")
        }
    }
    public override fun skipTurn(): Unit {
        assertWriteLock()
        if (over)
            return

        tick()
    }
    public override fun rest(maxTurns: Int): Unit {
        assertWriteLock()
        if (over)
            return

        val startTime = globalClock.time
        if (maxTurns == -1 && player.hitPoints == player.maximumHitPoints) {
            player.message("You don't feel like you need to rest.")
            return
        }

        player.message("Resting...")
        while (maxTurns == -1 || maxTurns < startTime - globalClock.time)
        {
            if (player.hitPoints == player.maximumHitPoints) {
                player.message("You feel rested!")
                break
            }

            if (player.getHungerLevel().hungry) {
                player.message("You wake up feeling hungry.")
                break
            }

            if (player.seesNonFriendlyCreatures()) {
                player.message("Your rest is interrupted.")
                break
            }

            tick()
        }
    }
    public fun attack(attacker: Creature, target: Creature): Boolean {
        assertWriteLock()
        if (!target.alive)
            return false

        if (attacker.isPlayer && target.friendly)
            if (!ask("Really attack %s?", target.name))
                return false

        target.onAttackedBy(attacker)
        val weapon = attacker.attack
        val rollToHit: Int = findRollToHit(attacker, weapon, target)
        if (RandomUtils.rollDie(20) <= rollToHit) {
            message("%s %s %s.", attacker.You(), attacker.verb(weapon.attackVerb), target.you())
            assignDamage(attacker, weapon, target)
            attacker.onSuccessfulHit(target, weapon)
            if (!target.alive)
                attacker.onKilledCreature(target)
        } else {
            message("%s %s.", attacker.You(), attacker.verb("miss"))
        }
        return true
    }

    private fun findRollToHit(attacker: Creature, weapon: Attack, target: Creature): Int {
        val attackerLuck = attacker.luck
        val hitBonus = attacker.hitBonus
        val weaponToHit = weapon.getToHit(target)
        val proficiency = attacker.getProficiency(weapon.weaponClass)
        val armorClass = target.armorClass
        val targetLuck = target.luck
        val roll = 1 + attackerLuck + hitBonus + weaponToHit + proficiency + armorClass - targetLuck

        if (log.isDebugEnabled())
            log.debug("hit roll: $roll = 1 + %d + %d + %d + %d + %d - %d".format(attackerLuck, hitBonus, weaponToHit, proficiency, armorClass, targetLuck))

        return roll
    }

    private fun assignDamage(attacker: Creature, weapon: Attack, target: Creature) {
        val damage = weapon.getDamage(target)
        if (log.isDebugEnabled())
            log.debug("rolled $damage hp damage from $weapon")

        target.takeDamage(damage, attacker)
        if (target.hitPoints <= 0) {
            attacker.message("%s %s.", target.You(), target.verb("die"))
            target.message("%s %s.", target.You(), target.verb("die"))
            target.die(attacker.name)
        }
    }

    private fun tick() {
        if (player.alive) {
            do {
                val ticks = player.tickRate
                globalClock.tick(ticks, this)
                regionClock.tick(ticks, this)
            } while (player.alive && player.isFainted())

            currentRegion.updateLighting()
            currentRegion.updateSeenCells(player.visibleCells!!)

            listener()
        }
    }

    public fun message(message: String, vararg args: Any?): Unit {
        console.message(message.format(*args))
    }

    public fun ask(question: String, vararg args: Any?): Boolean =
        console.ask(question, *args)

    val console: Console 
        get() = LockSafeConsole(ServiceProvider.console, selfRef)

    private fun assertWriteLock() {
        selfRef.assertWriteLockedByCurrentThread()
    }

    fun gameOver(reason: String) {
        over = true
        if (!player.regenerated) {
            ServiceProvider.highScoreService.saveGameScore(selfRef, reason)
        }
    }

    override val time: Int
        get() = globalClock.time

    class object {
        private val log: Log = LogFactory.getLog(javaClass<Game>())
    }
}
