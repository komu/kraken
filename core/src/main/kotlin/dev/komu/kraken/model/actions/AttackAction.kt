package dev.komu.kraken.model.actions

import dev.komu.kraken.model.assignDamage
import dev.komu.kraken.model.creature.Creature
import dev.komu.kraken.model.findRollToHit
import dev.komu.kraken.utils.rollDie

class AttackAction(private val target: Creature, private val attacker: Creature) : Action {
    override fun perform(): ActionResult {
        if (!target.alive)
            return ActionResult.Failure

        target.onAttackedBy(attacker)
        val weapon = attacker.attack
        val rollToHit = findRollToHit(attacker, weapon, target)
        if (rollDie(20) <= rollToHit) {
            attacker.message("%s %s %s.", attacker.You(), attacker.verb(weapon.attackVerb), target.you())
            target.message("%s %s %s.", attacker.You(), attacker.verb(weapon.attackVerb), target.you())
            assignDamage(attacker, weapon, target)
            attacker.onSuccessfulHit(target, weapon)
            if (!target.alive)
                attacker.onKilledCreature(target)
        } else {
            attacker.message("%s %s.", attacker.You(), attacker.verb("miss"))
            target.message("%s %s.", attacker.You(), attacker.verb("miss"))
        }

        return ActionResult.Success
    }
}
