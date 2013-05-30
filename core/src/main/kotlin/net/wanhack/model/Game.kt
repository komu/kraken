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

import java.util.Calendar
import net.wanhack.model.GameConfiguration.PetType
import net.wanhack.model.common.Attack
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
import net.wanhack.utils.RandomUtils
import net.wanhack.utils.isFriday
import net.wanhack.utils.isFestivus
import net.wanhack.utils.logger
import net.wanhack.definitions.Creatures
import net.wanhack.definitions.Items
import net.wanhack.definitions.Weapons
import net.wanhack.model.common.Console
import net.wanhack.utils.MaximumCounter
import net.wanhack.service.config.ObjectFactory
import net.wanhack.service.score.HighScoreService

class Game(val config: GameConfiguration, val console: Console, val listener: () -> Unit) : ReadOnlyGame {
    private val globalClock = Clock()
    private val regionClock = Clock()
    override val player = Player(config.name)
    private val world = World(this)

    val objectFactory = ObjectFactory()

    val maximumDungeonLevel = MaximumCounter(0)
    var over = false;

    {
        player.sex = config.sex

        objectFactory.addDefinitions(Weapons)
        objectFactory.addDefinitions(Items)
        objectFactory.addDefinitions(Creatures)
    }

    fun revealCurrentRegion() {
        currentRegion.reveal()
    }

    fun start() {
        player.wieldedWeapon = Weapons.dagger.create()
        player.inventoryItems.add(Items.foodRation.create())
        player.inventoryItems.add(Items.cyanideCapsule.create())
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
            player.inventoryItems.add(Weapons.aluminiumPole.create())
        }

        if (today.isFriday()) {
            player.message("It is Friday, good luck!")
            player.luck = 1
        }

        addGlobalEvent(HungerEvent())
        addGlobalEvent(RegainHitPointsEvent())
    }

    private fun putPetNextToPlayer(pet: Creature) {
        val target = player.cell.adjacentCells.find { cell ->
            cell.isFloor() && cell.creature == null
        }

        if (target != null) {
            pet.cell = target
            regionClock.schedule(pet.tickRate, pet)
        }
    }

    fun addGlobalEvent(event: GameEvent) {
        globalClock.schedule(event.rate, event)
    }

    fun addRegionEvent(event: GameEvent) {
        regionClock.schedule(event.rate, event)
    }

    override val maxDungeonLevel: Int
        get() = maximumDungeonLevel.value

    override val dungeonLevel: Int
        get() = currentRegion.level

    override val cellInFocus: Cell
        get() = selectedCell ?: player.cell

    override val selectedCell: Cell?
        get() = null

    fun enterRegion(name: String, location: String) {
        val region = world.getRegion(name)
        val oldCell = player.cellOrNull
        val oldRegion = oldCell?.region
        region.setPlayerLocation(player, location)
        if (region != oldRegion) {
            regionClock.clear()
            maximumDungeonLevel.update(region.level)
            if (oldCell != null) {
                val pet = oldCell.findAdjacentPet()
                if (pet != null)
                    putPetNextToPlayer(pet)
            }

            addRegionEvent(CreateMonstersEvent(region))
            for (creature in region.creatures)
                regionClock.schedule(creature.tickRate, creature)
        }
    }

    private fun Cell.findAdjacentPet(): Pet? {
        for (cell in adjacentCells) {
            val pet = cell.creature as? Pet
            if (pet != null)
                return pet
        }
        return null
    }

    public fun addCreature(creature: Creature, target: Cell) {
        creature.cell = target
        if (target.region == currentRegion) {
            regionClock.schedule(creature.tickRate, creature)
        }
    }

    fun talk() = gameAction {
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

    fun openDoor() = gameAction {
        val closedDoors  = player.cell.adjacentCells.filter { it.cellType == CellType.CLOSED_DOOR }
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

    fun closeDoor() = gameAction {
        val openDoors = player.cell.adjacentCells.filter { it.cellType == CellType.OPEN_DOOR }
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

    private fun closeDoor(door: Cell) {
        if (door.closeDoor(player))
            tick()
    }

    fun pickup() = gameAction {
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

    fun wield() = gameAction {
        val oldWeapon = player.wieldedWeapon
        val weapon = console.selectItem("Select weapon to wield", player.getInventoryItems(javaClass<Weapon>()))
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

    fun wear() = gameAction {
        val armor = console.selectItem("Select armor to wear", player.getInventoryItems(javaClass<Armor>()))
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

    fun drop() = gameAction {
        for (item in console.selectItems("Select items to drop", player.inventoryItems))
            doDrop(item)
    }

    fun drop(item: Item) = gameAction {
        doDrop(item)
        tick()
    }

    private fun doDrop(item: Item) {
        player.inventoryItems.remove(item)
        player.cell.items.add(item)
        message("Dropped %s.", item.title)
    }

    fun eat() = gameAction {
        val food = console.selectItem("Select food to eat", player.getInventoryItems(javaClass<Food>()))
        if (food != null) {
            player.inventoryItems.remove(food)
            food.onEatenBy(player)
            tick()
        }
    }

    fun search() = gameAction {
        for (cell in player.cell.adjacentCells)
            if (cell.search(player))
                break

        tick()
    }

    fun fling() = gameAction {
        val projectile = console.selectItem("Select item to throw", player.getInventoryItems(javaClass<Item>()))
        if (projectile != null) {
            val dir = console.selectDirection()
            if (dir != null) {
                player.inventoryItems.remove(projectile)
                var currentCell = player.cell
                var nextCell = currentCell.getCellTowards(dir)
                val range = player.getThrowRange(projectile.weight)

                var distance = 0
                while (distance < range && nextCell.isPassable()) {
                    currentCell = nextCell
                    nextCell = currentCell.getCellTowards(dir)
                    val creature = currentCell.creature
                    if (creature != null)
                        if (throwAttack(player, projectile, creature))
                            break

                    distance++
                }
                currentCell.items.add(projectile)
                tick()
            }
        }
    }

    private fun throwAttack(attacker: Creature, projectile: Item, target: Creature): Boolean {
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

    fun movePlayer(direction: Direction) = gameAction {
        val cell = player.cell.getCellTowards(direction)
        val creatureInCell = cell.creature
        if (creatureInCell != null) {
            if (attack(player, creatureInCell))
                tick()
        } else if (cell.canMoveInto(player.corporeal)) {
            cell.enter(player)
            tick()
        } else {
            log.finer("Can't move towards: $direction")
        }
    }

    fun runTowards(direction: Direction) = gameAction {
        val target = player.cell.getCellTowards(direction)
        if (isInCorridor(target))
            runInCorridor(direction)
        else
            runInRoom(direction)
    }

    private fun isInCorridor(cell: Cell) =
        cell.countPassableMainNeighbours() == 2 && !cell.isRoomCorner()

    private fun runInCorridor(initialDirection: Direction) {
        val first = player.cell.getCellTowards(initialDirection)
        if (first.canMoveInto(player.corporeal)) {
            var previous = player.cell

            first.enter(player)
            tick()

            while (!player.cell.isInteresting(true)) {
                val nextCandidates = player.cell.adjacentCellsInMainDirections.filter {
                    c -> c != previous && c.canMoveInto(player.corporeal)
                }

                if (nextCandidates.size == 1) {
                    previous = player.cell
                    nextCandidates.first().enter(player)
                    tick()

                } else {
                    return
                }
            }
        }
    }

    private fun runInRoom(direction: Direction) {
        val first = player.cell.getCellTowards(direction)
        if (first.canMoveInto(player.corporeal)) {
            first.enter(player)
            tick()

            while (!player.cell.isInteresting(false)) {
                val target = player.cell.getCellTowards(direction)
                if (target.canMoveInto(player.corporeal)) {
                    val previous = player.cell
                    target.enter(player)
                    tick()

                    if (previous.countPassableMainNeighbours() != target.countPassableMainNeighbours())
                        break
                } else
                    break
            }
        }
    }

    private fun Cell.isInteresting(corridor: Boolean) =
        isInteresting() || player.seesNonFriendlyCreatures() || (corridor && countPassableMainNeighbours() > 2)

    fun movePlayerVertically(up: Boolean) = gameAction {
        val target = player.cell.getJumpTarget(up)
        if (target != null) {
            if (target.isExit) {
                if (ask("Really escape from the dungeon?"))
                    gameOver("Escaped the dungeon.")

            } else {
                log.fine("Found portal to target $target")
                enterRegion(target.region, target.location)
                tick()
            }
        } else {
            log.fine("No matching portal at current location")
        }
    }

    fun skipTurn() {
        if (!over)
            tick()
    }

    fun rest(maxTurns: Int) = gameAction {
        val startTime = globalClock.time
        if (maxTurns == -1 && player.hitPoints == player.maximumHitPoints) {
            player.message("You don't feel like you need to rest.")
        } else {

            player.message("Resting...")
            while (maxTurns == -1 || maxTurns < startTime - globalClock.time) {
                when {
                    player.hitPoints == player.maximumHitPoints -> {
                        player.message("You feel rested!")
                        break
                    }
                    player.getHungerLevel().hungry -> {
                        player.message("You wake up feeling hungry.")
                        break
                    }
                    player.seesNonFriendlyCreatures() -> {
                        player.message("Your rest is interrupted.")
                        break
                    }
                    else ->
                        tick()
                }
            }
        }
    }

    fun attack(attacker: Creature, target: Creature): Boolean {
        if (!target.alive)
            return false

        if (attacker.isPlayer && target.friendly)
            if (!ask("Really attack %s?", target.name))
                return false

        target.onAttackedBy(attacker)
        val weapon = attacker.attack
        val rollToHit = findRollToHit(attacker, weapon, target)
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

        log.fine("hit roll: $roll = 1 + %d + %d + %d + %d + %d - %d".format(attackerLuck, hitBonus, weaponToHit, proficiency, armorClass, targetLuck))

        return roll
    }

    private fun assignDamage(attacker: Creature, weapon: Attack, target: Creature) {
        val damage = weapon.getDamage(target)

        log.fine("rolled $damage hp damage from $weapon")

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
            } while (player.alive && player.fainted)

            currentRegion.updateLighting()
            currentRegion.updateSeenCells(player.visibleCells!!)

            listener()
        }
    }

    fun message(message: String, vararg args: Any?) {
        console.message(message.format(*args))
    }

    fun ask(question: String, vararg args: Any?): Boolean =
        console.ask(question.format(*args))

    fun gameOver(reason: String) {
        over = true
        if (!player.regenerated) {
            HighScoreService().saveGameScore(this, reason)
        }
    }

    override val time: Int
        get() = globalClock.time

    private fun gameAction(callback: () -> Unit) {
        if (!over) {
            if (!player.paralyzed) {
                callback()
            } else {
                message("You are paralyzed.")
            }
        }
    }

    class object {
        private val log = javaClass<Game>().logger()
    }
}
