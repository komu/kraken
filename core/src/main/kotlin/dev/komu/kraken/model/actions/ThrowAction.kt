package dev.komu.kraken.model.actions

import dev.komu.kraken.common.Direction
import dev.komu.kraken.model.common.assignDamage
import dev.komu.kraken.model.common.findRollToHit
import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.creature.Player
import dev.komu.kraken.model.item.Item
import dev.komu.kraken.utils.rollDie

class ThrowAction(private val projectile: Item, private val direction: Direction, private val player: Player) : Action {
    override fun perform(): ActionResult {
        player.inventory.remove(projectile)
        var currentCell = player.cell
        var nextCell = currentCell.getCellTowards(direction)
        val range = getThrowRange(projectile.weight)

        var distance = 0
        while (distance < range && nextCell.isPassable) {
            currentCell = nextCell
            nextCell = currentCell.getCellTowards(direction)
            val creature = currentCell.creature
            if (creature != null)
                if (throwAttack(player, projectile, creature))
                    break

            distance++
        }
        currentCell.items.add(projectile)

        return ActionResult.Success
    }

    private fun throwAttack(attacker: Creature, projectile: Item, target: Creature): Boolean {
        target.onAttackedBy(attacker)
        val rollToHit = findRollToHit(attacker, projectile, target)
        return if (rollDie(20) <= rollToHit) {
            attacker.message("%s %s %s at %s.", attacker.You(), attacker.verb("throw"), projectile.title, target.you())
            target.message("%s %s %s at %s.", attacker.You(), attacker.verb("throw"), projectile.title, target.you())
            assignDamage(attacker, projectile, target)
            attacker.onSuccessfulHit(target, projectile)
            if (!target.isAlive)
                attacker.onKilledCreature(target)

            true
        } else {
            attacker.message("%s flies past %s.", projectile.title, target.name)
            target.message("%s flies past %s.", projectile.title, target.name)
            false
        }
    }

    // TODO: take strength of thrower into account
    private fun getThrowRange(weight: Int): Int = when {
        weight < 1000  -> 30
        weight < 2000  -> 20
        weight < 3000  -> 15
        weight < 5000  -> 10
        weight < 10000 -> 8
        weight < 15000 -> 5
        weight < 20000 -> 3
        weight < 25000 -> 2
        weight < 50000 -> 1
        else           -> 0
    }
}
