package dev.komu.kraken.model

import dev.komu.kraken.common.Direction
import dev.komu.kraken.definitions.Creatures
import dev.komu.kraken.definitions.Items
import dev.komu.kraken.definitions.Weapons
import dev.komu.kraken.model.GameConfiguration.PetType
import dev.komu.kraken.model.actions.*
import dev.komu.kraken.model.common.Console
import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.creature.Player
import dev.komu.kraken.model.creature.pets.Doris
import dev.komu.kraken.model.creature.pets.Lassie
import dev.komu.kraken.model.creature.pets.Pet
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

    override var selectedCell: Coordinate? = null

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
                val pet = oldCell.findAdjacentPet()
                if (pet != null)
                    putPetNextToPlayer(pet)
            }

            regionClock.schedulePeriodic(500) {
                spawnRandomMonsters()
            }

            creatures += region.creatures

            selectedCell = null
        }
    }

    private fun Cell.findAdjacentPet(): Pet? {
        return adjacentCells
            .asSequence()
            .mapNotNull { it.creature as? Pet }
            .firstOrNull()
    }

    fun addCreature(creature: Creature, target: Cell) {
        creature.cell = target
        if (target.region == currentRegion) {
            creatures += creature
        }
    }

    fun talk() = gameAction {
        val adjacent = player.adjacentCreatures
        if (adjacent.size == 1) {
            TalkAction(adjacent.first(), player)
        } else if (adjacent.isEmpty()) {
            player.message("There's no-one to talk to.")
            null
        } else {
            val dir = console.selectDirection()
            if (dir != null) {
                val creature = player.cell.getCellTowards(dir).creature
                if (creature != null) {
                    TalkAction(adjacent.first(), player)
                } else {
                    player.message("There's nobody in selected direction.")
                    null
                }
            } else {
                null
            }
        }
    }

    fun openDoor() = gameAction {
        val closedDoors = player.cell.adjacentCells.filter { it.cellType == CellType.CLOSED_DOOR }
        if (closedDoors.isEmpty()) {
            player.message("There are no closed doors around you.")
            null
        } else if (closedDoors.size == 1) {
            OpenDoorAction(closedDoors[0], player)
        } else {
            val dir = console.selectDirection()
            if (dir != null) {
                val cell = player.cell.getCellTowards(dir)
                if (cell.cellType == CellType.CLOSED_DOOR) {
                    OpenDoorAction(cell, player)
                } else {
                    player.message("No closed door in selected direction.")
                    null
                }
            } else {
                null
            }
        }
    }

    fun closeDoor() = gameAction {
        val openDoors = player.cell.adjacentCells.filter { it.cellType == CellType.OPEN_DOOR }
        if (openDoors.isEmpty()) {
            player.message("There are no open doors around you.")
            null
        } else if (openDoors.size == 1) {
            CloseDoorAction(openDoors[0], player)
        } else {
            val dir = console.selectDirection()
            if (dir != null) {
                val cell = player.cell.getCellTowards(dir)
                if (cell.cellType == CellType.OPEN_DOOR)
                    CloseDoorAction(cell, player)
                else {
                    player.message("No open door in selected direction.")
                    null
                }
            } else {
                null
            }
        }
    }

    fun pickup() = gameAction {
        val cell = player.cell
        val items = cell.items
        when {
            items.isEmpty() -> {
                player.message("There's nothing to pick up."); null
            }
            items.size == 1 -> PickupAction(items.first(), player)
            else ->
                console.selectItem("Select item to pick up", items)?.let { PickupAction(it, player) }
        }
    }

    fun equip() = gameAction {
        console.selectItem("Select item to equip", player.inventory.byType<Equipable>())
            ?.let { EquipAction(it, player) }
    }

    fun drop() = gameAction {
        console.selectItem("Select item to drop", player.inventory.items)?.let { DropAction(it, player) }
    }

    fun drop(item: Item) = gameAction {
        DropAction(item, player)
    }

    fun eat() = gameAction {
        console.selectItem("Select food to eat", player.inventory.byType<Food>())?.let { EatAction(it, player) }
    }

    fun search() = gameAction {
        SearchAction(player)
    }

    fun throwItem() = gameAction {
        val projectile = console.selectItem("Select item to throw", player.inventory.byType<Item>())
        if (projectile != null) {
            console.selectDirection()?.let { ThrowAction(projectile, it, player) }
        } else {
            null
        }
    }

    override val currentRegionOrNull: Region?
        get() = player.cellOrNull?.region

    private val currentRegion: Region
        get() = player.region

    fun movePlayer(direction: Direction) = gameAction {
        val creatureInCell = player.cell.getCellTowards(direction).creature
        if (creatureInCell != null) {
            if (creatureInCell.isAlive && (!creatureInCell.friendly || ask("Really attack %s?", creatureInCell.name)))
                AttackAction(creatureInCell, player)
            else
                null

        } else {
            MoveAction(player, direction)
        }
    }

    fun runTowards(direction: Direction) = behave {
        val target = player.cell.getCellTowards(direction)
        if (target.isInCorridor)
            RunInCorridorBehavior(direction)
        else
            RunInRoomBehavior(direction)
    }

    fun movePlayerVertically(up: Boolean) = gameAction {
        val target = player.cell.getJumpTarget(up)
        if (target != null) {
            if (target.isExit) {
                if (ask("Really escape from the dungeon?"))
                    gameOver("Escaped the dungeon.")
                null
            } else {
                EnterRegionAction(this, target.region, target.location)
            }
        } else {
            null
        }
    }

    fun skipTurn() {
        gameAction {
            SkipAction
        }
    }

    fun rest() = behave {
        if (player.hitPoints == player.maximumHitPoints) {
            player.message("You don't feel like you need to rest.")
            null
        } else {
            RestBehavior
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

    private fun gameAction(callback: () -> Action?) {
        behave { callback()?.let { ActionBehavior(it) } }
    }

    private fun behave(callback: () -> Behavior?) {
        if (!over) {
            player.behavior = callback()

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

        val invisibleCells = player.getInvisibleCells()
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
