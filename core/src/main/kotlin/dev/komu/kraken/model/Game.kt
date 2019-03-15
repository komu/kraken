package dev.komu.kraken.model

import dev.komu.kraken.common.Direction
import dev.komu.kraken.definitions.Creatures
import dev.komu.kraken.definitions.Items
import dev.komu.kraken.definitions.Weapons
import dev.komu.kraken.model.GameConfiguration.PetType
import dev.komu.kraken.model.actions.*
import dev.komu.kraken.model.common.Console
import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.creature.Monster
import dev.komu.kraken.model.creature.PetState
import dev.komu.kraken.model.creature.Player
import dev.komu.kraken.model.creature.pets.Doris
import dev.komu.kraken.model.creature.pets.Lassie
import dev.komu.kraken.model.item.Equipable
import dev.komu.kraken.model.item.Item
import dev.komu.kraken.model.item.ItemInfo
import dev.komu.kraken.model.item.food.Food
import dev.komu.kraken.model.region.*
import dev.komu.kraken.service.config.ObjectFactory
import dev.komu.kraken.utils.MaximumCounter
import dev.komu.kraken.utils.isFestivus
import java.time.DayOfWeek
import java.time.LocalDate

class Game(val config: GameConfiguration, private val console: Console, val listener: () -> Unit) : ReadOnlyGame {
    val globalClock = Clock()
    private val regionClock = Clock()
    private val creatures = mutableListOf<Creature>()
    private var currentCreature = -1

    val player = Player(config.name)
    private val world = World(this)

    val objectFactory = ObjectFactory().apply {
        addDefinitions(Weapons)
        addDefinitions(Items)
        addDefinitions(Creatures)
    }

    private val maximumDungeonLevel = MaximumCounter(0)
    var over = false

    override val inventoryItems: List<ItemInfo>
        get() {
            val infos = mutableListOf<ItemInfo>()

            player.activatedItems.mapTo(infos) {
                ItemInfo(it.title, it.description, it.letter, inUse = true)
            }

            player.inventory.items.mapTo(infos) {
                ItemInfo(it.title, it.description, it.letter, inUse = false)
            }

            return infos
        }

    override val statistics: GameStatistics
        get() = GameStatistics(player, globalClock.time)

    override val visibleCells: CellSet
        get() = player.visibleCells

    fun revealCurrentRegion() {
        currentRegion.reveal()
    }

    fun start() {
        player.wieldedWeapon = Weapons.dagger.create()
        player.inventory.add(Items.foodRation.create())
        player.inventory.add(Items.cyanideCapsule.create())
        enterRegion("start", "from up")

        if (config.pet == PetType.DORIS)
            putPetNextToPlayer(Doris("Doris"))
        else if (config.pet == PetType.LASSIE)
            putPetNextToPlayer(Lassie("Lassie"))

        currentRegion.updateLighting()
        player.updateVisiblePoints()
        currentRegion.updateSeenCells(player.visibleCells)
        player.message("Hello %s, welcome to Kraken!", player.name)

        val today = LocalDate.now()
        if (today.isFestivus) {
            player.message("Happy Festivus!")
            player.strength += 10
            player.luck = 2
            player.inventory.add(Weapons.aluminiumPole.create())
        }

        if (today.dayOfWeek == DayOfWeek.FRIDAY) {
            player.message("It is Friday, good luck!")
            player.luck = 1
        }

        globalClock.schedulePeriodic(1) {
            player.increaseHungriness(this)
        }
        globalClock.schedulePeriodic(50) {
            player.regainHitPoint()
        }
    }

    private fun putPetNextToPlayer(pet: Creature) {
        val target = player.cell.adjacentCells.find { it.isFloor && it.creature == null }

        if (target != null) {
            pet.cell = target
            creatures += pet
        }
    }

    override val cellInFocus: Coordinate
        get() = player.cell.coordinate

    fun enterRegion(name: String, location: String) {
        val region = world.getRegion(name)
        val oldCell = player.cellOrNull
        val oldRegion = oldCell?.region
        region.setPlayerLocation(player, location)
        if (region != oldRegion) {
            regionClock.clear()
            creatures.clear()
            creatures += player
            currentCreature = 0

            maximumDungeonLevel.update(region.level)
            if (oldCell != null) {
                // TODO: perhaps all adjacent creatures could come with player?
                val pet = oldCell.adjacentCells
                    .mapNotNull { it.creature as? Monster }
                    .find { it.state is PetState }
                if (pet != null)
                    putPetNextToPlayer(pet)
            }

            regionClock.schedulePeriodic(500) {
                spawnRandomMonsters()
            }

            creatures += region.creatures
        }
    }

    private fun addCreature(creature: Creature, target: Cell) {
        creature.cell = target
        if (target.region == currentRegion)
            creatures += creature
    }

    fun talk() {
        val adjacent = player.adjacentCreatures
        if (adjacent.size == 1) {
            act(TalkAction(adjacent.first(), player))
        } else if (adjacent.isEmpty()) {
            player.message("There's no-one to talk to.")
        } else {
            val dir = console.selectDirection()
            if (dir != null) {
                val creature = player.cell.getCellTowards(dir).creature
                if (creature != null) {
                    act(TalkAction(adjacent.first(), player))
                } else {
                    player.message("There's nobody in selected direction.")
                }
            }
        }
    }

    fun openDoor() {
        val closedDoors = player.cell.adjacentCells.filter { it.type == CellType.CLOSED_DOOR }
        if (closedDoors.isEmpty()) {
            player.message("There are no closed doors around you.")
        } else if (closedDoors.size == 1) {
            act(OpenDoorAction(closedDoors[0], player))
        } else {
            val dir = console.selectDirection()
            if (dir != null) {
                val cell = player.cell.getCellTowards(dir)
                if (cell.type == CellType.CLOSED_DOOR) {
                    act(OpenDoorAction(cell, player))
                } else {
                    player.message("No closed door in selected direction.")
                }
            }
        }
    }

    fun closeDoor() {
        val openDoors = player.cell.adjacentCells.filter { it.type == CellType.OPEN_DOOR }
        if (openDoors.isEmpty()) {
            player.message("There are no open doors around you.")
        } else if (openDoors.size == 1) {
            act(CloseDoorAction(openDoors[0], player))
        } else {
            val dir = console.selectDirection()
            if (dir != null) {
                val cell = player.cell.getCellTowards(dir)
                if (cell.type == CellType.OPEN_DOOR)
                    act(CloseDoorAction(cell, player))
                else {
                    player.message("No open door in selected direction.")
                }
            }
        }
    }

    fun pickup() {
        val cell = player.cell
        val items = cell.items
        when {
            items.isEmpty() ->
                player.message("There's nothing to pick up.")
            items.size == 1 ->
                act(PickupAction(items.first(), player))
            else ->
                console.selectItem("Select item to pick up", items)?.let { act(PickupAction(it, player)) }
        }
    }

    fun equip() {
        console.selectItem("Select item to equip", player.inventory.byType<Equipable>())
            ?.let { act(EquipAction(it, player)) }
    }

    fun drop() {
        console.selectItem("Select item to drop", player.inventory.items)?.let { act(DropAction(it, player)) }
    }

    fun drop(item: Item) {
        act(DropAction(item, player))
    }

    fun eat() {
        console.selectItem("Select food to eat", player.inventory.byType<Food>())?.let { act(EatAction(it, player)) }
    }

    fun search() {
        act(SearchAction(player))
    }

    fun throwItem() {
        val projectile = console.selectItem("Select item to throw", player.inventory.byType<Item>())
        if (projectile != null) {
            console.selectDirection()?.let { act(ThrowAction(projectile, it, player)) }
        }
    }

    override val currentRegionOrNull: Region?
        get() = player.cellOrNull?.region

    private val currentRegion: Region
        get() = player.region

    fun movePlayer(direction: Direction) {
        val creatureInCell = player.cell.getCellTowards(direction).creature
        if (creatureInCell != null) {
            if (creatureInCell.isAlive && (!creatureInCell.isFriendly || ask("Really attack %s?", creatureInCell.name)))
                act(AttackAction(creatureInCell, player))

        } else {
            act(MoveAction(player, direction))
        }
    }

    fun runTowards(direction: Direction) {
        val target = player.cell.getCellTowards(direction)
        if (target.isInCorridor)
            behave(RunInCorridorBehavior(direction))
        else
            behave(RunInRoomBehavior(direction))
    }

    fun movePlayerVertically(up: Boolean) {
        val target = player.cell.getJumpTarget(up)
        if (target != null) {
            if (target.isExit) {
                if (ask("Really escape from the dungeon?"))
                    gameOver("Escaped the dungeon.")
            } else {
                act(EnterRegionAction(this, target.region, target.location))
            }
        }
    }

    fun skipTurn() {
        act(SkipAction)
    }

    fun rest() {
        if (player.hitPoints == player.maximumHitPoints) {
            player.message("You don't feel like you need to rest.")
        } else {
            behave(RestBehavior)
        }
    }

    fun message(message: String, vararg args: Any?) {
        console.message(message.format(*args))
    }

    fun ask(question: String, vararg args: Any?): Boolean =
        console.ask(question.format(*args))

    fun gameOver(reason: String) {
        over = true
    }

    private fun act(action: Action) {
        behave(ActionBehavior(action))
    }

    private fun behave(behavior: Behavior?) {
        player.behavior = behavior

        while (player.isAlive && creatures.isNotEmpty()) {
            val creature = creatures[currentCreature]
            if (!creature.isAlive) {
                creatures.removeAt(currentCreature)
                currentCreature -= 1
                continue
            }

            if (creature.energy.canTakeTurn) {
                if (creature == player && player.needsInput)
                    break

                creature.energy.spend()
                val action = creature.getAction(this)
                if (action != null) {
                    val success = perform(action)
                    if (!success && creature == player)
                        break
                }
            }

            creature.energy.gain(creature.speed)

            currentRegion.updateLighting()
            player.updateVisiblePoints()
            currentRegion.updateSeenCells(player.visibleCells)

            listener()

            currentCreature = (currentCreature + 1) % creatures.size

            if (currentCreature == 0) {
                globalClock.tick()
                regionClock.tick()
            }
        }
    }

    private tailrec fun perform(action: Action): Boolean {
        val result = action.perform()
        return when (result) {
            ActionResult.Success ->
                true
            ActionResult.Failure ->
                false
            is ActionResult.Alternate ->
                perform(result.action)
        }
    }

    private fun spawnRandomMonsters() {
        val creatures = objectFactory.randomSwarm(player.region.level, player.level)

        val invisibleCells = player.invisibleCells
        val regionCells = player.region.getCells()
        for (creature in creatures) {
            val target = invisibleCells.selectRandomTargetCell(creature) ?: regionCells.selectRandomTargetCell(creature)
            if (target != null)
                addCreature(creature, target)
        }
    }

    private fun CellSet.selectRandomTargetCell(creature: Creature): Cell? =
        randomCellMatching { it.canMoveInto(creature.corporeal) }
}
