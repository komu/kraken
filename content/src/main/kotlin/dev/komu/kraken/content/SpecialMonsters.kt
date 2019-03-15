package dev.komu.kraken.content

import dev.komu.kraken.model.Direction
import dev.komu.kraken.model.Energy
import dev.komu.kraken.model.Game
import dev.komu.kraken.model.actions.Action
import dev.komu.kraken.model.actions.AttackAction
import dev.komu.kraken.model.actions.RandomMoveAction
import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.creature.DefaultMonsterState
import dev.komu.kraken.model.creature.Monster
import dev.komu.kraken.model.creature.MonsterState
import dev.komu.kraken.model.item.Item
import dev.komu.kraken.model.region.Cell
import dev.komu.kraken.utils.randomElement
import dev.komu.kraken.utils.rollDie

/**
 * Knight of Ni - like a normal monster, but yells scary things at player.
 */
object KnightOfNiMonsterState : DefaultMonsterState() {

    override fun act(self: Monster, game: Game): Pair<Action?, MonsterState> {
        val player = game.player
        if (self.seesCreature(player))
            when (rollDie(20)) {
                0, 1, 2 -> player.say(self, "Ni!")
                3       -> player.say(self, "Noo!")
                else    -> {  }
            }

        return super.act(self, game)
    }
}

/**
 * Bugs Bunny - hops around and moves player.
 */
object BugsBunnyState : MonsterState {
    override fun talk(self: Monster, target: Creature) {
        target.say(self, "What's up, Doc?")
    }

    override fun act(self: Monster, game: Game): Pair<Action?, MonsterState> {
        if (self.seesCreature(game.player))
            game.movePlayer(Direction.randomDirection())

        return RandomMoveAction(self) to this
    }
}

/**
 * Black Knight - never dies and taunts player relentlessly.
 */
class BlackKnightState : MonsterState {

    private var hasBeenFighting = false

    override fun act(self: Monster, game: Game): Pair<Action?, MonsterState> {
        val player = game.player

        val isAdjacent = self.isAdjacentToCreature(player)

        if (hasBeenFighting && !isAdjacent) {
            player.say(self, PLAYER_FLEEING_YELLS.randomElement())
            hasBeenFighting = false

        } else if (isAdjacent) {
            talk(self, player)
        }

        return when {
            isAdjacent -> {
                hasBeenFighting = true
                AttackAction(player, self)
            }
            self.fullyCrippled ->
                null
            else ->
                self.lastKnownPlayerPosition?.let(self::moveTowardsAction)
        } to this
    }

    override fun didTakeDamage(self: Monster, points: Int, attacker: Creature) {
        hasBeenFighting = true
        self.hitPoints = self.hitPoints.coerceAtLeast(1) // never die
        if (self.fullyCrippled)
            return

        self.baseSpeed = (self.baseSpeed - 1).coerceAtLeast(Energy.MIN_SPEED)
        if (self.fullyCrippled) {
            attacker.message("The Black Knight is crippled!")
            val weapon = self.wieldedWeapon
            if (weapon != null) {
                self.wieldedWeapon = null
                self.dropToAdjacentCell(weapon)
            }
        } else {
            attacker.message("The Black Knight loses a limb.")
        }
    }

    override fun talk(self: Monster, target: Creature) {

        val yells = when (self.hitPointPercentage) {
            in 0..20 -> TORSO_YELLS
            in 21..40 -> ONE_LEGGED_YELLS
            in 41..60 -> ARMLESS_YELLS
            in 61..80 -> ONE_ARMED_YELLS
            else -> HEALTHY_YELLS
        }

        target.say(self, yells.randomElement())
    }

    companion object {

        private fun Monster.dropToAdjacentCell(item: Item) {
            val target = cell.adjacentCells.shuffled().find(Cell::canDropItemToCell) ?: cell
            target.items += item
        }

        private val Monster.hitPointPercentage: Int
            get() = 100 * hitPoints / maximumHitPoints

        private val Monster.fullyCrippled: Boolean
            get() = hitPointPercentage < 20

        private val HEALTHY_YELLS = listOf("None shall pass.", "I move for no man.", "Aaaagh!")
        private val ONE_ARMED_YELLS = listOf("Tis but a scratch.", "I've had worse.", "Come on, you pansy!")
        private val ARMLESS_YELLS = listOf("Come on, then.", "Have at you!")
        private val ONE_LEGGED_YELLS = listOf("Right. I'll do you for that!", "I'm invincible!")
        private val TORSO_YELLS = listOf("Oh. Oh, I see. Running away, eh?", "You yellow bastard!", "Come back here and take what's coming to you.", "I'll bite your legs off!")
        private val PLAYER_FLEEING_YELLS = listOf("Oh, had enough, eh?", "Just a flesh wound.", "Chicken! Chickennn!")
    }
}

/**
 * Oracle - says interesting things to player
 */
object OracleState : MonsterState {
    override fun act(self: Monster, game: Game): Pair<Action?, MonsterState> =
        null to this

    override val isFriendly: Boolean
        get() = true

    override fun talk(self: Monster, target: Creature) {
        target.say(self, wisdoms.randomElement())
    }

    private val wisdoms = listOf(
        "Beauty is in the eye of beholder.",
        "They say that no ordinary shovel can dig into Exceptionally Hard Rock.",
        "Only the light of Graal can defeat the ultimate darkness.",
        "They say that Festivus is the day to start your adventure.",
        "They say that The Founders are there on Friday.",
        "The deaf are not afraid of the Ni.",
        "All names have a meaning.",
        "How small a rock needs to be in order to float on water?",
        "Beholders tend to feel uneasy around spoons.",
        "Chain smoking might be good for you.",
        "The Siamese bats are more dangerous.",
        "There is no fork.",
        "Enlarge your shield.",
        "They say a lot of things.",
        "Don't believe everything you are told.",
        "This sentence is a lie.",
        "This sentence is true, but can't be proved.",
        "What's the value of x in the following sequence? 1, 2, 720!, x, ...",
        "How many roads must a man walk down?",
        "Attributes are the first step on the road that leads to the Dark Side."
    )
}
