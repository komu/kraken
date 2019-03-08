package dev.komu.kraken.model.events.region

import dev.komu.kraken.model.Game
import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.creature.Player
import dev.komu.kraken.model.region.Cell
import dev.komu.kraken.model.region.CellSet
import dev.komu.kraken.utils.logger

object CreateMonstersEvent {

    private val log = javaClass.logger()

    fun createMonsters(game: Game) {
        val player = game.player
        val creatures = game.objectFactory.randomSwarm(player.region.level, player.level)
        log.fine("Created new random creatures: $creatures")

        for (creature in creatures) {
            val target = findTargetCell(player, creature)
            if (target != null)
                game.addCreature(creature, target)
            else
                log.warning("No empty cells available, creature not added.")
        }
    }

    private fun findTargetCell(player: Player, creature: Creature) =
        selectRandomTargetCell(player.getInvisibleCells(), creature) ?: selectRandomTargetCell(player.region.getCells(), creature)

    private fun selectRandomTargetCell(candidates: CellSet, creature: Creature): Cell? {
        var tries = 0
        while (candidates.isNotEmpty() && tries++ < 100) {
            val cell = candidates.randomElement()
            if (cell.creature == null && cell.canMoveInto(creature.corporeal))
                return cell
        }
        return null
    }
}
